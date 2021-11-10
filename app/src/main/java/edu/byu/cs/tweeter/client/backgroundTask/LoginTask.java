package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;


/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {

    private static final String LOG_TAG = "LoginTask";
    private final String URL_PATH = "/login";

    public LoginTask(String username, String password, Handler messageHandler) {
        super(username, password, messageHandler);
    }

    @Override
    public boolean runTask() throws IOException, TweeterRemoteException {
        LoginRequest request = new LoginRequest(username, password);
        LoginResponse response = Cache.getInstance().getServerFacade().login(request, URL_PATH);
        boolean success = response.isSuccess();
        if (success) {
            user = response.getUser();
            authToken = response.getAuthToken();
            BackgroundTaskUtils.loadImage(user);
        }

        return success;
    }

}
