package ch.zuehlke.fullstack.hackathon.service;

import ch.zuehlke.fullstack.hackathon.model.CreateSongDto;
import ch.zuehlke.fullstack.hackathon.model.Song;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SongService {

    private Map<UUID, Song> songs = new HashMap<>();

    public List<Song> getAllSongs() {
        return songs
                .values()
                .stream()
                .toList();
    }

    public void addNewSong(CreateSongDto createSongDto) {
        UUID id = UUID.randomUUID();
        songs.put(id, new Song(
                id,
                createSongDto.topic(),
                createSongDto.genre(),
                createSongDto.instruments(),
                createSongDto.mood()));
    }
}
