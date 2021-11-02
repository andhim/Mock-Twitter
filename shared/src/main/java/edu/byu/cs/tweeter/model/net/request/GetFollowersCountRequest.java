package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class GetFollowersCountRequest extends AuthenticatedRequest{
    private String selectedUserAlias;

    private GetFollowersCountRequest() {}

    public GetFollowersCountRequest(AuthToken authToken, String selectedUserAlias) {
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
