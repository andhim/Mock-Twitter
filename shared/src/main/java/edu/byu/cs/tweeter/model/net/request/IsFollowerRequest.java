package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowerRequest extends AuthenticatedRequest{
    private String currUserAlias;
    private String selectedUserAlias;

    private IsFollowerRequest() {}

    public IsFollowerRequest(AuthToken authToken, String currUserAlias, String selectedUserAlias) {
        this.authToken = authToken;
        this.currUserAlias = currUserAlias;
        this.selectedUserAlias = selectedUserAlias;
    }

    public String getCurrUserAlias() {
        return currUserAlias;
    }

    public void setCurrUserAlias(String currUserAlias) {
        this.currUserAlias = currUserAlias;
    }

    public String getSelectedUserAlias() {
        return selectedUserAlias;
    }

    public void setSelectedUserAlias(String selectedUserAlias) {
        this.selectedUserAlias = selectedUserAlias;
    }
}
