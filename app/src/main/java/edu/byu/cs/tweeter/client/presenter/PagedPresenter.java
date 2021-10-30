package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.GetFeedService;
import edu.byu.cs.tweeter.client.model.service.GetPagedService;
import edu.byu.cs.tweeter.client.model.service.GetStoryService;
import edu.byu.cs.tweeter.client.model.service.GetUserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter <T> extends Presenter{
    public PagedPresenter(PagedView view, User targetUser, AuthToken authToken) {
        super(view);
        this.targetUser = targetUser;
        this.authToken = authToken;
    }

    private User targetUser;
    private AuthToken authToken;

    protected T lastItem;
    protected boolean hasMorePages;
    protected boolean isLoading = false;

    private static final int PAGE_SIZE = 10;

    public void loadMoreItems(boolean isInitial) {
        if (isInitial) {
            if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
                isLoading = true;
                ((PagedView) this.view).setLoading(isLoading);
                getItems(authToken, targetUser, PAGE_SIZE, lastItem, (GetPagedService.GetItemObserver) this);
            }
        } else {
            if (!isLoading && hasMorePages) {
                isLoading = true;
                ((PagedView) this.view).setLoading(isLoading);
                getItems(authToken, targetUser, PAGE_SIZE, lastItem, (GetPagedService.GetItemObserver) this);
            }
        }
    }

    public void gotoUser(String alias) {
        new GetUserService().getUser(authToken, alias, (GetUserService.GetUserObserver) this);
    }

    public interface PagedView <T> extends View {
        void setLoading(boolean value);
        void addItems(List<T> items);
        void navigateToUser(User user);
    }


    public abstract void getItems(AuthToken authToken, User user, int limit, T lastStatus, GetPagedService.GetItemObserver observer);
}
