package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {
    //FollowerFragment
    public interface GetFollowersObserver {
        void getFollowersSucceeded(List<User> users, User lastFollower, boolean hasMorePages);
        void getFollowersFailed(String message);
        void getFollowersThrewException(Exception ex);
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower, GetFollowersObserver observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(authToken,targetUser, limit, lastFollower, new GetFollowersHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);
    }

    private class GetFollowersHandler extends Handler {
        private GetFollowersObserver observer;

        public GetFollowersHandler(GetFollowersObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersTask.SUCCESS_KEY);
            if (success) {
                List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.FOLLOWERS_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
                User lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;

                observer.getFollowersSucceeded(followers, lastFollower, hasMorePages);
            } else if (msg.getData().containsKey(GetFollowersTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersTask.MESSAGE_KEY);
                observer.getFollowersFailed(message);
            } else if (msg.getData().containsKey(GetFollowersTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersTask.EXCEPTION_KEY);
                observer.getFollowersThrewException(ex);
            }
        }
    }

    //FollowingFragment
    public interface GetFollowingObserver {
        void getFollowingSucceeded(List<User> users, User lastFollowee, boolean hasMorePages);
        void getFollowingFailed(String message);
        void getFollowingThrewException(Exception ex);
    }

    public void getFollowing(AuthToken authToken,
                             User targetUser,
                             int limit,
                             User lastFollowee,
                             GetFollowingObserver observer) {

        GetFollowingTask getFollowingTask = new GetFollowingTask(authToken, targetUser, limit, lastFollowee, new GetFollowingHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowingTask);

    }

    private class GetFollowingHandler extends Handler {

        private GetFollowingObserver observer;

        public GetFollowingHandler(GetFollowingObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowingTask.SUCCESS_KEY);
            if (success) {
                List<User> followees = (List<User>) msg.getData().getSerializable(GetFollowingTask.FOLLOWEES_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
                User lastFollowee = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;

                observer.getFollowingSucceeded(followees, lastFollowee, hasMorePages);

            } else if (msg.getData().containsKey(GetFollowingTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingTask.MESSAGE_KEY);
                observer.getFollowingFailed(message);
            } else if (msg.getData().containsKey(GetFollowingTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingTask.EXCEPTION_KEY);
                observer.getFollowingThrewException(ex);
            }
        }
    }
}
