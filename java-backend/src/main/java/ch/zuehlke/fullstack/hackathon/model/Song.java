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

    public Song(UUID id, String topic, String genre, List<String> instruments, String mood, String bertId, String generatedVerseText, String generatedChorusText) {
        this(id, topic, genre, instruments, mood, bertId, generatedVerseText, generatedChorusText, null);
    }
}