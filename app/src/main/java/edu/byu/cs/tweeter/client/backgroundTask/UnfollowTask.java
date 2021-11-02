package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask {
    private static final String LOG_TAG = "UnfollowTask";
    static final String URL_PATH = "/unfollow";

    /**
     * The user that is being followed.
     */
    private User followee;

    public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);

        this.followee = followee;
    }

    @Override
    public boolean runTask() throws IOException, TweeterRemoteException {
        UnfollowRequest request = new UnfollowRequest(authToken, followee.getAlias());
        UnfollowResponse response = Cache.getInstance().getServerFacade().unfollow(request, URL_PATH);

        boolean success = response.isSuccess();
        return success;
    }
}
