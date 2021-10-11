package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingService extends GetPagedService<User> {

    //FollowingFragment
    public interface GetFollowingObserver extends GetPagedService.GetItemObserver<User> {
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
            getItems(msg, (GetItemObserver) this.observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }
}
