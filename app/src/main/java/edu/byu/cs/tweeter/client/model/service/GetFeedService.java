package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFeedService extends GetPagedService {
    //FeedFragment
    public interface GetFeedObserver extends GetPagedService.GetItemObserver<Status> {
    }

    public void getFeed(AuthToken authToken, User user, int limit, Status lastStatus, GetFeedObserver observer) {
        execute(new GetFeedTask(authToken, user, limit, lastStatus, new GetFeedHandler(observer)));
    }

    /**
     * Message handler (i.e., observer) for GetFeedTask.
     */
    private class GetFeedHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to get feed: ";

        public GetFeedHandler(GetFeedObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            getItems(msg, (GetPagedService.GetItemObserver) this.observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }
}
