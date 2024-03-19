package ch.zuehlke.fullstack.hackathon.controller;

import ch.zuehlke.fullstack.hackathon.service.SongCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class SoundFileController {

    @Autowired
    SongCache songCache;

    @GetMapping("/sound-file/{songId}")
    public ResponseEntity<Resource> getFile(@PathVariable String songId) {
        Path songPath =
                Paths.get("/Users/phil/Documents/hackathon/java-backend/src/songfiles/" + songId +
                        ".mp3");
        Resource songFile = null;
        try {
            songFile = new UrlResource(songPath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf("audio/mpeg3"))
                .body(songFile);
    }
}
