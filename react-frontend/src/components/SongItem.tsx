import "./Song.css"
import AudioPlayer from "./AudioPlayer.tsx";
import {Song} from "./SongList.tsx";

export default function SongItem({topic, id, urls, generatedChorusText, generatedVerseText}: Song) {
    return (
        <li className={"song-item"}>
            {topic}
            {urls ? <AudioPlayer id={id} songUrls={urls}/> : 'song is still loading'
            }
            <div>
            Lyrics: {`${generatedVerseText ?? ''} ${generatedChorusText ?? ''}`}

            </div>
        </li>)
}