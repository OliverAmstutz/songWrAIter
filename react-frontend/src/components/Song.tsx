import "./Song.css"

interface Song {
    topic: string
}

export default function Song({topic}: Song) {
    return <li className={"song-item"}>{topic}</li>
}