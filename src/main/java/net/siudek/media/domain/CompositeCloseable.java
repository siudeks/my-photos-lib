package net.siudek.media.domain;

import java.util.ArrayList;
import java.util.List;

import net.siudek.media.utils.SafeCloseable;

public final class CompositeCloseable implements SafeCloseable {

  private final List<AutoCloseable> closeables = new ArrayList<>();

  public <T extends AutoCloseable> T add(T item) {
    closeables.add(item);
    return item;
  }

  @Override
  public void close() {
    closeables.forEach(it -> {
      try {
        it.close();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }
  
}
