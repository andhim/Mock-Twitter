package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;


/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {

    private static final String LOG_TAG = "LoginTask";
    private final String URL_PATH = "/login";
    private LoginRequest request;
    private ServerFacade serverFacade;

    public LoginTask(LoginRequest request, Handler messageHandler) {
        super(request, messageHandler);
        this.request = request;
        this.serverFacade = new ServerFacade();
    }

    @Override
    public boolean runTask() throws IOException, TweeterRemoteException {
        LoginResponse response = serverFacade.login(request, URL_PATH);
        boolean success = response.isSuccess();
        if (success) {
            user = response.getUser();
            authToken = response.getAuthToken();
            BackgroundTaskUtils.loadImage(user);
        }

        return response.isSuccess();
    }

}
