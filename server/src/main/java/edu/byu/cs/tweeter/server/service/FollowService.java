package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public GetFollowingResponse getFollowees(GetFollowingRequest request) {
        if (request == null || request.getFollowerAlias() == null || request.getAuthToken() == null || request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Invalid request");
        }

        try {
            return getFollowDAO().getFollowees(request);
        } catch(Exception ex) {
            throw new RuntimeException("[BadRequest]" + ex.getMessage());
        }
    }

    /**
     * Returns the users that the user specified in the request is followed by. Uses information in
     * the request object to limit the number of followers returned and to return the next set of
     * followers after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followers.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followers.
     */
    public GetFollowersResponse getFollowers(GetFollowersRequest request) {
        if (request == null || request.getFolloweeAlias() == null || request.getAuthToken() == null || request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Invalid request" );
        }

        try {
            return getFollowDAO().getFollowers(request); //noDao
        } catch(Exception ex) {
            throw new RuntimeException("[BadRequest]" + ex.getMessage());
        }
    }

    public FollowResponse follow(FollowRequest request) {
        if (request == null || request.getAuthToken() == null || request.getSelectedUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Invalid request" );
        }
        try {
            return getFollowDAO().follow(request);
        } catch(Exception ex) {
            throw new RuntimeException("[BadRequest]" + ex.getMessage());
        }
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if (request == null || request.getAuthToken() == null || request.getSelectedUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Invalid request" );
        }
        try {
            return getFollowDAO().unfollow(request);
        } catch(Exception ex) {
            throw new RuntimeException("[BadRequest]" + ex.getMessage());
        }
    }

    public IsFollowerResponse isFolloweer(IsFollowerRequest request) {
        if (request == null || request.getAuthToken() == null || request.getCurrUserAlias() == null || request.getSelectedUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Invalid request" );
        }
        try {
            return getFollowDAO().isFollower(request);
        } catch(Exception ex) {
            throw new RuntimeException("[BadRequest]" + ex.getMessage());
        }
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowDAO() {
        return new FollowDAO();
    }


}
