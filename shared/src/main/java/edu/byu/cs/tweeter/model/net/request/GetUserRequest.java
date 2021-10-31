package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class GetUserRequest extends AuthenticatedRequest{
    private String alias;

    private GetUserRequest() {}

    public GetUserRequest(AuthToken authToken, String alias) {
        this.alias = alias;
        this.authToken = getAuthToken();
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
