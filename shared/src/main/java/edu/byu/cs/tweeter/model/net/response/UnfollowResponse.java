package edu.byu.cs.tweeter.model.net.response;

public class UnfollowResponse extends Response{

    UnfollowResponse() {
        super(true);
    }

    UnfollowResponse(String message) {
        super(false, message);
    }
}