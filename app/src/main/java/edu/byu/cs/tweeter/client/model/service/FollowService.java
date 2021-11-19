package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;

public class FollowService extends GetPagedService<User>{
    //MainActivity - Follow
    public interface FollowObserver extends ServiceOperationObserver{
        void followSucceeded();
    }

    public void follow(AuthToken authToken, User currUser, User selectedUser, FollowObserver observer) {
        execute(new FollowTask(authToken, currUser, selectedUser, new FollowHandler(observer)));
    }

    private class FollowHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to follow: ";

        public FollowHandler(FollowObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            ((FollowObserver) this.observer).followSucceeded();
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }

    //Main Activity - Unfollow
    public interface UnfollowObserver extends ServiceOperationObserver {
        void unfollowSucceeded();
    }

    public void unfollow(AuthToken authToken, User currUser, User selectedUser, UnfollowObserver observer) {
        execute(new UnfollowTask(authToken, currUser, selectedUser, new UnfollowHandler(observer)));
    }

    private class UnfollowHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to unfollow: ";

        public UnfollowHandler(UnfollowObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            ((UnfollowObserver) this.observer).unfollowSucceeded();
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }

    }

    //FollowingFragment-GetFollowing
    public interface GetFollowingObserver extends GetPagedService.GetItemObserver<User> {
    }

    public void getFollowing(AuthToken authToken,
                             User targetUser,
                             int limit,
                             User lastFollowee,
                             GetFollowingObserver observer) {
        GetFollowingRequest request = null;
        if (lastFollowee != null) {
            request = new GetFollowingRequest(authToken, targetUser.getAlias(), limit, lastFollowee.getAlias());
        } else {
            request = new GetFollowingRequest(authToken, targetUser.getAlias(), limit, null);
        }

        execute(new GetFollowingTask(request, new GetFollowingHandler(observer)));
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

    //FollowerFragment - GetFollowers
    public interface GetFollowersObserver extends GetPagedService.GetItemObserver<User> {
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower, GetFollowersObserver observer) {
        GetFollowersRequest request = null;
        if (lastFollower != null) {
            request = new GetFollowersRequest(authToken ,targetUser.getAlias(), limit, lastFollower.getAlias());
        } else {
            request = new GetFollowersRequest(authToken ,targetUser.getAlias(), limit, null);
        }

        execute(new GetFollowersTask(request, new GetFollowersHandler(observer)));
    }

    private class GetFollowersHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to get followers: ";

        public GetFollowersHandler(FollowService.GetFollowersObserver observer) {
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

    //MainActivity - GetFollowersCount
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
            ((GetFollowersCountObserver) this.observer).getFollowersCountSucceeded(count);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }

    //Main Activity-GetFollowingCount
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
            ((GetFollowingCountObserver) this.observer).getFollowingCountSucceeded(count);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }

    //MainActivity-IsFollowerService
    public interface IsFollowerObserver extends ServiceObserver {
        void isFollowerSucceeded(boolean isFollower);
    }

    public void isFollower(AuthToken authToken, User currUser, User selectedUser, IsFollowerObserver observer) {
        execute(new IsFollowerTask(authToken, currUser, selectedUser, new IsFollowerHandler(observer)));
    }

    private class IsFollowerHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to determine following relationship: ";

        public IsFollowerHandler(IsFollowerObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            ((IsFollowerObserver) this.observer).isFollowerSucceeded(isFollower);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }
}
