package edu.byu.cs.tweeter.client.presenter;


import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticationPresenter extends Presenter {
    public AuthenticationPresenter(AuthenticationView view) {
        super(view);
    }

    public interface AuthenticationView extends Presenter.View {
        void navigateToUser(User user);
        void clearErrorMessage();
        void clearInfoMessage();
    }
}
