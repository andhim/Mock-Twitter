package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class GetUserService {

    //Following, Follower, Feed, Story Fragments
    public interface GetUserObserver extends ServiceObserver{
        void getUserSucceeded(User user);
    }

    public void getUser(AuthToken authToken, String alias, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(authToken, alias, new GetUserHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getUserTask);
    }

    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private class GetUserHandler extends BackgroundTaskHandler {

        private final String PREFIX_MESSAGE = "Failed to get user's profile: ";

        public GetUserHandler(GetUserObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);

            ((GetUserService.GetUserObserver) this.observer).getUserSucceeded(user);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }
}
