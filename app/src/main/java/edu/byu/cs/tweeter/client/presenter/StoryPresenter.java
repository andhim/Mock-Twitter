package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.GetUserService;
import edu.byu.cs.tweeter.client.model.service.GetStoryService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> implements GetStoryService.GetStoryObserver, GetUserService.GetUserObserver {
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

    //View
    public interface StoryView extends PagedPresenter.PagedView<Status> {
    }
}
