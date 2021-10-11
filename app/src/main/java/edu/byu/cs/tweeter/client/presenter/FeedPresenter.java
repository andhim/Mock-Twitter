package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.GetFeedService;
import edu.byu.cs.tweeter.client.model.service.GetUserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter implements GetFeedService.GetFeedObserver, GetUserService.GetUserObserver {

    //GetFeedObserver
    @Override
    public void getItemSucceeded(List<Status> statuses, Status lastStatus, boolean hasMorePages) {
        this.lastStatus = lastStatus;
        this.hasMorePages = hasMorePages;
        this.isLoading = false;

        view.setLoading(false);
        view.addItems(statuses);
    }

    @Override
    public void handleFailedWithOperations(String message) {
        this.isLoading = false;
        view.setLoading(isLoading);

        view.displayErrorMessage(message);
    }

    //GetUserObserver
    @Override
    public void getUserSucceeded(User user) {
        view.displayInfoMessage("Getting user's profile...");
        view.navigateToUser(user);

    }

    @Override
    public void handleFailed(String message) {
        view.displayErrorMessage("Failed to get user's profile: " + message);
    }

    //View
    public interface View {
        void addItems(List<Status> statuses);

        void displayInfoMessage(String message);

        void displayErrorMessage(String message);

        void navigateToUser(User user);

        void setLoading(boolean value);
    }

    //FeedPresenter
    private static final int PAGE_SIZE = 10;

    private View view;
    private User user; //Feed Owner
    private AuthToken authToken;

    private Status lastStatus;
    private boolean hasMorePages;
    private boolean isLoading = false;

    public FeedPresenter(View view, User user, AuthToken authToken) {
        this.view = view;
        this.user = user;
        this.authToken = authToken;
    }

    public void gotoUser(String alias) {
        new GetUserService().getUser(authToken, alias, this);
    }

    public void loadMoreItems(boolean isInitial) {
        if (isInitial) {
            if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
                isLoading = true;
                view.setLoading(isLoading);
                new GetFeedService().getFeed(authToken, user, PAGE_SIZE, lastStatus, this);
            }
        } else {
            if (!isLoading && hasMorePages) {
                isLoading = true;
                view.setLoading(isLoading);
                new GetFeedService().getFeed(authToken, user, PAGE_SIZE, lastStatus, this);
            }
        }
    }
}
