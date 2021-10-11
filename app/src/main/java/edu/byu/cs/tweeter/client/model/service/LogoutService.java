package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;

public class LogoutService extends Service {

    //Main Fragment
    public interface LogoutObserver extends ServiceObserver {
        void logoutSucceeded();
    }

    public void logout(AuthToken authToken, LogoutObserver observer) {
        execute(new LogoutTask(authToken, new LogoutHandler(observer)));
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
