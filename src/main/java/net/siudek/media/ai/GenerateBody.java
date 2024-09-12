package net.siudek.media.ai;

public record GenerateBody(String model, String prompt, boolean stream, String[] images) { }
