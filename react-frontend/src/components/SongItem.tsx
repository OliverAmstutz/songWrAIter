import "./Song.css"
import AudioPlayer from "./AudioPlayer.tsx";
import {Song} from "./SongList.tsx";

// interface Song {
//     id: string
//     topic: string
//     // genre: string
//     // instruments: string[]
//     // mood: string
//     // bertId: string
//     urls: SongUrls
// }



export default function SongItem({topic, id, urls}: Song) {
    return (
        <li className={"song-item"}>
            {topic}
            {urls ? <AudioPlayer id={id} songUrls={urls}/> : 'song is still loading'
            }
        </li>)
}