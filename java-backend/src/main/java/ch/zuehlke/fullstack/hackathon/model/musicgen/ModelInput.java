package ch.zuehlke.fullstack.hackathon.model.musicgen;

public record ModelInput(
        String prompt,
        int top_k,
        int top_p,
        int temperature,
        int classifier_free_guidance,
        String model_version,
        String normalization_strategy,
        int duration
) {

}
