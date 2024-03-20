import {IMusicgenSong} from "./IMusicgenSong.tsx";

export default function MusicgenSong({genre}: IMusicgenSong) {
    return <li className={"musicgen-song-item"}>{genre}</li>
}