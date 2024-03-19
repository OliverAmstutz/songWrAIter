package ch.zuehlke.fullstack.hackathon;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SoundFileController {
    @Value("classpath:soundfiles/Master_Of_Puppets.mp3")
    Resource soundFile;

    @GetMapping("/sound-file")
    public ResponseEntity<Resource> getFile() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf("audio/mpeg3"))
                .body(soundFile);
    }
}
