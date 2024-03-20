package ch.zuehlke.fullstack.hackathon.model.musicgen;

import java.net.URL;
import java.util.List;
import java.util.UUID;

public record MusicgenSong(
        UUID id,
        String genre,
        String prompt,
        List<String> chordProgression,
        String artist,
        String beatsPerMinute,
        String timeSignature,
        String mood,
        List<String> instruments,
        URL url) {

    public MusicgenSong(MusicgenSong song, URL url) {
        this(
                song.id,
                song.genre,
                song.prompt,
                song.chordProgression,
                song.artist,
                song.beatsPerMinute,
                song.timeSignature,
                song.mood,
                song.instruments,
                url);
    }
}
