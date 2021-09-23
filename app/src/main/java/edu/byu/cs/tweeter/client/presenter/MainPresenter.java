package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.CountService;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter implements UserService.LogoutObserver, CountService.GetFollowersCountObserver, CountService.GetFollowingCountObserver, FollowService.IsFollowerObserver, FollowService.UnfollowObserver {

    //View
    public interface View {
        void logoutUser();
        void displayErrorMessage(String type, String message);

        void displayInfoMessage(String type, String message);
        void clearPostingMessage();

        void setFollowersCount(int count);
        void setFollowingCount(int count);

        //IsFollower
        void setFollower();
        void setNotFollower();

        //Unfollow
        void updateUnfollow();
        void setFollowButton(boolean setEnabled);
    }

    //GetFollowersCountService
    @Override
    public void getFollowersCountSucceeded(int count) {
        view.setFollowersCount(count);
    }

    @Override
    public void getFollowersCountFailed(String message) {
        view.displayErrorMessage(FOLLOWERS_COUNT, "Failed to get followers count: " + message);
    }

    @Override
    public void getFollowersCountThrewException(Exception ex) {
        view.displayErrorMessage(FOLLOWERS_COUNT, "Failed to get followers count because of exception: " + ex.getMessage());
    }

    //GetFollowingCountServie
    @Override
    public void getFollowingCountSucceeded(int count) {
        view.setFollowingCount(count);
    }

    @Override
    public void getFollowingCountFailed(String message) {
        view.displayErrorMessage(FOLLOWING_COUNT, "Failed to get following count: " + message);
    }

    @Override
    public void getFollowingCountThrewException(Exception ex) {
        view.displayErrorMessage(FOLLOWING_COUNT, "Failed to get following count because of exception: " + ex.getMessage());
    }

    //Logout
    @Override
    public void logoutSucceeded() {
        view.logoutUser();
    }

    @Override
    public void logoutFailed(String message) {
        view.displayErrorMessage(LOGOUT, "Failed to logout: " + message);
    }

    @Override
    public void logoutThrewException(Exception ex) {
        view.displayErrorMessage(LOGOUT,"Failed to logout because of exception: " + ex.getMessage());
    }

    //IsFollower
    @Override
    public void isFollowerSucceeded(boolean isFollower) {
        if (isFollower) {
            view.setFollower();
        } else {
            view.setNotFollower();
        }
    }

    @Override
    public void isFollowerFailed(String message) {
        view.displayErrorMessage(IS_FOLLOWER, "Failed to determine following relationship: " + message);
    }

    @Override
    public void isFollowerThrewException(Exception ex) {
        view.displayErrorMessage(IS_FOLLOWER, "Failed to determine following relationship because of exception: " + ex.getMessage());
    }

    //Unfollow
    @Override
    public void unfollowSucceeded() {
        view.updateUnfollow();
        view.setFollowButton(true);
    }

    @Override
    public void unfollowFailed(String message) {
        view.displayErrorMessage(UNFOLLOW, "Failed to unfollow: " + message);
        view.setFollowButton(true);
    }

    @Override
    public void unfollowThrewException(Exception ex) {
        view.displayErrorMessage(UNFOLLOW, "Failed to unfollow because of exception: " + ex.getMessage());
        view.setFollowButton(true);
    }

    private View view;
    private final String LOGOUT = "logout";
    private final String FOLLOWERS_COUNT = "followersCount";
    private final String FOLLOWING_COUNT = "followingCount";
    private final String IS_FOLLOWER = "isFollower";
    private final String UNFOLLOW = "unfollow";

    public MainPresenter(View view) {
        this.view = view;
    }

    public void logout(AuthToken authToken) {
        view.displayInfoMessage(LOGOUT, "Logging Out...");
        new UserService().logout(authToken, this);
    }

    public void getFollowersCount(AuthToken authToken, User selectedUser) {
        new CountService().getFollowersCount(authToken, selectedUser, this);
    }

    public void getFollowingCount(AuthToken authToken, User selectedUser) {
        new CountService().getFollowingCount(authToken, selectedUser, this);
    }

    public void isFollower(AuthToken authToken, User currUser, User selectedUser) {
        new FollowService().isFollower(authToken, currUser, selectedUser, this);
    }

    public void unfollow(AuthToken authToken, User selectedUser) {
        new FollowService().unfollow(authToken, selectedUser, this);
    }

}
