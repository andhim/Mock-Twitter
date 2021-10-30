package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.AuthenticateRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;

public abstract class AuthenticateTask extends BackgroundTask{
    private static final String LOG_TAG = "AuthenticateTask";

    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    /**
     * The user's username (or "alias" or "handle"). E.g., "@susan".
     */
    private String username;
    /**
     * The user's password.
     */
    private String password;
    /**
     * The logged-in user returned by the server
     */
    protected User user;
    /**
     * Auth token for logged-in user.
     */
    protected AuthToken authToken;


    protected AuthenticateTask(AuthenticateRequest request, Handler messageHandler) {
        super(messageHandler);

        this.username = request.getUsername();
        this.password = request.getPassword();
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, this.user);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, this.authToken);
    }
}
