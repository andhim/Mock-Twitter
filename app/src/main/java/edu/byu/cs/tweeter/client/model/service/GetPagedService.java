package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.PagedTask;

public abstract class GetPagedService <T> extends Service{
    public interface GetItemObserver <T> extends ServiceOperationObserver {
        void getItemSucceeded(List<T> items, T lastItem, boolean hasMorePages);
    }

    protected void getItems(Message msg, GetItemObserver observer) {
        List<T> items = (List<T>) msg.getData().getSerializable(GetFollowersTask.ITEMS_KEY);
        boolean hasMorePages = msg.getData().getBoolean(PagedTask.MORE_PAGES_KEY);
        T lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;

        observer.getItemSucceeded(items, lastItem, hasMorePages);
    }

}