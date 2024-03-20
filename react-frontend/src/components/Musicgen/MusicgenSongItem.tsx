import "./MusicgenSong.css"
import {IMusicgenSong} from "./IMusicgenSong.tsx";
import MusicgenAudioPlayer from "./MusicgenAudioPlayer.tsx";

export default function MusicgenSongItem({
                                             id,
                                             title,
                                             genre,
                                             chordProgression,
                                             prompt,
                                             artist,
                                             url
                                         }: IMusicgenSong) {

    return (
        <li className={"song-item"}>
            <h2 className="song-title">{title}</h2>
            <h2 className="description">Genre: {genre}</h2>
            <h2 className="description">Chord progression: {chordProgression}</h2>
            <h2 className="description">Prompt: {prompt}</h2>
            <h2 className="description">Artist: {artist}</h2>
            {url ?
                <MusicgenAudioPlayer id={id} songUrl={url}/> : ' song is still loading'
            }
        </li>)
}