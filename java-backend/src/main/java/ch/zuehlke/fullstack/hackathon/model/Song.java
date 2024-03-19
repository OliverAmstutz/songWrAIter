package ch.zuehlke.fullstack.hackathon.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Song(
        UUID id,
        String topic,
        Genre genre,
        List<String> instruments,
        String mood,
        String bertId,
        String generatedVerseText,
        String generatedChorusText,
        SongUrls urls,
        LocalDateTime lastTimeUpdated
) {


    public Song(Song song, String bertId) {
        this(song.id, song.topic, song.genre, song.instruments, song.mood, bertId, song.generatedVerseText, song.generatedChorusText, null, song.lastTimeUpdated);
    }

    public Song(UUID id, String topic, Genre genre, List<String> instruments, String mood, String generatedVerseText, String generatedChorusText) {
        this(id, topic, genre, instruments, mood, null, generatedVerseText, generatedChorusText, null, null);
    }

    public Song bertId(String bertId) {
        return new Song(id, topic, genre, instruments, mood, bertId, generatedVerseText, generatedChorusText, null, lastTimeUpdated);
    }

    public Song urls(SongUrls songUrls) {
        return new Song(id, topic, genre, instruments, mood, bertId, generatedVerseText, generatedChorusText, songUrls, lastTimeUpdated);
    }

    // overwriteTimeStamp
    public Song(Song song, LocalDateTime dateTime) {
        this(song.id, song.topic, song.genre, song.instruments, song.mood, song.bertId, song.generatedVerseText, song.generatedChorusText, song.urls, dateTime);
    }
}