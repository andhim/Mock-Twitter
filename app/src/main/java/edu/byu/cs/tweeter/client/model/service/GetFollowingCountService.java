package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingCountService {
    //Main Activity
    public interface GetFollowingCountObserver extends ServiceObserver {
        void getFollowingCountSucceeded(int count);
    }

    public void getFollowingCount(AuthToken authToken, User selectedUser, GetFollowingCountObserver observer) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(authToken, selectedUser, new GetFollowingCountHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followingCountTask);
    }

    private class GetFollowingCountHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to get following count: ";

        public GetFollowingCountHandler(GetFollowingCountObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            int count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
            ((GetFollowingCountService.GetFollowingCountObserver) this.observer).getFollowingCountSucceeded(count);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }
}
