package ch.zuehlke.fullstack.hackathon.model;

import java.util.List;

public record ImageDto(long created, List<ImageUrlDto> data) {
}