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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class LocalJsonSaver implements SongSaver {

    public static final String USER_HOME = System.getProperty("user.home");
    public static final String APPLICATION_DIRECTORY = "/songWrAiter";
    public static final String FILE_NAME = "songs.json";
    public static final Path DEFAULT_FILE_PATH = Paths.get(USER_HOME, APPLICATION_DIRECTORY, FILE_NAME);
    private final ObjectMapper objectMapper;
    private final String jsonFilePath;

    private Map<UUID, Song> defaultEmptyMap = new ConcurrentHashMap<>();

    public LocalJsonSaver() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        new File(USER_HOME, APPLICATION_DIRECTORY).mkdirs();

        this.jsonFilePath = DEFAULT_FILE_PATH.toString();
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
            File toRead = new File(jsonFilePath);
            if (toRead.exists()) {
                return objectMapper.readValue(toRead, objectMapper.getTypeFactory().constructMapType(Map.class, UUID.class, Song.class));
            } else {
                return defaultEmptyMap;
            }
        } catch (IOException e) {
            log.error("loading failed, lets take a emptyMap. Error={}", e.getMessage());
            log.warn("File is corrupt or no longer compatible with existing data structre. We purge the file");
            try {
                this.purgeErrorFile();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return defaultEmptyMap;
        }
    }

    private void purgeErrorFile() throws IOException {
        Files.deleteIfExists(DEFAULT_FILE_PATH);
//
    }
}
