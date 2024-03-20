package ch.zuehlke.fullstack.hackathon.service.replicate;

import java.util.Map;

public record ReplicateResult<T>(String id, String status, Map<String, String> urls, T output) {
    public boolean isDone() {
        return !status.equals("starting") && !status.equals("processing");
    }

    public boolean isSucceeded() {
        return status.equals("succeeded");
    }

    public String getJobUrl() {
        return urls.get("get");
    }
}
