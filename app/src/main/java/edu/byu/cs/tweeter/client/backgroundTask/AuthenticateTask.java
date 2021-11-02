package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.AuthenticateRequest;

public abstract class AuthenticateTask extends BackgroundTask{
    private static final String LOG_TAG = "AuthenticateTask";

    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    /**
     * The logged-in user returned by the server
     */
    protected User user;
    /**
     * Auth token for logged-in user.
     */
    protected AuthToken authToken;
    protected String username;
    protected String password;


    protected AuthenticateTask(String username, String password, Handler messageHandler) {
        super(messageHandler);
        this.username = username;
        this.password = password;
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, this.user);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, this.authToken);
    }
}
