package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class CountService {
    //MainActivity
    public interface GetFollowersCountObserver {

        void getFollowersCountSucceeded(int count);
        void getFollowersCountFailed(String message);
        void getFollowersCountThrewException(Exception ex);
    }

    public void getFollowersCount(AuthToken authToken, User selectedUser, GetFollowersCountObserver observer) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(authToken, selectedUser, new GetFollowersCountHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followersCountTask);
    }

    private class GetFollowersCountHandler extends Handler {
        private GetFollowersCountObserver observer;

        public GetFollowersCountHandler(GetFollowersCountObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
                observer.getFollowersCountSucceeded(count);

            } else if (msg.getData().containsKey(GetFollowersCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersCountTask.MESSAGE_KEY);
                observer.getFollowersCountFailed(message);
            } else if (msg.getData().containsKey(GetFollowersCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersCountTask.EXCEPTION_KEY);
                observer.getFollowersCountThrewException(ex);
            }
        }
    }

    //Main Activity
    public interface GetFollowingCountObserver {
        void getFollowingCountSucceeded(int count);
        void getFollowingCountFailed(String message);
        void getFollowingCountThrewException(Exception ex);
    }

    public void getFollowingCount(AuthToken authToken, User selectedUser, GetFollowingCountObserver observer) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(authToken, selectedUser, new GetFollowingCountHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followingCountTask);
    }

    private class GetFollowingCountHandler extends Handler {
        private GetFollowingCountObserver observer;

        public GetFollowingCountHandler(GetFollowingCountObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowingCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
                observer.getFollowingCountSucceeded(count);
            } else if (msg.getData().containsKey(GetFollowingCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingCountTask.MESSAGE_KEY);
                observer.getFollowingCountFailed(message);
            } else if (msg.getData().containsKey(GetFollowingCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingCountTask.EXCEPTION_KEY);
                observer.getFollowingCountThrewException(ex);
            }
        }
    }



}
