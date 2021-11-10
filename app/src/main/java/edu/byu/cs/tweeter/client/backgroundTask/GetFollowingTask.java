package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowingResponse;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedTask<User> {
    private static final String LOG_TAG = "GetFollowingTask";
    static final String URL_PATH = "/getfollowing";
    private final GetFollowingRequest request;
    private ServerFacade serverFacade;

    public GetFollowingTask(GetFollowingRequest request,
                            Handler messageHandler) {
        super(request, messageHandler);
        this.request = request;
        this.serverFacade = new ServerFacade();
    }

    @Override
    protected boolean runTask() throws IOException, TweeterRemoteException {
        GetFollowingResponse response = serverFacade.getFollowees(request, URL_PATH);
        boolean success = response.isSuccess();
        if (success) {
            this.items  = response.getFollowees();
            this.hasMorePages = response.getHasMorePages();
            loadImages(items);
        }

        return success;
    }
}
