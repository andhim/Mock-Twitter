package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowerRequest extends AuthenticatedRequest{
    private User currUser;
    private User selectedUser;

    private IsFollowerRequest() {}

    public IsFollowerRequest(AuthToken authToken, User currUser, User selectedUser) {
        this.authToken = authToken;
        this.currUser = currUser;
        this.selectedUser = selectedUser;
    }

    public User getCurrUser() {
        return currUser;
    }

    public void setCurrUser(User currUser) {
        this.currUser = currUser;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }
}
