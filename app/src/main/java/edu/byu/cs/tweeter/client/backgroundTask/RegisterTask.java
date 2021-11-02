package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
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

    public RegisterTask(String firstName, String lastName, String username, String password, String image, Handler messageHandler) {
        super(username, password, messageHandler);
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
    }

    @Override
    public boolean runTask() throws IOException, TweeterRemoteException {
        return doRegister();
    }

    private boolean doRegister() throws IOException, TweeterRemoteException {
        RegisterRequest request = new RegisterRequest(firstName, lastName, username, password, image);
        RegisterResponse response = Cache.getInstance().getServerFacade().register(request, URL_PATH);
        boolean success = response.isSuccess();
        if (success) {
            this.user = response.getUser();
            this.authToken = response.getAuthToken();
            BackgroundTaskUtils.loadImage(user);
        }

        return success;
    }
}
