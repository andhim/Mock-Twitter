package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterService {
    //Register Fragment
    public interface RegisterObserver extends ServiceObserver {
        void registerSucceeded(User registeredUser);
    }

    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64, RegisterObserver observer) {
        //Send register request.
        RegisterTask registerTask = new RegisterTask(firstName, lastName, alias, password, imageBytesBase64, new RegisterHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(registerTask);
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

            ((RegisterService.RegisterObserver) this.observer).registerSucceeded(registeredUser);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return PREFIX_MESSAGE;
        }
    }
}
