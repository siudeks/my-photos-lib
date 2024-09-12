package net.siudek.media.domain;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FileInfoContributor implements InfoContributor {

  private final State state;

  public FileInfoContributor(State state) {
    this.state = state;
  }

  @Override
  public void contribute(Builder builder) {
    var details = new HashMap<String, Integer>();
    details.put("found", (Integer) null);
    details.put("processing", null);
    details.put("processed", null);

    builder.withDetail("files", details);
  }
 
}
