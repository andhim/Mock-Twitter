package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UnfollowRequest extends AuthenticatedRequest {
    private String selectedUserAlias;
    private String currUserAlias;

    private UnfollowRequest() {}

    public UnfollowRequest(AuthToken authToken, String currUserAlias, String selectedUserAlias) {
        this.authToken = authToken;
        this.currUserAlias = currUserAlias;
        this.selectedUserAlias = selectedUserAlias;
    }

    public String getSelectedUserAlias() {
        return selectedUserAlias;
    }

    public void setSelectedUserAlias(String selectedUserAlias) {
        this.selectedUserAlias = selectedUserAlias;
    }

    public String getCurrUserAlias() {
        return currUserAlias;
    }

    public void setCurrUserAlias(String currUserAlias) {
        this.currUserAlias = currUserAlias;
    }
}
