package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends GetPagedService<User>{
    //MainActivity
    public interface FollowObserver extends ServiceOperationObserver{
        void followSucceeded();
    }

    public void follow(AuthToken authToken, User selectedUser, FollowObserver observer) {
        execute(new FollowTask(authToken, selectedUser, new FollowHandler(observer)));
    }

    private class FollowHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to follow: ";

        public FollowHandler(FollowObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            ((FollowService.FollowObserver) this.observer).followSucceeded();
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }

    //FollowingFragment
    public interface GetFollowingObserver extends GetPagedService.GetItemObserver<User> {
    }

    public void getFollowing(AuthToken authToken,
                             User targetUser,
                             int limit,
                             User lastFollowee,
                             FollowService.GetFollowingObserver observer) {
        execute(new GetFollowingTask(authToken, targetUser, limit, lastFollowee, new FollowService.GetFollowingHandler(observer)));
    }

    private class GetFollowingHandler extends BackgroundTaskHandler {

        private final String PREFIX_MESSAGE = "Failed to get following: ";

        public GetFollowingHandler(GetFollowingObserver observer) {
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
