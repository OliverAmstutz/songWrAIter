package ch.zuehlke.fullstack.hackathon.model;

public record MusicGenPromptDto(
        int top_k,
        int top_p,
        String prompt,
        int duration,
        int temperature,
        boolean continuation,
        String model_version,
        String output_format,
        int continuation_start,
        boolean multi_band_diffusion,
        String normalization_strategy,
        int classifier_free_guidance
) {
    public MusicGenPromptDto(String prompt) {
        this(250, 0, prompt, 33, 1, false, "melody-large", "mp3", 0, false, "peak", 3);
    }
}