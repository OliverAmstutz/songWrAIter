package ch.zuehlke.fullstack.hackathon.service.musicgenservice;

import ch.zuehlke.fullstack.hackathon.model.musicgen.MusicgenSong;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MusicgenSongCache {

    private final Map<UUID, MusicgenSong> songs = new ConcurrentHashMap<>();

}
