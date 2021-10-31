package edu.byu.cs.tweeter.model.net.response;

public class FollowResponse extends Response{

    FollowResponse() {
        super(true);
    }

    FollowResponse(String message) {
        super(false, message);
    }
}
