package ch.zuehlke.fullstack.hackathon.model;

public enum Genre {

    ROCK("rock", 140, 4),
    BLUES("blues", 100, 4),
    POP("pop", 120, 4),
    JAZZ("jazz", 100, 7),
    CLASSICAL("classical", 80, 4),
    HIPHOP("hip-hop", 120,4),
    ELECTRONIC("electronic", 200, 4),
    COUNTRY("country", 140, 4),
    REGGAE("reggae", 60, 4),
    SCHWIIZERPOPMUSIG("schwiizer Popmusig", 120, 5);

    public final int tempo;
    public final String genreName;
    public final int timeSignature;

    Genre(String genreName, int tempo, int timeSignature) {
        this.tempo = tempo;
        this.genreName = genreName;
        this.timeSignature = timeSignature;
    }

    public static Genre mapGenre(String name) {
        return switch (name) {
            case "rock" -> Genre.ROCK;
            case "blues" -> Genre.BLUES;
            case "pop" -> Genre.POP;
            case "jazz" -> Genre.JAZZ;
            case "classical" -> Genre.CLASSICAL;
            case "hipHop" -> Genre.HIPHOP;
            case "electronic" -> Genre.ELECTRONIC;
            case "country" -> Genre.COUNTRY;
            case "reggae" -> Genre.REGGAE;
            case "schwiizer Popmusig" -> Genre.SCHWIIZERPOPMUSIG;
            default -> throw new IllegalArgumentException("Illegal genre: " + name);
        };
    }
}