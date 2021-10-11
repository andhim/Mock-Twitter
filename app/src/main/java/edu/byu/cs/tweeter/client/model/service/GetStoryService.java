package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetStoryService extends Service {
    //StoryFragment
    public interface GetStoryObserver extends ServiceOperationObserver{
        void getStorySucceeded(List<Status> statuses, Status lastStatus, boolean hasMorePages);
    }

    public void getStory(AuthToken authToken, User user, int limit, Status lastStatus, GetStoryObserver observer) {
        execute(new GetStoryTask(authToken, user, limit, lastStatus, new GetStoryHandler(observer)));
    }

    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    private class GetStoryHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to get story: ";

        public GetStoryHandler(GetStoryObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetFeedTask.ITEMS_KEY);
            boolean hasMorePages = msg.getData().getBoolean(GetFeedTask.MORE_PAGES_KEY);
            Status lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;

            ((GetStoryService.GetStoryObserver) this.observer).getStorySucceeded(statuses, lastStatus, hasMorePages);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }

}
