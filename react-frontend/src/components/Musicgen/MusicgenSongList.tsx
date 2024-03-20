import {useQuery} from "@tanstack/react-query";
import remoteService from "../../services/RemoteService.tsx";
import MusicgenSongItem from "./MusicgenSongItem.tsx";
import {IMusicgenSong} from "./IMusicgenSong.tsx";


export default function MusicgenSongList() {
    const query = useQuery({
        queryKey: ['musicgenSongs'],
        queryFn: () => remoteService.get<IMusicgenSong[]>("/song/musicgen"),
        refetchInterval: 3000
    })

    function renderSongs() {
        return (
            <ul>
                {query.data?.map((song) =>
                    <MusicgenSongItem key={song.id} {...song}/>)}
            </ul>);
    }

    return <ul>{renderSongs()}</ul>
}