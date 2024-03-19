interface Song {
    id: string
}

export default function AudioPlayer({id}: Song) {
    console.log(id)
    return (
        <audio controls>
            <source src={"http://localhost:8080/sound-file/" + id} type="audio/mpeg"/>
            Your browser does not support the audio element.
        </audio>
    );
}
