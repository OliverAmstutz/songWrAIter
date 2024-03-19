import "./Song.css"
import AudioPlayer from "./AudioPlayer.tsx";
import {Song} from "./SongList.tsx";
import CroppedImage from "./CroppedImage.tsx";

export default function SongItem({topic, id, urls, generatedChorusText, generatedVerseText}: Song) {


    return (
        <li className={"song-item"}>
            <h2 className="song-topic">{topic}</h2>
            {urls ? <AudioPlayer id={id} songUrls={urls}/> : ' song is still loading'
            }
            <div className="lyrics">
                <strong>
                    Lyrics:
                </strong>
                {` ${generatedVerseText ?? ''} ${generatedChorusText ?? ''}`}
            </div>
            {urls ?
                <div className="canvas-container">
                    <CroppedImage imageUrl={urls.score}/>
                </div>
                : ''
            }

        </li>)
}