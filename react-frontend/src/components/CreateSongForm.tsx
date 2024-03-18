import {SubmitHandler, useForm} from "react-hook-form"
import remoteService from "../services/RemoteService.tsx";

interface IFormInput {
    topic: string
    genre: string
    instruments: string[]
    mood: string
}

export default function CreateSongForm() {
    const {register, handleSubmit} = useForm<IFormInput>({
        defaultValues: {
            topic: '',
            genre: '',
            instruments: [],
            mood: '',
        },
    })
    const onSubmit: SubmitHandler<IFormInput> = (data) => remoteService.post("/song", {
        ...data,
        instruments: [data.instruments]
    })

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <label>Topic</label>
            <input {...register("topic")} />

            <label>Genre</label>
            <select {...register("genre")} defaultValue={""}>
                <option value="rock">rock</option>
                <option value="blues">blues</option>
                <option value="pop">pop</option>
            </select>

            <label>Instruments</label>
            <select {...register("instruments")}>
                <option value="guitar">guitar</option>
                <option value="piano">piano</option>
                <option value="drums">drums</option>
            </select>

            <label>Mood</label>
            <select {...register("mood")}>
                <option value="sad">sad</option>
                <option value="happy">happy</option>
                <option value="neutral">neutral</option>
            </select>

            <input type="submit"/>
        </form>)
}