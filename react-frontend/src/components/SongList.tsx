import {useQuery} from "@tanstack/react-query";
import remoteService from "../services/RemoteService.tsx";

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
    return <ul>{query.data?.map((todo) => <li key={todo.id}>{todo.topic}</li>)}</ul>
}