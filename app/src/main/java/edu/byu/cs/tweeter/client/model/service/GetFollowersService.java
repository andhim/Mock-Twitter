package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersService {

    //FollowerFragment
    public interface GetFollowersObserver extends ServiceOperationObserver {
        void getFollowersSucceeded(List<User> users, User lastFollower, boolean hasMorePages);
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower, GetFollowersObserver observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(authToken,targetUser, limit, lastFollower, new GetFollowersHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);
    }

    private class GetFollowersHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to get followers: ";

        public GetFollowersHandler(GetFollowersObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.ITEMS_KEY);
            boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
            User lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;

            ((GetFollowersService.GetFollowersObserver) this.observer).getFollowersSucceeded(followers, lastFollower, hasMorePages);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }

}
