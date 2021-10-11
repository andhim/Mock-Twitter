package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.GetFollowersService;
import edu.byu.cs.tweeter.client.model.service.GetFollowingService;
import edu.byu.cs.tweeter.client.model.service.GetUserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User> implements GetFollowingService.GetFollowingObserver, GetUserService.GetUserObserver {
    //FollowingPresenter
    public FollowingPresenter(FollowingView view, AuthToken authToken, User targetUser) {
        super(view, targetUser, authToken);
    }

    //GetFollowingObserver
    @Override
    public void getItemSucceeded(List<User> followees, User lastFollowee, boolean hasMorePages) {
        this.lastItem = lastFollowee;
        this.hasMorePages = hasMorePages;
        this.isLoading = false;

        ((FollowingView) view).setLoading(isLoading);
        ((FollowingView) view).addItems(followees);
    }

    @Override
    public void handleFailedWithOperations(String message) {
        ((FollowingView) view).displayErrorMessage(message);
        this.isLoading = false;

        ((FollowingView) view).setLoading(isLoading);
    }

    //GetUserObserver
    @Override
    public void getUserSucceeded(User user) {
        ((FollowingView) view).displayInfoMessage("Getting user's profile...");
        ((FollowingView) view).navigateToUser(user);
    }

    @Override
    public void handleFailed(String message) {
        ((FollowingView) view).displayErrorMessage(message);
    }

    //View Interface
    public interface FollowingView extends PagedPresenter.PagedView<User> {
    }
}
