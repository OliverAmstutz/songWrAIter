package ch.zuehlke.fullstack.hackathon.model;

import java.util.List;

public record CreateSongDto(
        String topic,
        String genre,
        List<String> instruments,
        String mood
) {
}
