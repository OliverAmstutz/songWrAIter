package ch.zuehlke.fullstack.hackathon.model;

public record BertPromptDto(    String chords,
                                String notes,
                                int tempo,
                                int seed,
                                int sample_width,
                                int time_signature) {

}