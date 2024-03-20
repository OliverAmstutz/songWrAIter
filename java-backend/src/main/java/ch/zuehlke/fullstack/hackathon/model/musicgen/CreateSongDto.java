package ch.zuehlke.fullstack.hackathon.model.musicgen;

import java.util.List;

public record CreateSongDto(
        String style,
        String toneScale,
        String dynamics,
        String mood,
        List<String> instruments) {
}
