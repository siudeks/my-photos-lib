package net.siudek.media.ai;

/**
 * To define closed list of model used for work on images, we defined all of them in {@see Models} enum.
 */
public enum Models {

    /**
     * https://ollama.com/library/mxbai-embed-large/tags
     * https://www.mixedbread.ai/blog/mxbai-embed-large-v1
     */
    // MxbaiEmbedLarge("mxbai-embed-large:latest"),

    /** Used to convert images to its textual representation. */
    // https://ollama.com/library/llava/tags
    Llava_v16_p7b("llava:7b-v1.6"),

    /** Used to create embeddings based of textual representation of images. */
    // https://ollama.com/library/llama3.1/tags
    Llama_v31_p8b("llama3.1:8b"),

    mxbai_embed_large("mxbai-embed-large:335m-v1-fp16");

    public final String modelName;

    public final String nameAndTag;

    private Models(String nameAndTag) {
        String[] parts = nameAndTag.split(":");
        modelName = parts[0];
        this.nameAndTag = nameAndTag;
    }

    // Checks if requested model is already available for usage.
    // Used to fix the issue when we try to use a model, and it is downloded instead of being used immediately
    public static void assureModelsAvailable(OllamaPort.ListResult listResult) {
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
