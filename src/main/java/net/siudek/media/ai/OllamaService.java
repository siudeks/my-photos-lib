package net.siudek.media.ai;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.siudek.media.ai.OllamaPort.EmbeddingsBody;
import net.siudek.media.domain.ImageDescService;

@Component
@RequiredArgsConstructor
class LlavaServiceImpl implements ImageDescService, Runnable, SmartLifecycle, AutoCloseable {

  private final OllamaPort ollamaService;
  private volatile boolean isRunning;
  @Delegate
  private final ExecutorService vExecutor = Executors.newVirtualThreadPerTaskExecutor();
  private final BlockingQueue<Command> commands = new LinkedBlockingQueue<>();

  @Override
  public void request(String jpgBase64, Consumer<String> responseHandler) {
    var cmd = new Command.DescribeImage(jpgBase64, responseHandler);
    commands.offer(cmd);
  }

  @Override
  public void asEmbeddings(String text, Consumer<double[]> responseHandler) {
    var cmd = new Command.GetEmbeddings(text, responseHandler);
    commands.offer(cmd);
  }

  @Override
  public void run() {
    while (true) {
      try {
        var event = commands.take();
        switch (event) {
          case Command.DescribeImage(var jpgBase64, var callback) -> execute(jpgBase64, callback);
          case Command.GetEmbeddings(var description, var callback) -> embeddings(description, callback);
        }

      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }

  }

  private void execute(String jpgBase64, Consumer<String> callback) {
    var llavaModelName = Models.Llava_v16_p7b.getNameAndTag();
    var response = ollamaService.generate(new GenerateBody(llavaModelName, "Describe the photo.", false, new String[] { jpgBase64 }));
    var responseText = response.response();
    callback.accept(responseText);
  }

  private void embeddings(String description, Consumer<double[]> callback) {
    Models.assureModelsAvailable(ollamaService.list());

    var modelName = Models.mxbai_embed_large.getNameAndTag();
    var response = ollamaService.embeddings(new EmbeddingsBody(modelName, description)).embedding();
    callback.accept(response);
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

    record GetEmbeddings(String description, Consumer<double[]> callback) implements Command {
    }

  }

}
