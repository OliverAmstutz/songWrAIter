package ch.zuehlke.fullstack.hackathon.model;

public record DallePromptInput(String model, String prompt, int n, String size) {
}