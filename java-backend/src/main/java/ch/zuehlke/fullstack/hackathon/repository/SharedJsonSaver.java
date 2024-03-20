package ch.zuehlke.fullstack.hackathon.repository;

import ch.zuehlke.fullstack.hackathon.model.Song;
import ch.zuehlke.fullstack.hackathon.service.SongSaver;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
@Component
@Profile("shared-store")
public class SharedJsonSaver implements SongSaver {

    private final ObjectMapper objectMapper;

    @Value("azure-blob://blob/songs.json")
    private Resource blobFile;

    private Map<UUID, Song> defaultEmptyMap = new ConcurrentHashMap<>();

    @Override
    public void save(final Map<UUID, Song> mapToSave) {

        try (OutputStream os = ((WritableResource) this.blobFile).getOutputStream()) {
            os.write(objectMapper.writeValueAsBytes(mapToSave));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<UUID, Song> load() {
        try {

            return objectMapper.readValue(this.blobFile.getInputStream(), objectMapper.getTypeFactory().constructMapType(Map.class, UUID.class, Song.class));

        } catch (Exception e) {
            e.printStackTrace();
            log.error("remote load failed. Error={}", e.getMessage());
            log.warn("File is corrupt or no longer compatible with existing data structre. We purge the file");
        }
        return defaultEmptyMap;
    }
}
