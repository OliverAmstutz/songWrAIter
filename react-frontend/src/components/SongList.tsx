import {useQuery} from "@tanstack/react-query";
import remoteService from "../services/RemoteService.tsx";
import SongItem from "./SongItem.tsx";

export interface MusicgenSong {
    id: string
    topic: string
    genre: string
    instruments: string[]
    mood: string
    generatedVerseText?: string
    generatedChorusText?: string
    bertUrls: SongUrls
    musicGenUrls: SongUrls
    imageUrl: string
}

export interface SongUrls {
    mp3: string
    score: string
    midi: string
}

export default function SongList() {
    const query = useQuery({
        queryKey: ['songs'],
        queryFn: () => remoteService.get<MusicgenSong[]>("/song"),
        refetchInterval: 3000
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