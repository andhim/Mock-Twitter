package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.GetFollowersService;
import edu.byu.cs.tweeter.client.model.service.GetFollowingService;
import edu.byu.cs.tweeter.client.model.service.GetUserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter implements GetFollowingService.GetFollowingObserver, GetUserService.GetUserObserver {

    //GetFollowingObserver
    @Override
    public void getItemSucceeded(List<User> followees, User lastFollowee, boolean hasMorePages) {
        this.lastFollowee = lastFollowee;
        this.hasMorePages = hasMorePages;
        this.isLoading = false;

        view.setLoading(isLoading);
        view.addItems(followees);
    }

    @Override
    public void handleFailedWithOperations(String message) {
        view.displayErrorMessage(message);
        this.isLoading = false;

        view.setLoading(isLoading);
    }

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

    //View Interface
    public interface View {

        void addItems(List<User> followees);
        void navigateToUser(User user);

        void displayErrorMessage(String message);
        void displayInfoMessage(String message);

        void setLoading(boolean value);
    }

    //FollowingPresenter
    private static final int PAGE_SIZE = 10;

    private View view;
    private User targetUser;
    private AuthToken authToken;

    private User lastFollowee;
    private boolean hasMorePages;
    private boolean isLoading = false;

    public FollowingPresenter(View view, AuthToken authToken, User targetUser) {
        this.view = view;
        this.targetUser = targetUser;
        this.authToken = authToken;
    }

    public void loadMoreItems(boolean isInitial) {
        if (isInitial) {
            if (!isLoading) {
                isLoading = true;
                view.setLoading(isLoading);
                new GetFollowingService().getFollowing(authToken, targetUser, PAGE_SIZE, lastFollowee, this);
            }
        } else {
            if (!isLoading && hasMorePages) {
                isLoading = true;
                view.setLoading(isLoading);
                new GetFollowingService().getFollowing(authToken, targetUser, PAGE_SIZE, lastFollowee, this);
            }
        }
    }

    public void gotoUser(String alias) {
        new GetUserService().getUser(authToken, alias, this);
    }
}
