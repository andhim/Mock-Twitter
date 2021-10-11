package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowerService extends Service{
    //MainActivity
    public interface IsFollowerObserver extends ServiceObserver {
        void isFollowerSucceeded(boolean isFollower);
    }

    public void isFollower(AuthToken authToken, User currUser, User selectedUser, IsFollowerObserver observer) {
        execute(new IsFollowerTask(authToken, currUser, selectedUser, new IsFollowerHandler(observer)));
    }

    private class IsFollowerHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to determine following relationship: ";

        public IsFollowerHandler(IsFollowerObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            ((IsFollowerService.IsFollowerObserver) this.observer).isFollowerSucceeded(isFollower);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }
}
