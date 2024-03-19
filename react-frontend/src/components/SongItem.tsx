import "./Song.css"
import AudioPlayer from "./AudioPlayer.tsx";
import {Song} from "./SongList.tsx";

export default function SongItem({topic, id, urls, generatedChorusText, generatedVerseText}: Song) {
    return (
        <li className={"song-item"}>
            <h2 className="song-topic">{topic}</h2>
            {urls ? <AudioPlayer id={id} songUrls={urls}/> : ' song is still loading'
            }
            <div>
                <strong>
                    Lyrics:
                </strong>
                {` ${generatedVerseText ?? ''} ${generatedChorusText ?? ''}`}
            </div>
            {urls ?

                    <img src={urls.score} alt="Song score"/>
                : ''
            }
        </li>)
}