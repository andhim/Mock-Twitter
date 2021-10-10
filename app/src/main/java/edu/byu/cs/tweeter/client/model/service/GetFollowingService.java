package edu.byu.cs.tweeter.client.model.service;

import android.app.Service;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingService {

    //FollowingFragment
    public interface GetFollowingObserver extends ServiceOperationObserver {
        void getFollowingSucceeded(List<User> users, User lastFollowee, boolean hasMorePages);
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

    private class GetFollowingHandler extends BackgroundTaskHandler {

        private final String PREFIX_MESSAGE = "Failed to get following: ";

        public GetFollowingHandler(GetFollowingObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            List<User> followees = (List<User>) msg.getData().getSerializable(GetFollowingTask.ITEMS_KEY);
            boolean hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
            User lastFollowee = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;

            ((GetFollowingService.GetFollowingObserver) this.observer).getFollowingSucceeded(followees, lastFollowee, hasMorePages);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }
}
