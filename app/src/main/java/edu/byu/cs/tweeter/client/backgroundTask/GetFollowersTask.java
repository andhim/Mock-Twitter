package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.util.ByteArrayUtils;
import edu.byu.cs.tweeter.client.util.Pair;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;


/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedTask<User> {
    private static final String LOG_TAG = "GetFollowersTask";
    static final String URL_PATH = "/getfollowers";
    private final GetFollowersRequest request;
    private ServerFacade serverFacade;


    public GetFollowersTask(GetFollowersRequest request,
                            Handler messageHandler) {
        super(request, messageHandler);
        this.request = request;
        this.serverFacade = new ServerFacade();
    }

    @Override
    protected boolean runTask() throws IOException, TweeterRemoteException {
        GetFollowersResponse response = serverFacade.getFollowers(request, URL_PATH);
        boolean success = response.isSuccess();
        if (success) {
            this.items  = response.getFollowers();
            this.hasMorePages = response.getHasMorePages();
            loadImages(items);
        }

        return success;
    }
}
