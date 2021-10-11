package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersService extends GetPagedService<User> {

    //FollowerFragment
    public interface GetFollowersObserver extends GetPagedService.GetItemObserver<User> {
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower, GetFollowersObserver observer) {
        execute(new GetFollowersTask(authToken,targetUser, limit, lastFollower, new GetFollowersHandler(observer)));
    }

    private class GetFollowersHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to get followers: ";

        public GetFollowersHandler(GetFollowersObserver observer) {
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

}
