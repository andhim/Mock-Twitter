package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter implements UserService.RegisterObserver {

    public interface View {
        void navigateToUser(User user);

        void displayErrorMessage(String message);
        void clearErrorMessage();

        void displayInfoMessage(String message);
        void clearInfoMessage();
    }

    //TODO: check the methods from RegisterObserver
    @Override
    public void registerSucceeded(User registeredUser) {
        view.navigateToUser(registeredUser);
        view.clearErrorMessage(); //TODO: check if it is clearInfoMessage();
        view.displayInfoMessage("Hello " + registeredUser.getName());
    }

    @Override
    public void registerFailed(String message) {
        view.displayErrorMessage("Failed to register: " + message);
    }

    @Override
    public void registerThrewException(Exception ex) {
        view.displayErrorMessage("Failed to register because of exception: " + ex.getMessage());
    }

    private View view;

    public RegisterPresenter(View view) {
        this.view = view;
    }

    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64) {

        view.clearErrorMessage();
        view.clearInfoMessage();

        String message = validateRegistration(firstName, lastName, alias, password, imageBytesBase64); //TODO: and imageBytesBase64?va
        if (message == null) {
            view.displayInfoMessage("Registering...");
            


            new UserService().register(firstName, lastName, alias, password, imageBytesBase64, this);
        } else {
            view.displayErrorMessage("Register failed: " + message);
        }
    }

    private String validateRegistration(String firstName, String lastName, String alias, String password, String imageBytesBase64) {
        if (firstName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lastName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (alias.length() == 0) {
            return "Alias cannot be empty.";
        }
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }

        if (imageBytesBase64.length() == 0 ) {
            return "Profile image must be uploaded.";
        }

        return null;
    }


}