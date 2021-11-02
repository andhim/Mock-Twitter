package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowRequest extends AuthenticatedRequest{
    private String selectedUserAlias;

    private FollowRequest() {}

    public FollowRequest(AuthToken authToken, String selectedUserAlias) {
        this.authToken = authToken;
        this.selectedUserAlias = selectedUserAlias;
    }

    public String getSelectedUserAlias() {
        return selectedUserAlias;
    }

    public void setSelectedUserAlias(String selectedUserAlias) {
        this.selectedUserAlias = selectedUserAlias;
    }
}
