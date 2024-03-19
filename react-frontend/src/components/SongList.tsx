import {useQuery} from "@tanstack/react-query";
import remoteService from "../services/RemoteService.tsx";
import SongItem from "./SongItem.tsx";

interface Song {
    id: string
    topic: string
    genre: string
    instruments: string[]
    mood: string
}

export default function SongList() {
    const query = useQuery({
        queryKey: ['songs'],
        queryFn: () => remoteService.get<Song[]>("/song")
    })

    function renderSongs() {
        return (
            <ul>
                {query.data?.map((song) =>
                    <SongItem key={song.id} id={song.id} topic={song.topic}/>)
                }</ul>
        );
    }

    return <ul>{renderSongs()}</ul>
}