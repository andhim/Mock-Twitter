package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusRequest extends AuthenticatedRequest{
    private Status newStatus;

    private PostStatusRequest() {}

    public PostStatusRequest(AuthToken authToken, Status newStatus) {
        this.authToken = authToken;
        this.newStatus = newStatus;
    }

    public Status getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(Status newStatus) {
        this.newStatus = newStatus;
    }
}
