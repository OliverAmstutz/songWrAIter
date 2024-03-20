import styled from 'styled-components';
import CreateMusicgenForm from "./CreateMusicgenForm.tsx";
import MusicgenSongList from "./MusicgenSongList.tsx";


const Section = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
`;

export default function MusicgenSongPage() {
    return (
        <Section>
            <CreateMusicgenForm/>
            <MusicgenSongList/>
        </Section>
    );
}