package ch.zuehlke.fullstack.hackathon.service;

import ch.zuehlke.fullstack.hackathon.model.Song;

import java.util.Map;
import java.util.UUID;

public interface SongSaver {

    void save(Map<UUID, Song> mapToSave);

    Map<UUID, Song> load();
}
