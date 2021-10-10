package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class UnfollowService {
    //Main Activity
    public interface UnfollowObserver extends ServiceOperationObserver {
        void unfollowSucceeded();
    }

    public void unfollow(AuthToken authToken, User selectedUser, UnfollowObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(authToken, selectedUser, new UnfollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(unfollowTask);
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
