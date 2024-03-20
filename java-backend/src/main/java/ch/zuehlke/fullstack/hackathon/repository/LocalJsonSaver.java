package ch.zuehlke.fullstack.hackathon.repository;

import ch.zuehlke.fullstack.hackathon.model.Song;
import ch.zuehlke.fullstack.hackathon.service.SongSaver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class LocalJsonSaver implements SongSaver {

    private final ObjectMapper objectMapper;
    private final String jsonFilePath;

    private Map<UUID, Song> defaultEmptyMap = new ConcurrentHashMap<>();

    public LocalJsonSaver() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String userHome = System.getProperty("user.home");
        String applicationDirectory = "/songWrAiter";
        String fileName = "songs.json";

        new File(userHome, applicationDirectory).mkdirs();

        this.jsonFilePath = Paths.get(userHome, applicationDirectory, fileName).toString();
    }

    @Override
    public void save(final Map<UUID, Song> mapToSave) {
        try {
            objectMapper.writeValue(new File(jsonFilePath), mapToSave);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<UUID, Song> load() {
        try {
            return objectMapper.readValue(new File(jsonFilePath), objectMapper.getTypeFactory().constructMapType(Map.class, UUID.class, Song.class));
        } catch (IOException e) {
            log.error("loading failed, lets take a emptyMap. Error={}", e.getMessage());
            return defaultEmptyMap;
        }
    }
}
