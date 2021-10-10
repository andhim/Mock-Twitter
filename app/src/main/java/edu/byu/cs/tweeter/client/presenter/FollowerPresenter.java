package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.GetFollowersService;
import edu.byu.cs.tweeter.client.model.service.GetUserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter implements GetFollowersService.GetFollowersObserver, GetUserService.GetUserObserver {

    //GetUserObserver
    @Override
    public void getUserSucceeded(User user) {
        view.displayInfoMessage("Getting user's profile...");
        view.navigateToUser(user);
    }

    @Override
    public void handleFailed(String message) {
        view.displayErrorMessage(message);
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
    public void handleFailedWithOperations(String message) {
        this.isLoading = false;
        view.setLoading(isLoading);
        view.displayErrorMessage(message);
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
        new GetUserService().getUser(authToken, alias, this);
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
                new GetFollowersService().getFollowers(authToken, targetUser, PAGE_SIZE, lastFollower, this);
            }
        } else {
            if (!isLoading && hasMorePages) {
                isLoading = true;
                view.setLoading(isLoading);
                new GetFollowersService().getFollowers(authToken, targetUser, PAGE_SIZE, lastFollower, this);
            }
        }
    }


}
