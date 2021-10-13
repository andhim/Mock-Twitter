package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetStoryService extends GetPagedService<Status> {
    public void getStory(AuthToken authToken, User user, int limit, Status lastStatus, GetStoryObserver observer) {
        execute(new GetStoryTask(authToken, user, limit, lastStatus, new GetStoryHandler(observer)));
    }

    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    private class GetStoryHandler extends BackgroundTaskHandler<GetStoryObserver> {
        private final String PREFIX_MESSAGE = "Failed to get story: ";

        public GetStoryHandler(GetStoryObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            getItems(msg, this.observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }

    //StoryFragment
    public interface GetStoryObserver extends GetPagedService.GetItemObserver<Status>{
    }
}
