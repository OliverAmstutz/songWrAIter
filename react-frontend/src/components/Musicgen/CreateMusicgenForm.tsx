import {SubmitHandler, useForm} from "react-hook-form";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import remoteService from "../../services/RemoteService.tsx";
import {GENRES} from "../../shared/GENRES.ts";

interface IFormInput {
    genre: string;
    chordProgression: string;
    artist: string;
    beatsPerMinute: string
    timeSignature: string;
    mood: string;
    instruments: string[]
}

interface IFormSubmitInput {
    genre: string;
    chordProgression: string[];
    artist: string;
    beatsPerMinute: string
    timeSignature: string;
    mood: string;
    instruments: string[]
}

export default function CreateMusicgenForm() {
    const {register, handleSubmit, reset} = useForm<IFormInput>({
        defaultValues: {
            genre: '',
            chordProgression: '',
            artist: '',
            beatsPerMinute: '',
            timeSignature: '',
            mood: '',
            instruments: []
        },
    });

    const queryClient = useQueryClient();

    function submitForm(data: IFormSubmitInput) {
        return remoteService.post("/song/musicgen", data);
    }

    const mutation = useMutation({
        mutationFn: submitForm,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['musicgenSongs']});
            reset()
        },
    });

    const onSubmit: SubmitHandler<IFormInput> = (data) => mutation.mutate({
        ...data,
        chordProgression: data.chordProgression.split(',')
    });

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <label style={{fontWeight: 'bold'}}>Genre:</label>
            <select {...register("genre")} defaultValue="">
                {GENRES.map((genre) => (
                    <option key={genre.value} value={genre.value}>
                        {genre.label}
                    </option>
                ))}
            </select>

            <label style={{fontWeight: 'bold'}}>Chord progression:</label>
            <input {...register("chordProgression")} type={"text"}/>

            <label style={{fontWeight: 'bold'}}>Artist like...:</label>
            <input {...register("artist")} type={"text"}/>


            <label style={{fontWeight: 'bold'}}>BPM:</label>
            <input {...register("beatsPerMinute")} type={"text"}/>

            <label style={{fontWeight: 'bold'}}>Time signature:</label>
            <input {...register("timeSignature")} type={"text"}/>

            <label style={{fontWeight: 'bold'}}>Mood:</label>
            <label>What mood do you want to be in?</label>
            <select {...register("mood")} defaultValue="">
                <option value="calm">calm</option>
                <option value="energized">energized</option>
                <option value="happy">happy</option>
                <option value="sad">sad</option>
                <option value="angry">angry</option>
            </select>

            <fieldset>
                <legend style={{fontWeight: 'bold'}}>Instruments</legend>
                <label>Select instruments</label>
                <div>
                    {["guitar", "piano", "drums", "violin", "bass", "saxophone", "flute", "cello"].map((instrument) => (
                        <div key={instrument}>
                            <label>
                                <input
                                    type="checkbox"
                                    value={instrument}
                                    {...register("instruments")}
                                /> {instrument}
                            </label>
                        </div>
                    ))}
                </div>
            </fieldset>

            <input type="submit" disabled={mutation.isPending}/>
        </form>)
}