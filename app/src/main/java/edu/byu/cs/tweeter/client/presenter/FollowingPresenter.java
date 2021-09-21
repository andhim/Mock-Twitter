package edu.byu.cs.tweeter.client.presenter;

import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.following.FollowingFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter implements FollowService.GetFollowingObserver, UserService.GetUserObserver {

    //GetFollowingObserver
    @Override
    public void getFollowingSucceeded(List<User> followees, User lastFollowee, boolean hasMorePages, boolean isLoading) {
        this.lastFollowee = lastFollowee; //TODO: Check
        this.hasMorePages = hasMorePages; //TODO: Check
        this.isLoading = isLoading; //TODO: check

        view.setLoading(isLoading);
        view.addItems(followees);

    }

    @Override
    public void getFollowingFailed(String message) {
        view.displayErrorMessage("Failed to get following: " + message);
    }

    @Override
    public void getFollowingThrewException(Exception ex) {
        view.displayErrorMessage("Failed to get following because of exception: " + ex.getMessage());
    }

    //GetUserObserver
    @Override
    public void getUserSucceeded(User user) {
        view.navigateToUser(user);
        view.displayInfoMessage("Getting user's profile...");
    }

    @Override
    public void getUserFailed(String message) {
        view.displayErrorMessage("Failed to get user's profile: " + message);
    }

    @Override
    public void getUserThrewException(Exception ex) {
        view.displayErrorMessage("Failed to get user's profile because of exception: " + ex.getMessage());
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
                view.setLoading(true);
                new FollowService().getFollowing(authToken, targetUser, PAGE_SIZE, lastFollowee, this);
            }
        } else {
            if (!isLoading && hasMorePages) {
                isLoading = true;
                view.setLoading(true);
                new FollowService().getFollowing(authToken, targetUser, PAGE_SIZE, lastFollowee, this);
            }
        }
    }

    public void gotoUser(String alias) {
        view.displayInfoMessage("Getting user's profile...");
        new UserService().getUser(authToken, alias, this);
    }
}
