import {useEffect, useRef} from "react";


interface CroppedImageProps {
    imageUrl: string;
}

export default function CroppedImage({imageUrl}: CroppedImageProps) {
    const canvasRef = useRef<HTMLCanvasElement>(null);

    useEffect(() => {
        const image = new Image();
        image.src = imageUrl;
        image.onload = () => {
            const canvas = canvasRef.current;
            if (canvas) {
                const ctx = canvas.getContext('2d');
                if (ctx) {
                    const cropHeight = 240;
                    canvas.width = image.width;
                    canvas.height = image.height - cropHeight;
                    ctx.drawImage(
                        image,
                        0, 0, image.width, image.height - cropHeight,
                        0, 0, canvas.width, canvas.height
                    );
                }
            }
        };
    }, [imageUrl]);

    return <canvas ref={canvasRef} />;
}
