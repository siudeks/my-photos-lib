package net.siudek.media.ai;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import net.siudek.media.domain.ImageDescService;

@Component
class LlavaServiceImpl implements ImageDescService, Runnable, SmartLifecycle {

  @Value("${spring.ai.ollama.base-url}")
  String apiHost;

  private volatile boolean isRunning;
  private final ExecutorService vExecutor = Executors.newVirtualThreadPerTaskExecutor();
  private final BlockingQueue<Command> commands = new LinkedBlockingQueue<>();

  @Override
  public void request(String jpgBase64, Consumer<String> responseHandler) {
    var cmd = new Command.DescribeImage(jpgBase64, responseHandler);
    commands.offer(cmd);
  }

  @Override
  public void run() {
    while (true) {
      try {
        var event = commands.take();
        switch (event) {
          case Command.DescribeImage(var jpgBase64, var callback): {
            execute(jpgBase64, callback);
          }
        }

      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }

  }

  private void execute(String jpgBase64, Consumer<String> callback) {
    var restClient = RestClient.builder().baseUrl(apiHost).build();
    var adapter = RestClientAdapter.create(restClient);
    var factory = HttpServiceProxyFactory.builderFor(adapter).build();

    var ollamaService = factory.createClient(OllamaPort.class);

    Models.assureModelsAvailable(ollamaService.list());

    var llavaModelName = Models.Llava_v16_p7b.getNameAndTag();
    var response = ollamaService.generate(new GenerateBody(llavaModelName, "Describe the photo.", false, new String[] { jpgBase64 }));
    var responseText = response.response();
    callback.accept(responseText);
  }

  @Override
  public void start() {
    isRunning = true;
    vExecutor.submit(this);
  }

  @Override
  public void stop() {
    vExecutor.close();
    isRunning = false;
  }

  @Override
  public boolean isRunning() {
    return isRunning;
  }

  sealed interface Command {
    record DescribeImage(String jpgBase64, Consumer<String> callback) implements Command {
    }
  }
}
