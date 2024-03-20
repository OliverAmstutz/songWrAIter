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
        SongUrls bertUrls,
        SongUrls musicGenUrls,
        LocalDateTime lastTimeUpdated
) {


    public Song(Song song, String bertId) {
        this(song.id, song.topic, song.genre, song.instruments, song.mood, bertId, song.generatedVerseText, song.generatedChorusText, song.bertUrls, song.musicGenUrls, song.lastTimeUpdated);
    }

    public Song(UUID id, String topic, Genre genre, List<String> instruments, String mood, String generatedVerseText, String generatedChorusText) {
        this(id, topic, genre, instruments, mood, null, generatedVerseText, generatedChorusText, null, null, null);
    }

    public Song bertId(String bertId) {
        return new Song(id, topic, genre, instruments, mood, bertId, generatedVerseText, generatedChorusText, bertUrls, musicGenUrls, lastTimeUpdated);
    }

    public Song bertUrls(SongUrls songUrls) {
        return new Song(id, topic, genre, instruments, mood, bertId, generatedVerseText, generatedChorusText, songUrls, musicGenUrls, lastTimeUpdated);
    }

    public Song musicGenUrls(SongUrls songUrls) {
        return new Song(id, topic, genre, instruments, mood, bertId, generatedVerseText, generatedChorusText, bertUrls, songUrls, lastTimeUpdated);
    }

    public Song(Song song, LocalDateTime dateTime) {
        this(song.id, song.topic, song.genre, song.instruments, song.mood, song.bertId, song.generatedVerseText, song.generatedChorusText, song.bertUrls, song.musicGenUrls, dateTime);
    }
}