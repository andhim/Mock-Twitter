package edu.byu.cs.tweeter.client.presenter;

import android.graphics.pdf.PdfDocument;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.GetFeedService;
import edu.byu.cs.tweeter.client.model.service.GetPagedService;
import edu.byu.cs.tweeter.client.model.service.GetUserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status> implements GetFeedService.GetFeedObserver, GetUserService.GetUserObserver {
    //FeedPresenter
    public FeedPresenter(FeedView view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    //GetFeedObserver
    @Override
    public void getItemSucceeded(List<Status> statuses, Status lastStatus, boolean hasMorePages) {
        this.lastItem = lastStatus;
        this.hasMorePages = hasMorePages;
        this.isLoading = false;

        ((FeedView) this.view).setLoading(false);
        ((FeedView) this.view).addItems(statuses);
    }

    @Override
    public void handleFailedWithOperations(String message) {
        this.isLoading = false;
        ((FeedView) this.view).setLoading(isLoading);
        ((FeedView) this.view).displayErrorMessage(message);
    }

    //GetUserObserver
    @Override
    public void getUserSucceeded(User user) {
        ((FeedView) this.view).displayInfoMessage("Getting user's profile...");
        ((FeedView) this.view).navigateToUser(user);
    }

    @Override
    public void handleFailed(String message) {
        ((FeedView) this.view).displayErrorMessage("Failed to get user's profile: " + message);
    }

    @Override
    public void getItems(AuthToken authToken, User user, int limit, Status lastStatus, GetPagedService.GetItemObserver observer) {
        new GetFeedService().getFeed(authToken, user, limit, lastItem, this);
    }

    //View
    public interface FeedView extends PagedPresenter.PagedView<Status> {
    }
}
