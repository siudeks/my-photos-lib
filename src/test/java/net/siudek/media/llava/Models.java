package net.siudek.media.llava;

import lombok.Getter;

public enum Models {

    /**
     * https://ollama.com/library/mxbai-embed-large/tags
     * https://www.mixedbread.ai/blog/mxbai-embed-large-v1
     */
    // MxbaiEmbedLarge("mxbai-embed-large:latest"),

    // https://ollama.com/library/llava/tags
    Llava("llava:34b-v1.6"),

    // https://ollama.com/library/llama3.1/tags
    Llama("llama3.1:70b-instruct-q4_0");

    @Getter
    private final String modelName;
    private final String nameAndTag;

    private Models(String nameAndTag) {
        String[] parts = nameAndTag.split(":");
        modelName = parts[0];
        this.nameAndTag = nameAndTag;
    }

    // Checks if requested model is already available for usage.
    // Used to fix the issue when we try to use a model, and it is downloded instead of being used immediately
    static void assureModelsAvailable(OllamaPort.ListResult listResult) {
        var availableModels = listResult.models().stream()
                .map(it -> it.name())
                .toList();
        for (var model : Models.values()) {
            var modelName = model.nameAndTag;
            if (!availableModels.contains(modelName)) {
                throw new IllegalStateException("Model: " + modelName
                        + " does not exist in local Ollama instance. Available models: " + availableModels);
            }
        }
    }
}
