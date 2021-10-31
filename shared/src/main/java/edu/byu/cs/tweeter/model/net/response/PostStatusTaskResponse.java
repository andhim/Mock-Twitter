package edu.byu.cs.tweeter.model.net.response;

public class PostStatusTaskResponse extends Response{

    PostStatusTaskResponse() {
        super(true);
    }

    PostStatusTaskResponse(String message) {
        super(false, message);
    }
}
