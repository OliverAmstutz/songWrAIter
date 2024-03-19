package ch.zuehlke.fullstack.hackathon.service;

import ch.zuehlke.fullstack.hackathon.model.Song;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class SongCache {

    private final Map<UUID, Song> songs = new HashMap<>();

    public List<Song> getAllSongs() {
        return songs
                .values()
                .stream()
                .toList();
    }

    public void addNewSong(Song song) {
        songs.put(song.id(), song);
    }
}