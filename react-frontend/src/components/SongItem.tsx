import "./Song.css"
import AudioPlayer from "./AudioPlayer.tsx";

interface Song {
    id: string
    topic: string

}

export default function SongItem({topic, id}: Song) {
    return (
        <li className={"song-item"}>
            {topic}
            <AudioPlayer id={id}/>
        </li>)
}