package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.CountService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter implements UserService.LogoutObserver, CountService.GetFollowersCountObserver, CountService.GetFollowingCountObserver {

    //View
    public interface View {
        void logoutUser();
        void displayErrorMessage(String type, String message);

        void displayInfoMessage(String type, String message);
        void clearPostingMessage();

        void setFollowersCount(int count);
        void setFollowingCount(int count);
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

    private View view;
    private final String LOGOUT = "logout";
    private final String FOLLOWERS_COUNT = "followersCount";
    private final String FOLLOWING_COUNT = "followingCount";

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

}
