package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;

public class UserService extends Service {

    //Following, Follower, Feed, Story Fragments-GetUser
    public interface GetUserObserver extends ServiceObserver{
        void getUserSucceeded(User user);
    }

    public void getUser(AuthToken authToken, String alias, GetUserObserver observer) {
        execute(new GetUserTask(authToken, alias, new GetUserHandler(observer)));
    }

    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private class GetUserHandler extends BackgroundTaskHandler {

        private final String PREFIX_MESSAGE = "Failed to get user's profile: ";

        public GetUserHandler(GetUserObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
            ((UserService.GetUserObserver) this.observer).getUserSucceeded(user);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }

    //LoginFragment - Login
    public interface LoginObserver extends ServiceObserver {
        void loginSucceeded(AuthToken authToken, User user);
    }

    public void login(String alias, String password, LoginObserver observer) {
        //Run a LoginTask to login the user
        LoginRequest request = new LoginRequest(alias, password);

        execute(new LoginTask(request, new LoginHandler(observer)));
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

    //Main Fragment-Logout
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

    //Register Fragment-Register
    public interface RegisterObserver extends ServiceObserver {
        void registerSucceeded(User registeredUser);
    }

    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64, RegisterObserver observer) {
        //Send register request.
        execute(new RegisterTask(firstName, lastName, alias, password, imageBytesBase64, new RegisterHandler(observer)));
    }

    private class RegisterHandler extends BackgroundTaskHandler {

        private final String PREFIX_MESSAGE = "Failed to register: ";

        public RegisterHandler(RegisterObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccess(Message msg) {
            User registeredUser = (User) msg.getData().getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) msg.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            ((RegisterObserver) this.observer).registerSucceeded(registeredUser);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }
}
