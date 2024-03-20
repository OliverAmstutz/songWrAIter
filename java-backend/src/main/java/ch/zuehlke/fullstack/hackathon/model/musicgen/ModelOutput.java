package ch.zuehlke.fullstack.hackathon.model.musicgen;

public record ModelOutput(
        int classifier_free_guidance,
        int duration,
        String model_version,
        String normalization_strategy,
        String prompt,
        int temperature,
        int top_k,
        int top_p
) {
}
