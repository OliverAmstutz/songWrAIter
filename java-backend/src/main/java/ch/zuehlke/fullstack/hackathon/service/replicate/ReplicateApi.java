package ch.zuehlke.fullstack.hackathon.service.replicate;

import ch.zuehlke.fullstack.hackathon.model.BertPromptDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ReplicateApi {

    private static final String URL = "https://api.replicate.com/v1/predictions";

    private static final String BERT_MODEL = "58bdc2073c9c07abcc4200fe808e15b1a555dbb1390e70f5daa6b3d81bd11fb1";

    private final WebClient webClient;

    public ReplicateApi(@Value("${replicateApiKey}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(URL)
                .defaultHeader("Authorization", "Token %s".formatted(apiKey))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public ReplicateResult<Map<String, String>> createBertJob(BertPromptDto bertPrompt) {
        var body = new ReplicateInput<>(BERT_MODEL, bertPrompt);
        return webClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ReplicateResult<Map<String, String>>>() {
                })
                .block();
    }

    public ReplicateResult<Map<String, String>> pollBertResult(String jobUrl) {
        return webClient
                .mutate()
                .baseUrl(jobUrl)
                .build()
                .get()
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ReplicateResult<Map<String, String>>>() {
                })
                .block();
    }
}


//{
//        "completed_at": "2024-03-19T20:57:34.119563Z",
//        "created_at": "2024-03-19T20:57:20.676202Z",
//        "error": null,
//        "id": "bqmyyqzbify2mnfkuudmnfrspq",
//        "input": {
//        "seed": -1,
//        "notes": "?|?|?|?|?|?|?|?|?|?|?|?|?|?|?|?|?|?|?|?|?|?|?|?|?|?|?|?",
//        "tempo": 140,
//        "chords": "G|C|D|G|C|D|G|C|D|G|G|D|C|G|G|C|D|G|C|D|G|C|D|G|G|D|C|G",
//        "sample_width": 10,
//        "time_signature": 4
//        },
//        "logs": "Using seed: 29451\n0.0 10\n0.1 8\n0.2 6\n0.36\n0.4 5\n0.5 4\n0.6 4\n0.7 3\n0.8 2\n0.9 2\n0:0 (0/1 G5) G/G 5\n0:1 (0/1 G5) G/G 5\n0:2 (0/1 G5) G/G 7\n1:3 (0/1 G5) G/G 4\n1:0 (0/1 E5) C/C 3\n1:1  C/C 9\n1:2  C/C 2\n2:3  C/C 6\n2:0 (0/1 D5) D/D 1\n2:1  D/D 1\n2:2 (0/1 D5) D/D 3\n3:3  D/D8\n3:0 (0/1 G4) G/G 1\n3:1  G/G 7\n3:2 (0/1 G5) G/G 3\n4:3 (0/1 G5) G/G 1\n4:0 (0/1 G5) C/C 4\n4:1 (0/1 G5) C/C 3\n4:2 (0/1 E5) C/C 7\n5:3 (0/1 E5) C/C 7\n5:0 (0/1 F#5) D/D 8\n5:1  D/D 6\n22: Invalid pattern: 0\n6:3  D/D 3\n6:0 (0/1 G4) G/G 8\n6:1 (0/1 G5) G/G 2\n6:2 (0/1 G5) G/G 5\n7:3 (0/1 G5) G/G 4\n7:0 (0/1 C5) C/C 4\n7:1  C/C 9\n7:2 (0/1 E5) C/C 7\n8:3 (0/1 E5) C/C 8\n8:0 (0/1 F#5) D/D 1\n8:1  D/D 3\n8:2  D/D 9\n9:3 (0/1 F#5) D/D 7\n9:0 (0/1 G5) G/G 4\n9:1 (0/1 G4) G/G 4\n9:2  G/G 10\n10:3  G/G 6\n10:0 (0/1 B4) G/G 2\n10:1  G/G 2\n10:2 (0/1 G5) G/G1\n11:3 (0/1 G5) G/G 5\n11:0 (0/1 F#5) D/D 2\n11:1  D/D 9\n11:2 (0/1 A5) D/D 6\n12:3 (0/1 A5) D/D1\n12:0 (0/1 G5) C/C 7\n12:1  C/C 9\n12:2 (0/1 C6) C/C6\n13:3 (0/1 B5) C/C 3\n13:0 (0/1 G5) G/G5\n13:1  G/G 6\n13:2 (0/1 G4) G/G 5\n14:3  G/G 9\n14:0 (0/1 G4) G/G 3\n14:1  G/G 10\n14:2 (0/1 G4) G/G 6\n15:3  G/G 8\n15:0  C/C 2\n15:1  C/C 7\n15:2 (0/1 E5) C/C 8\n16:3  C/C 4\n16:0 (0/1 D5) D/D 1\n16:1  D/D 4\n16:2 (0/1 F#5) D/D6\n17:3  D/D 3\n17:0 (0/1 G5) G/G 1\n17:1  G/G 1\n17:2 G/G 9\n18:3 (0/1 G5) G/G 4\n18:0 (0/1 E5) C/C 8\n18:1 (0/1 G5) C/C 8\n18:2 (0/1 E5) C/C 6\n19:3  C/C 3\n19:0 (0/1 A5) D/D 8\n19:1  D/D 4\n19:2 (0/1 F#5) D/D 5\n20:3  D/D 2\n20:0 (0/1 G5) G/G 8\n20:1 (0/1 G5) G/G 9\n20:2 (0/1 G5) G/G 8\n21:3 (0/1 G5) G/G 4\n21:0 (0/1 G5) C/C 5\n21:1  C/C 6\n21:2 (0/1 E5) C/C 8\n22:3 (0/1 G5) C/C 7\n22:0 (0/1 F#5) D/D 5\n22:1  D/D 4\n22:2 (0/1 D5) D/D 7\n23:3  D/D 2\n23:0 (0/1 G4) G/G 5\n23:1 (0/1 G5) G/G 1\n23:2 (0/1 G5) G/G 2\n24:3 (0/1 G5) G/G 9\n24:0 (0/1 G4) G/G 2\n24:1 (0/1 G5) G/G 1\n24:2 (0/1 G5) G/G 10\n25:3 (0/1 G5) G/G 6\n25:0 (0/1 F#5) D/D 9\n25:1  D/D 3\n25:2 (0/1 D5) D/D 5\n26:3 (0/1 B5) D/D 2\n26:0 (0/1 C6) C/C 9\n26:1 (0/1 E5) C/C 6\n26:2  C/C 5\n27:3  C/C 7\n27:0 (0/1 G5) G/G 7\n27:1 (0/1 G5) G/G 9\n27:2 (0/1 G4) G/G 3\n0 pattern\nffmpeg version 3.4.8-0ubuntu0.2 Copyright (c) 2000-2020 the FFmpeg developers\nbuilt with gcc 7 (Ubuntu 7.5.0-3ubuntu1~18.04)\nconfiguration: --prefix=/usr --extra-version=0ubuntu0.2 --toolchain=hardened --libdir=/usr/lib/x86_64-linux-gnu --incdir=/usr/include/x86_64-linux-gnu --enable-gpl --disable-stripping --enable-avresample --enable-avisynth --enable-gnutls --enable-ladspa --enable-libass --enable-libbluray --enable-libbs2b --enable-libcaca --enable-libcdio --enable-libflite --enable-libfontconfig --enable-libfreetype --enable-libfribidi --enable-libgme --enable-libgsm --enable-libmp3lame --enable-libmysofa --enable-libopenjpeg --enable-libopenmpt --enable-libopus --enable-libpulse --enable-librubberband --enable-librsvg --enable-libshine --enable-libsnappy --enable-libsoxr --enable-libspeex --enable-libssh --enable-libtheora --enable-libtwolame --enable-libvorbis --enable-libvpx --enable-libwavpack --enable-libwebp --enable-libx265 --enable-libxml2 --enable-libxvid --enable-libzmq --enable-libzvbi --enable-omx --enable-openal --enable-opengl --enable-sdl2 --enable-libdc1394 --enable-libdrm --enable-libiec61883 --enable-chromaprint --enable-frei0r --enable-libopencv --enable-libx264 --enable-shared\nlibavutil      55. 78.100 / 55. 78.100\nlibavcodec     57.107.100 / 57.107.100\nlibavformat    57. 83.100 / 57. 83.100\nlibavdevice    57. 10.100 / 57. 10.100\nlibavfilter     6.107.100 /  6.107.100\nlibavresample   3.  7.  0 /  3.  7.  0\nlibswscale      4.  8.100 /  4.  8.100\nlibswresample   2.  9.100 /  2.  9.100\nlibpostproc    54.  7.100 / 54.  7.100\nGuessed Channel Layout for Input Stream #0.0 : mono\nInput #0, wav, from '/tmp/tmpu0ma68og/out.wav':\nDuration: 00:00:48.57, bitrate: 2822 kb/s\nStream #0:0: Audio: pcm_f64le ([3][0][0][0] / 0x0003), 44100 Hz, mono, dbl, 2822 kb/s\nStream mapping:\nStream #0:0 -> #0:0 (pcm_f64le (native) -> mp3 (libmp3lame))\nPress [q] to stop, [?] for help\nOutput #0, mp3, to '/tmp/tmpu0ma68og/out.mp3':\nMetadata:\nTSSE            : Lavf57.83.100\nStream #0:0: Audio: mp3 (libmp3lame), 44100 Hz, mono, fltp\nMetadata:\nencoder         : Lavc57.107.100 libmp3lame\nsize=     172kB time=00:00:21.98 bitrate=  64.2kbits/s speed=  44x\nsize=     377kB time=00:00:48.18 bitrate=  64.1kbits/s speed=51.7x\nvideo:0kB audio:377kB subtitle:0kB other streams:0kB global headers:0kB muxing overhead: 0.058842%\nLY output to `/tmp/tmpu0ma68og/score.ly'...\nChanging working directory to: `/tmp/tmpu0ma68og'\nProcessing `/tmp/tmpu0ma68og/score.ly'\nParsing...\nInterpreting music...[8][16][24]\nPreprocessing graphical objects...\nInterpreting music...\nMIDI output to `score.midi'...\nFinding the ideal number of pages...\nFitting music on 1 page...\nDrawing systems...\nLayout output to `score.ps'...\nConverting to PNG...\nSuccess: compilation successfully completed",
//        "metrics": {
//        "predict_time": 13.425649,
//        "total_time": 13.443361
//        },
//        "output": {
//        "mp3": "https://replicate.delivery/pbxt/XoqJimmkw3YlBRxI5VclJGGTva7BfhkJCuXgE0P20nze46hSA/out.mp3",
//        "midi": "https://replicate.delivery/pbxt/oBWU1v9NE1YbPpQkFmaWw1XY1B4zjYEeA9IKEQfzHuW946hSA/out.midi",
//        "score": "https://replicate.delivery/pbxt/HlMggLfW3CzVbaKx66HqBMeKK610uf8KT7OJ1Eh75Hf3jrHKB/score.png"
//        },
//        "started_at": "2024-03-19T20:57:20.693914Z",
//        "status": "succeeded",
//        "urls": {
//        "get": "https://api.replicate.com/v1/predictions/bqmyyqzbify2mnfkuudmnfrspq",
//        "cancel": "https://api.replicate.com/v1/predictions/bqmyyqzbify2mnfkuudmnfrspq/cancel"
//        },
//        "version": "58bdc2073c9c07abcc4200fe808e15b1a555dbb1390e70f5daa6b3d81bd11fb1"
//        }