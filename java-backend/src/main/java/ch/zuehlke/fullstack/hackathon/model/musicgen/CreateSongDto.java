package ch.zuehlke.fullstack.hackathon.model.musicgen;

import java.util.List;

public record CreateSongDto(
        String title,
        String genre,
        List<String> chordProgression,
        String artist,
        String beatsPerMinute,
        String timeSignature,
        String mood,
        List<String> instruments) {
}
