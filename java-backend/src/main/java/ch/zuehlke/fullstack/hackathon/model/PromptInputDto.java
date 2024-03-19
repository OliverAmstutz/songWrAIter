package ch.zuehlke.fullstack.hackathon.model;

import lombok.Builder;

import java.util.List;

@Builder
public record PromptInputDto(String topic, String genre, List<String> instruments, String mood) {
}