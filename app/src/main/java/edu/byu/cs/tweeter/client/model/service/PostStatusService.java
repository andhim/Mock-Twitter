package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusService extends Service {
    //Main Activity

    public interface PostStatusObserver extends ServiceObserver {
        void postStatusSucceeded();
    }

    public void postStatus(AuthToken authToken, Status newStatus, PostStatusObserver observer) {
        execute(new PostStatusTask(authToken, newStatus, new PostStatusHandler(observer)));
    }

    private class PostStatusHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to post status: ";

        public PostStatusHandler(PostStatusObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            ((PostStatusService.PostStatusObserver) this.observer).postStatusSucceeded();
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }
}
