package ch.zuehlke.fullstack.hackathon.service;

import ch.zuehlke.fullstack.hackathon.model.Song;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SongCache {

    private final Map<UUID, Song> songs = new ConcurrentHashMap<>();

    public List<Song> getAllSongs() {
        return songs
                .values()
                .stream()
                .toList();
    }

    public void addNewSong(Song song) {
        songs.put(song.id(), song);
    }

    public void updateSong(Song song) {
        songs.put(song.id(), song);
    }
}