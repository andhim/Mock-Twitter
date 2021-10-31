package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UnfollowRequest extends AuthenticatedRequest {
    private User selectedUser;

    private UnfollowRequest() {}

    public UnfollowRequest(AuthToken authToken, User selectedUser) {
        this.authToken = authToken;
        this.selectedUser = selectedUser;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }
}
