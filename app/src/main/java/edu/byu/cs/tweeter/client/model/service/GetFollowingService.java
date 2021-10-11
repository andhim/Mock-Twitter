package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingService extends Service {

    //FollowingFragment
    public interface GetFollowingObserver extends ServiceOperationObserver {
        void getFollowingSucceeded(List<User> users, User lastFollowee, boolean hasMorePages);
    }

    public void getFollowing(AuthToken authToken,
                             User targetUser,
                             int limit,
                             User lastFollowee,
                             GetFollowingObserver observer) {
        execute(new GetFollowingTask(authToken, targetUser, limit, lastFollowee, new GetFollowingHandler(observer)));
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
