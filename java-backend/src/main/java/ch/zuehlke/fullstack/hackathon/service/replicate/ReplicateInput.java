package ch.zuehlke.fullstack.hackathon.service.replicate;

public record ReplicateInput<T>(
        String version,
        T input
) {
}
