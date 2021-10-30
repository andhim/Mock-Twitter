package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.GetPagedService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> implements StatusService.GetStoryObserver, UserService.GetUserObserver {
    //StoryPresenter
    public StoryPresenter(StoryView view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    //GetStoryObserver
    @Override
    public void getItemSucceeded(List<Status> statuses, Status lastStatus, boolean hasMorePages) {
        this.lastItem = lastStatus;
        this.hasMorePages = hasMorePages;
        this.isLoading = false;

        ((StoryView) this.view).setLoading(false);
        ((StoryView) this.view).addItems(statuses);
    }

    @Override
    public void handleFailedWithOperations(String message) {
        this.isLoading = false;
        ((StoryView) this.view).setLoading(isLoading);
        ((StoryView) this.view).displayErrorMessage(message);
    }

    //GetUserObserver
    @Override
    public void getUserSucceeded(User user) {
        ((StoryView) this.view).displayInfoMessage("Getting user's profile...");
        ((StoryView) this.view).navigateToUser(user);
    }

    @Override
    public void handleFailed(String message) {
        view.displayErrorMessage(message);
    }


    @Override
    public void getItems(AuthToken authToken, User user, int limit, Status lastStatus, GetPagedService.GetItemObserver observer) {
        new StatusService().getStory(authToken, user, limit, lastItem, this);
    }

    //View
    public interface StoryView extends PagedPresenter.PagedView<Status> {
    }
}
