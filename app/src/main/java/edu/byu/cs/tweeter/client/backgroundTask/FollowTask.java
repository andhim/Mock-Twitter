package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask {
    private static final String LOG_TAG = "FollowTask";
    static final String URL_PATH = "/follow";


    /**
     * The user that is being followed.
     */
    private User followee;
    private User currUser;

    public FollowTask(AuthToken authToken, User currUser, User followee, Handler messageHandler) {
        super(authToken, messageHandler);

        this.currUser = currUser;
        this.followee = followee;
    }

    @Override
    public boolean runTask() throws IOException, TweeterRemoteException {
        FollowRequest request = new FollowRequest(authToken, currUser.getAlias(), followee.getAlias());
        FollowResponse response = Cache.getInstance().getServerFacade().follow(request, URL_PATH);

        boolean success = response.isSuccess();
        return success;
    }
}
