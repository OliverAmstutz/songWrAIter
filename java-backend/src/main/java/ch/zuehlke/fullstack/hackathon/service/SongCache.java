package ch.zuehlke.fullstack.hackathon.service;

import ch.zuehlke.fullstack.hackathon.model.Song;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Repository
@RequiredArgsConstructor
public class SongCache {

    private final SongSaver songSaver;
    private Map<UUID, Song> songs = new ConcurrentHashMap<>();

    public List<Song> getAllSongs() {
        this.songs = songSaver.load();

        return this.songs
                .values()
                .stream()
                .filter(onlySongsNewerThanOneHour())
                .toList();
    }

    public Song getById(UUID id) {
        return songs.get(id);
    }

    private Predicate<Song> onlySongsNewerThanOneHour() {
        return song -> song.lastTimeUpdated() != null && song.lastTimeUpdated().isAfter(LocalDateTime.now().minusHours(1));
    }

    public void addNewSong(Song song) {
        this.songs = songSaver.load();
        Song toSave = updateTimeStamp(song);
        this.songs.put(toSave.id(), song);
        songSaver.save(this.songs);
    }

    private Song updateTimeStamp(final Song song) {
        return song.lastTimeUpdated(LocalDateTime.now());
    }

    public void updateSong(Song song) {
        this.songs = songSaver.load();
        Song overwriteSongWithAdaptedTimeStamp = song.lastTimeUpdated(LocalDateTime.now());
        this.songs.put(overwriteSongWithAdaptedTimeStamp.id(), overwriteSongWithAdaptedTimeStamp);
        songSaver.save(this.songs);
    }
}