package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {
    private static final String LOG_TAG = "GetFollowersCountTask";
    static final String URL_PATH = "/getfollowerscount";

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected boolean runTask() throws IOException, TweeterRemoteException {
        GetFollowersCountRequest request = new GetFollowersCountRequest(authToken, targetUser.getAlias());
        GetFollowersCountResponse response = Cache.getInstance().getServerFacade().getFollowersCount(request, URL_PATH);
        boolean success = response.isSuccess();
        if (success) {
            this.count = response.getFollowersCount();
        }
        return success;
    }
}
