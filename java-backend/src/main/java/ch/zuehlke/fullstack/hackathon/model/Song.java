package ch.zuehlke.fullstack.hackathon.model;

import java.util.List;
import java.util.UUID;

public record Song(
        UUID id,
        String topic,
        String genre,
        List<String> instruments,
        String mood,
        String bertId,
        String generatedVerseText,
        String generatedChorusText,
        SongUrls urls
) {


    public Song(Song song, String bertId) {
        this(song.id, song.topic, song.genre, song.instruments, song.mood, bertId, song.generatedVerseText, song.generatedChorusText, null);
    }

    public Song(UUID id, String topic, String genre, List<String> instruments, String mood, String generatedVerseText, String generatedChorusText) {
        this(id, topic, genre, instruments, mood, null, generatedVerseText, generatedChorusText, null);
    }

    public Song(Song song, SongUrls songUrls) {
        this(song.id, song.topic, song.genre, song.instruments, song.mood, song.bertId, song.generatedVerseText, song.generatedChorusText, songUrls);
    }
}