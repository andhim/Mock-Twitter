package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowRequest extends AuthenticatedRequest{
    private User selectedUser;

    private FollowRequest() {}

    public FollowRequest(AuthToken authToken, User selectedUser) {
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
