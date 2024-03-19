import "./CreateSongForm.css"
import {SubmitHandler, useForm} from "react-hook-form"
import remoteService from "../services/RemoteService.tsx";
import {useMutation, useQueryClient} from "@tanstack/react-query";


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

    const queryClient = useQueryClient()

    function submitForm(data: IFormInput) {
        return remoteService.post("/song", {
            ...data,
            instruments: [data.instruments]
        });
    }

    const mutation = useMutation({
        mutationFn: submitForm,
        onSuccess: () => {
            // Invalidate and refetch
            queryClient.invalidateQueries({queryKey: ['songs']})
        },
    })

    const onSubmit: SubmitHandler<IFormInput> = (data) => mutation.mutate(data)

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <label>Topic</label>
            <input type={"text"} {...register("topic")} />

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
