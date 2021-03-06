package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends AuthenticationPresenter implements UserService.RegisterObserver {

    public RegisterPresenter(RegisterView view) {
        super(view);
    }

    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64) {
        ((RegisterView) view).clearErrorMessage();
        ((RegisterView) view).clearInfoMessage();

        String message = validateRegistration(firstName, lastName, alias, password, imageBytesBase64);
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
        if (imageBytesBase64 == null || imageBytesBase64.length() == 0) {
            return "Profile image must be uploaded.";
        }

        return null;
    }

    @Override
    public void registerSucceeded(User registeredUser) {
        ((RegisterView) view).navigateToUser(registeredUser);
        ((RegisterView) view).clearErrorMessage();
        ((RegisterView) view).displayInfoMessage("Hello " + registeredUser.getName());
    }

    @Override
    public void handleFailed(String message) {
        ((RegisterView) view).displayErrorMessage(message);
    }

    public interface RegisterView extends AuthenticationPresenter.AuthenticationView {
    }
}
