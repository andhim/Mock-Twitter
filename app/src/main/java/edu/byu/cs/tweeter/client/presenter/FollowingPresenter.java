package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.GetPagedService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User> implements FollowService.GetFollowingObserver, UserService.GetUserObserver {
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

    @Override
    public void getItems(AuthToken authToken, User user, int limit, User lastItem, GetPagedService.GetItemObserver observer) {
        new FollowService().getFollowing(authToken, user, limit, lastItem, this);
    }

    //View Interface
    public interface FollowingView extends PagedPresenter.PagedView<User> {
    }
}
