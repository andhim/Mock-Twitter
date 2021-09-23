package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter implements FollowService.GetFollowersObserver, UserService.GetUserObserver {

    //GetUserObserver
    @Override
    public void getUserSucceeded(User user) {
        view.displayInfoMessage("Getting user's profile...");
        view.navigateToUser(user);
    }

    @Override
    public void getUserFailed(String message) {
        view.displayErrorMessage("Failed to get user's profile: " + message);
    }

    @Override
    public void getUserThrewException(Exception ex) {
        view.displayErrorMessage("Failed to get user's profile because of exception: " + ex.getMessage());
    }

    //GetFollowersObserver
    @Override
    public void getFollowersSucceeded(List<User> followers, User lastFollower, boolean hasMorePages) {
        this.lastFollower = lastFollower;
        this.hasMorePages = hasMorePages;
        this.isLoading = false;

        view.setLoading(this.isLoading);
        view.addItems(followers);
    }

    @Override
    public void getFollowersFailed(String message) {
        this.isLoading = false;
        view.setLoading(isLoading);
        view.displayErrorMessage("Failed to get followers: " + message);
    }

    @Override
    public void getFollowersThrewException(Exception ex) {
        this.isLoading = false;
        view.setLoading(isLoading);
        view.displayErrorMessage("Failed to get followers because of exception: " + ex.getMessage());
    }



    public interface View {
        void addItems(List<User> followers);
        void navigateToUser(User user);

        void displayErrorMessage(String message);
        void displayInfoMessage(String message);

        void setLoading(boolean value);

        void logger(String message);
    }

    //FollowerPresenter
    private static final int PAGE_SIZE = 10;

    private View view;
    private User targetUser;
    private AuthToken authToken;

    private User lastFollower;
    private boolean hasMorePages;
    private boolean isLoading = false;

    public FollowerPresenter(View view, AuthToken authToken, User targetUser) {
        this.view = view;
        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    public void gotoUser(String alias) {
        new UserService().getUser(authToken, alias, this);
    }

    public void nullChecker(User user) {
        if (user == null) {
            view.logger("user is null!");
        }
        if (user != null && user.getImageBytes() == null) {
            view.logger("image bytes are null");
        }
    }

    public void loadMoreItems(boolean isInitial) {
        if (isInitial) {
            if (!isLoading) {
                isLoading = true;
                view.setLoading(isLoading);
                new FollowService().getFollowers(authToken, targetUser, PAGE_SIZE, lastFollower, this);
            }
        } else {
            if (!isLoading && hasMorePages) {
                isLoading = true;
                view.setLoading(isLoading);
                new FollowService().getFollowers(authToken, targetUser, PAGE_SIZE, lastFollower, this);
            }
        }
    }


}
