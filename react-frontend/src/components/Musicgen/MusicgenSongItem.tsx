import "./MusicgenSong.css"
import {IMusicgenSong} from "./IMusicgenSong.tsx";
import MusicgenAudioPlayer from "./MusicgenAudioPlayer.tsx";

export default function MusicgenSongItem({
                                             id,
                                             genre,
                                             chordProgression,
                                             prompt,
                                             artist,
                                             songUrl
                                         }: IMusicgenSong) {

    return (
        <li className={"song-item"}>
            <h2 className="song-title">"missing title"</h2>
            <h2 className="description">Genre: {genre}</h2>
            <h2 className="description">Chord progression{chordProgression}</h2>
            <h2 className="description">Prompt: {prompt}</h2>
            <h2 className="description">Artist: {artist}</h2>
            {songUrl ?
                <MusicgenAudioPlayer id={id} songUrl={songUrl}/> : ' song is still loading'
            }
        </li>)
}