package fr.svivien.cgbenchmark.model.request.session;

/**
 * Response for a SessionAPI
 */
public class SessionResponse {

    public SessionResponseSuccess success;

    public class SessionResponseSuccess {
        public String handle;
        public String testSessionHandle;
    }
}
