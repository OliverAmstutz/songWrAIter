package ch.zuehlke.fullstack.hackathon.model.musicgen;

import java.util.Date;

public record MusicgenResponseDto(
        String id,
        String model,
        String version,
        ModelOutput input,
        String logs,
        String error,
        String status,
        Date created_at,
        Urls urls) {

}
