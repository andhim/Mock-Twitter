package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;

public class LogoutService  {

    //Main Fragment
    public interface LogoutObserver extends ServiceObserver {
        void logoutSucceeded();
    }

    public void logout(AuthToken authToken, LogoutObserver observer) {
        LogoutTask logoutTask = new LogoutTask(authToken, new LogoutHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(logoutTask);
    }

    private class LogoutHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Failed to logout: ";

        public LogoutHandler(LogoutObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            ((LogoutObserver) this.observer).logoutSucceeded();
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }
}
