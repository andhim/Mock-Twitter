package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;


/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedTask<Status> {
    private static final String LOG_TAG = "GetStoryTask";
    static final String URL_PATH = "/getstory";
    private GetStoryRequest request;

    //For Teting
    public GetStoryTask(GetStoryRequest request, Handler messageHandler) {
        super(request, messageHandler);
        this.request = request;
    }

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected boolean runTask() throws IOException, TweeterRemoteException {
        GetStoryRequest request = new GetStoryRequest(authToken, targetUser.getAlias(), limit, lastItem);
        GetStoryResponse response = Cache.getInstance().getServerFacade().getStory(request, URL_PATH);
        boolean success = response.isSuccess();
        if (success) {
            this.items = response.getStories();
            this.hasMorePages = response.getHasMorePages();
            for (Status s : items) {
                BackgroundTaskUtils.loadImage(s.getUser());
            }
        }
        return success;
    }
}