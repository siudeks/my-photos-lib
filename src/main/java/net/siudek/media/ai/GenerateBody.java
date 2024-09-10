package net.siudek.media.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateBody {
  String model;
  String prompt;
  boolean stream;
  String[] images;
}
