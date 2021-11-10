package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {
    private static final String LOG_TAG = "GetFollowingCountTask";
    static final String URL_PATH = "/getfollowingcount";


    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected boolean runTask() throws IOException, TweeterRemoteException {
        GetFollowingCountRequest request = new GetFollowingCountRequest(authToken, targetUser.getAlias());
        GetFollowingCountResponse response = Cache.getInstance().getServerFacade().getFollowingCount(request, URL_PATH);
        boolean success = response.isSuccess();
        if (success) {
            this.count = response.getFollowingCount();
        }
        return success;
    }
}
