import "./Song.css"
import AudioPlayer from "./AudioPlayer.tsx";
import {Song} from "./SongList.tsx";
import CroppedImage from "./CroppedImage.tsx";

export default function SongItem({
                                     topic,
                                     id,
                                     bertUrls,
                                     musicGenUrls,
                                     imageUrl,
                                     generatedChorusText,
                                     generatedVerseText
                                 }: Song) {
    return (
        <li className={"song-item"}>
            <div className="container">
                <div className="left-column">
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
                </div>
                <div className="right-column">
                    {
                        imageUrl ? <img className="image" src={imageUrl} alt={topic}/>
                            : <div className="spinner-container">
                                <div className="spinner"></div>
                                <div className="spinner-text">Loading Image...</div>
                            </div>
                    }

                </div>
            </div>

            {bertUrls ?
                <div className="canvas-container">
                    <CroppedImage imageUrl={bertUrls.score}/>
                </div>
                : ''
            }

        </li>)
}