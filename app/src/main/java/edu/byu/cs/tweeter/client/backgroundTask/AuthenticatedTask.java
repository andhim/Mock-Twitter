package edu.byu.cs.tweeter.client.backgroundTask;


import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.AuthenticatedRequest;

public abstract class AuthenticatedTask extends BackgroundTask{
    public static final String LOG_TAG = "AuthenticatedTask";

    /**
     * Auth token for logged-in user.
     * This user is the "follower" in the relationship.
     */
    protected AuthToken authToken;

    //TODO: Erase
    protected AuthenticatedTask(AuthToken authToken, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
    }

    protected AuthenticatedTask(AuthenticatedRequest request, Handler messageHandler) {
        super(messageHandler);
        this.authToken = request.getAuthToken();
    }
}
