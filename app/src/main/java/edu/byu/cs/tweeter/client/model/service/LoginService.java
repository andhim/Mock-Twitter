package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginService extends Service{
    //Login Fragment
    public interface LoginObserver extends ServiceObserver {
        void loginSucceeded(AuthToken authToken, User user);
    }

    public void login(String alias, String password, LoginObserver observer) {
        //Run a LoginTask to login the user
        execute(new LoginTask(alias, password, new LoginHandler(observer)));
    }

    /**
     * Message handler (i.e., observer) for LoginTask
     */
    private class LoginHandler extends BackgroundTaskHandler {
        private final String PREFIX_MESSAGE = "Login failed: ";

        public LoginHandler(LoginObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            User loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
            AuthToken authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            ((LoginObserver) this.observer).loginSucceeded(authToken,loggedInUser);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }
}
