package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.GetFeedService;
import edu.byu.cs.tweeter.client.model.service.GetPagedService;
import edu.byu.cs.tweeter.client.model.service.GetUserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter extends PagedPresenter<User> implements FollowService.GetFollowersObserver, GetUserService.GetUserObserver {
    //FollowerPresenter
    public FollowerPresenter(FollowerView view, AuthToken authToken, User targetUser) {
        super(view, targetUser, authToken);
    }

    //GetUserObserver
    @Override
    public void getUserSucceeded(User user) {
        ((FollowerView) this.view).displayInfoMessage("Getting user's profile...");
        ((FollowerView) this.view).navigateToUser(user);
    }

    @Override
    public void handleFailed(String message) {
        ((FollowerView) this.view).displayErrorMessage(message);
    }

    //GetFollowersObserver
    @Override
    public void getItemSucceeded(List<User> followers, User lastFollower, boolean hasMorePages) {
        this.lastItem = lastFollower;
        this.hasMorePages = hasMorePages;
        this.isLoading = false;

        ((FollowerView) this.view).setLoading(this.isLoading);
        ((FollowerView) this.view).addItems(followers);
    }

    @Override
    public void handleFailedWithOperations(String message) {
        this.isLoading = false;
        ((FollowerView) this.view).setLoading(isLoading);
        ((FollowerView) this.view).displayErrorMessage(message);
    }

    @Override
    public void getItems(AuthToken authToken, User user, int limit, User lastItem, GetPagedService.GetItemObserver observer) {
        new FollowService().getFollowers(authToken, user, limit, lastItem, this);
    }

    public void nullChecker(User user) {
        if (user == null) {
            ((FollowerView) this.view).logger("user is null!");
        }
        if (user != null && user.getImageBytes() == null) {
            ((FollowerView) this.view).logger("image bytes are null");
        }
    }

    public interface FollowerView extends PagedPresenter.PagedView<User> {
        void logger(String message);
    }
}
