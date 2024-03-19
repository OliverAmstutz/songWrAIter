import {SongUrls} from "./SongList.tsx";

interface AudioPlayerSong {
    id: string
    songUrls: SongUrls
}

export default function AudioPlayer({id, songUrls}: AudioPlayerSong) {
    console.log(id)
    return (
        <audio controls>
            <source src={songUrls.mp3} type="audio/mpeg"/>
            Your browser does not support the audio element.
        </audio>
    );
}
