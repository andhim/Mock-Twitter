package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingCountService extends Service {
    //Main Activity
    public interface GetFollowingCountObserver extends ServiceObserver {
        void getFollowingCountSucceeded(int count);
    }

    public void getFollowingCount(AuthToken authToken, User selectedUser, GetFollowingCountObserver observer) {
        execute(new GetFollowingCountTask(authToken, selectedUser, new GetFollowingCountHandler(observer)));
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
