package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersCountService extends Service {
    //MainActivity
    public interface GetFollowersCountObserver extends ServiceObserver{

        void getFollowersCountSucceeded(int count);
    }

    public void getFollowersCount(AuthToken authToken, User selectedUser, GetFollowersCountObserver observer) {
        execute(new GetFollowersCountTask(authToken, selectedUser, new GetFollowersCountHandler(observer)));
    }

    private class GetFollowersCountHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to get following count: ";

        public GetFollowersCountHandler(GetFollowersCountObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
            ((GetFollowersCountService.GetFollowersCountObserver) this.observer).getFollowersCountSucceeded(count);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }
}
