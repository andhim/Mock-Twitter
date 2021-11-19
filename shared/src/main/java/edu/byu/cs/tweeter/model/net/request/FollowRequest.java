package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class FollowRequest extends AuthenticatedRequest{
    private String followeeAlias;
    private String followeeName;
    private String followeeImageURL;
    private String currUserAlias;
    private String currUserName;
    private String currImageURL;

    private FollowRequest() {}

    public FollowRequest(AuthToken authToken, String currUserName, String currImageURL, String currUserAlias, String followeeName, String followeeImageURL, String followeeAlias) {
        this.authToken = authToken;
        this.currUserAlias = currUserAlias;
        this.followeeName = followeeName;
        this.followeeImageURL = followeeImageURL;
        this.followeeAlias = followeeAlias;
        this.currUserName = currUserName;
        this.currImageURL = currImageURL;
    }

    public String getFolloweeName() {
        return followeeName;
    }

    public void setFolloweeName(String followeeName) {
        this.followeeName = followeeName;
    }

    public String getFolloweeImageURL() {
        return followeeImageURL;
    }

    public void setFolloweeImageURL(String followeeImageURL) {
        this.followeeImageURL = followeeImageURL;
    }

    public String getCurrUserName() {
        return currUserName;
    }

    public void setCurrUserName(String currUserName) {
        this.currUserName = currUserName;
    }

    public String getCurrImageURL() {
        return currImageURL;
    }

    public void setCurrImageURL(String currImageURL) {
        this.currImageURL = currImageURL;
    }

    public String getCurrUserAlias() {
        return currUserAlias;
    }

    public void setCurrUserAlias(String currUserAlias) {
        this.currUserAlias = currUserAlias;
    }

    public String getFolloweeAlias() {
        return followeeAlias;
    }

    public void setFolloweeAlias(String followeeAlias) {
        this.followeeAlias = followeeAlias;
    }
}
