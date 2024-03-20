interface AudioPlayerSong {
    id: string
    songUrl: string
}

export default function MusicgenAudioPlayer({id, songUrl}: AudioPlayerSong) {
    console.log(id)
    return (
        <audio controls>
            <source src={songUrl} type="audio/mpeg"/>
            Your browser does not support the audio element.
        </audio>
    );
}