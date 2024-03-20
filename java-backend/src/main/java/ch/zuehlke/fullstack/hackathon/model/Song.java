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
        String imageUrl,
        LocalDateTime lastTimeUpdated
) {

    public Song(UUID id, String topic, Genre genre, List<String> instruments, String mood, String generatedVerseText, String generatedChorusText) {
        this(id, topic, genre, instruments, mood, null, generatedVerseText, generatedChorusText, null, null, null, null);
    }

    public Song bertId(String bertId) {
        return new Song(id, topic, genre, instruments, mood, bertId, generatedVerseText, generatedChorusText, bertUrls, musicGenUrls, imageUrl, lastTimeUpdated);
    }

    public Song bertUrls(SongUrls songUrls) {
        return new Song(id, topic, genre, instruments, mood, bertId, generatedVerseText, generatedChorusText, songUrls, musicGenUrls, imageUrl, lastTimeUpdated);
    }

    public Song musicGenUrls(SongUrls songUrls) {
        return new Song(id, topic, genre, instruments, mood, bertId, generatedVerseText, generatedChorusText, bertUrls, songUrls, imageUrl, lastTimeUpdated);
    }

    public Song imageUrl(String imageUrl){
        return new Song(id, topic, genre, instruments, mood, bertId, generatedVerseText, generatedChorusText, bertUrls, musicGenUrls, imageUrl, lastTimeUpdated);
    }

    public Song lastTimeUpdated(LocalDateTime dateTime) {
        return new Song(id, topic, genre, instruments, mood, bertId, generatedVerseText, generatedChorusText, bertUrls, musicGenUrls, imageUrl, dateTime);
    }
}