package fr.svivien.cgbenchmark.model.request.session;

import java.util.ArrayList;

/**
 * Request body for a SessionAPI
 */
public class SessionChallengeRequest extends ArrayList<Object> {

    public SessionChallengeRequest(int userId, String multiName) {
    	add(multiName);
        add(userId);
    }

}
