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
        SongUrls urls
) {

    public Song(UUID id, String topic, String genre, List<String> instruments, String mood, String bertId) {
        this(id, topic, genre, instruments, mood, bertId, null);
    }
}