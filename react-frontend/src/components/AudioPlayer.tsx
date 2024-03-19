export default function AudioPlayer() {
    return (
        <audio controls>
            <source src="http://localhost:8080/sound-file" type="audio/mpeg"/>
            Your browser does not support the audio element.
        </audio>
    );
}
