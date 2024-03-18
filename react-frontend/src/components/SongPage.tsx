import styled from 'styled-components';
import CreateSongForm from "./CreateSongForm.tsx";
import SongList from "./SongList.tsx";

const Section = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
`;

export default function SongPage() {
    return (
        <Section>
            <CreateSongForm/>
            <SongList/>
        </Section>
    );
}
