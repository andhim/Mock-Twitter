package edu.byu.cs.tweeter.model.net.response;

public class GetFollowingCountResponse extends Response{
    private int followingCount;

    public GetFollowingCountResponse(int followingCount) {
        super(true);
        this.followingCount = followingCount;
    }

    public GetFollowingCountResponse(String message) {
        super(false, message);
    }

    public int getFollowingCount() {
        return followingCount;
    }
}
