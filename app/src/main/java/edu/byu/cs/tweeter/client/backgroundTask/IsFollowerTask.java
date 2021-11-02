package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthenticatedTask {
    private static final String LOG_TAG = "IsFollowerTask";
    public static final String IS_FOLLOWER_KEY = "is-follower";
    static final String URL_PATH = "/isfollower";

    /**
     * The alleged follower.
     */
    private User follower;  //currUser
    /**
     * The alleged followee.
     */
    private User followee;  //targetUser

    private boolean isFollower;


    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(authToken, messageHandler);

        this.follower = follower;
        this.followee = followee;
    }

    @Override
    public boolean runTask() throws IOException, TweeterRemoteException {
        IsFollowerRequest request = new IsFollowerRequest(authToken, follower.getAlias(), followee.getAlias());
        IsFollowerResponse response = Cache.getInstance().getServerFacade().isFollower(request, URL_PATH);
        boolean success = response.isSuccess();
        if (success) {
            this.isFollower = response.isFollower();
        }
        return success;
    }

    @Override
    protected  void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
    }
}
