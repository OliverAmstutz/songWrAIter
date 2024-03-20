import "./Song.css"
import AudioPlayer from "./AudioPlayer.tsx";
import {Song} from "./SongList.tsx";
import CroppedImage from "./CroppedImage.tsx";

export default function SongItem({topic, id, bertUrls, musicGenUrls, generatedChorusText, generatedVerseText}: Song) {


    return (
        <li className={"song-item"}>
            <h2 className="song-topic">{topic}</h2>
            <div>
                <p>BERT</p>
                {bertUrls ? <AudioPlayer id={id} songUrls={bertUrls}/> : ' song is still loading'}
            </div>

            <div>
                <p>Music Gen</p>
                {musicGenUrls ? <AudioPlayer id={id} songUrls={musicGenUrls}/> : ' song is still loading'}
            </div>

            <div className="lyrics">
                <strong>
                    Lyrics:
                </strong>
                {` ${generatedVerseText ?? ''} ${generatedChorusText ?? ''}`}
            </div>
            {bertUrls ?
                <div className="canvas-container">
                    <CroppedImage imageUrl={bertUrls.score}/>
                </div>
                : ''
            }

        </li>)
}