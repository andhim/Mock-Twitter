package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends Service{
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
}
