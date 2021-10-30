package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.util.Pair;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

/**
 * Background task that creates a new user account and logs in the new user (i.e., starts a session).
 */
public class RegisterTask extends AuthenticateTask {
    private static final String LOG_TAG = "RegisterTask";
    private final String URL_PATH = "/register";
    private RegisterRequest request;
    private ServerFacade serverFacade;


    /**
     * The user's first name.
     */
    private String firstName;
    /**
     * The user's last name.
     */
    private String lastName;
    /**
     * The base-64 encoded bytes of the user's profile image.
     */
    private String image;

    public RegisterTask(RegisterRequest request, Handler messageHandler) {
        super(request, messageHandler);
        this.firstName = request.getFirstName();
        this.lastName = request.getLastName();
        this.image = request.getImageBytesBase64();
        this.request = request;
        this.serverFacade = new ServerFacade();
    }

    @Override
    public boolean runTask() throws IOException, TweeterRemoteException {
        return doRegister();
    }

    private boolean doRegister() throws IOException, TweeterRemoteException {
        RegisterResponse response = serverFacade.register(request, URL_PATH);
        boolean success = response.isSuccess();
        if (success) {
            this.user = response.getUser();
            this.authToken = response.getAuthToken();
            BackgroundTaskUtils.loadImage(user);
        }

        return success;
    }
}
