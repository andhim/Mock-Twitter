package edu.byu.cs.tweeter.client.model.service;

import android.os.Looper;
import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService extends GetPagedService<Status>{
    //FeedFragment - GetFeedService
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
            getItems(msg, (GetItemObserver) this.observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }

    public interface GetFeedObserver extends GetPagedService.GetItemObserver<Status> {
    }

    //Story Fragment - GetStoryService
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

    public interface GetStoryObserver extends GetPagedService.GetItemObserver<Status>{
    }

    //Main Activity - PostStatus

    public interface PostStatusObserver extends ServiceObserver {
        void postStatusSucceeded();
    }

    public void postStatus(AuthToken authToken, Status newStatus, PostStatusObserver observer) {
        execute(new PostStatusTask(authToken, newStatus, new PostStatusHandler(observer)));
    }

    private class PostStatusHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to post the status: ";

        public PostStatusHandler(PostStatusObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            ((PostStatusObserver) this.observer).postStatusSucceeded();
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }
}
