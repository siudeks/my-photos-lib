package net.siudek.media;

import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.SneakyThrows;

@SpringBootApplication
public class Program {


  @SneakyThrows
	public static void main(String[] args) {
		SpringApplication.run(Program.class, args);
	}

  sealed interface Abc {

    record Aaa() implements Abc {

    }
  }

}
