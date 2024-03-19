import {useQuery} from "@tanstack/react-query";
import remoteService from "../services/RemoteService.tsx";
import SongItem from "./SongItem.tsx";

export interface Song {
    id: string
    topic: string
    genre: string
    instruments: string[]
    mood: string
    urls: SongUrls
}

export interface SongUrls {
    mp3: string
    score: string
    midi: string
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
                    <SongItem key={song.id} {...song}/>)
                }</ul>
        );
    }

    return <ul>{renderSongs()}</ul>
}