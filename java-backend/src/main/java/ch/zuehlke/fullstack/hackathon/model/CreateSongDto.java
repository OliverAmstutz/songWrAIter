package ch.zuehlke.fullstack.hackathon.model;

public record CreateSongDto(
        String topic,
        String genre,
        String[] instruments,
        String mood
) {
}
