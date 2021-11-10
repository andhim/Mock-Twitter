package edu.byu.cs.tweeter.model.net.response;

public class GetFollowersCountResponse extends Response{
    private int followersCount;

    public GetFollowersCountResponse(int followersCount) {
        super(true);
        this.followersCount = followersCount;
    }

    public GetFollowersCountResponse(String message) {
        super(false, message);
    }

    public int getFollowersCount() {
        return followersCount;
    }
}
