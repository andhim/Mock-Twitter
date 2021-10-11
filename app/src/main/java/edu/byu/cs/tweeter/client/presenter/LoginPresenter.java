package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.LoginService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends AuthenticationPresenter implements LoginService.LoginObserver {

    @Override
    public void loginSucceeded(AuthToken authToken, User user) {
        ((LoginView) view).navigateToUser(user);
        ((LoginView) view).clearErrorMessage();
        ((LoginView) view).displayInfoMessage("Hello " + user.getName());
    }

    @Override
    public void handleFailed(String message) {
        ((LoginView) view).displayErrorMessage(message);
    }


    public LoginPresenter(LoginView view) {
        super(view);
    }

    public void login(String alias, String password) {

        ((LoginView) view).clearErrorMessage();
        ((LoginView) view).clearInfoMessage();

        String message = validateLogin(alias, password);
        if (message == null) {
            ((LoginView) view).displayInfoMessage("Logging In...");
            new LoginService().login(alias,password,this);
        } else {
            ((LoginView) view).displayErrorMessage("Login failed: " + message);
        }
    }

    private String validateLogin(String alias, String password) {
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }

    public interface LoginView extends AuthenticationPresenter.AuthenticationView {
    }
}
