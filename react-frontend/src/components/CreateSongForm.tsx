import "./CreateSongForm.css"
import {SubmitHandler, useForm} from "react-hook-form"
import remoteService from "../services/RemoteService.tsx";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {useEffect} from "react";


interface IFormInput {
    topic: string;
    genre: string;
    instruments: string[];
    mood: string;
}

const GENRES = [
    { value: "rock", label: "Rock" },
    { value: "blues", label: "Blues" },
    { value: "pop", label: "Pop" },
    { value: "jazz", label: "Jazz" },
    { value: "classical", label: "Classical" },
    { value: "hip-hop", label: "Hip-Hop" },
    { value: "electronic", label: "Electronic" },
    { value: "country", label: "Country" },
    { value: "reggae", label: "Reggae" },
    { value: "Schwiizer Popmusig", label: "Schwiizer Popmusig" },
];

export default function CreateSongForm() {
    const {register, handleSubmit, watch, setValue} = useForm<IFormInput>({
        defaultValues: {
            topic: '',
            genre: '',
            instruments: [],
            mood: '',
        },
    });

    const selectedInstruments = watch("instruments");

    useEffect(() => {
        if (selectedInstruments.length > 2) {
            setValue("instruments", selectedInstruments.slice(0, 2));
        }
    }, [selectedInstruments, setValue]);

    const queryClient = useQueryClient();

    function submitForm(data: IFormInput) {
        return remoteService.post("/song", data);
    }

    const mutation = useMutation({
        mutationFn: submitForm,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['songs']});
        },
    });

    const onSubmit: SubmitHandler<IFormInput> = (data) => mutation.mutate(data);


    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <label style={{fontWeight: 'bold'}}>Topic:</label>
            <label>Whats the song about</label>
            <input type={"text"} {...register("topic")} />

            <label style={{fontWeight: 'bold'}}>Genre:</label>
            <label>What style is your song?</label>
            <select {...register("genre")} defaultValue="">
                {GENRES.map((genre) => (
                    <option key={genre.value} value={genre.value}>
                        {genre.label}
                    </option>
                ))}
            </select>

            <fieldset>
                <legend style={{fontWeight: 'bold'}}>Instruments</legend>
                <label>Select up to two instruments</label>
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

            <label style={{fontWeight: 'bold'}}>Mood:</label>
            <label>What mood should your child feel after the song is played?</label>
            <select {...register("mood")}>
                <option value="sad">sad</option>
                <option value="happy">happy</option>
                <option value="sleepy">sleepy</option>
                <option value="neutral">neutral</option>
            </select>

            <input type="submit" disabled={mutation.isPending}/>
        </form>)

}
