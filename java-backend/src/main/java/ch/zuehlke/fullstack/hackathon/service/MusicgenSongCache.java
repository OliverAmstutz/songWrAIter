package ch.zuehlke.fullstack.hackathon.service;

import ch.zuehlke.fullstack.hackathon.model.musicgen.MusicgenSong;
import org.springframework.stereotype.Repository;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MusicgenSongCache {

    private final Map<UUID, MusicgenSong> songs = new ConcurrentHashMap<>();

    public List<MusicgenSong> getAllSongs() {
        return songs
                .values()
                .stream()
                .toList();
    }

    public void addNewSong(MusicgenSong song) {
        songs.put(song.id(), song);
    }

    public void updateSong(MusicgenSong song, URL url) {
        MusicgenSong updatedSong = new MusicgenSong(song, url);
        songs.put(song.id(), updatedSong);
    }
}
