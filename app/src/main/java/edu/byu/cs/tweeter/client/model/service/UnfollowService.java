package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UnfollowService extends Service {
    //Main Activity
    public interface UnfollowObserver extends ServiceOperationObserver {
        void unfollowSucceeded();
    }

    public void unfollow(AuthToken authToken, User selectedUser, UnfollowObserver observer) {
        execute(new UnfollowTask(authToken, selectedUser, new UnfollowHandler(observer)));
    }

    private class UnfollowHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to unfollow: ";

        public UnfollowHandler(UnfollowObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            ((UnfollowService.UnfollowObserver) this.observer).unfollowSucceeded();
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }

    }
}
