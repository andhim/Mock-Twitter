package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dynamoDB.FollowDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends Service {

    public FollowService(DAOFactory factory) {
        super(factory);
    }

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
            return factory.getFollowDAO().getFollowees(request);
        } catch(Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
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
            return factory.getFollowDAO().getFollowers(request.getFolloweeAlias(), request.getLimit(), request.getLastFollowerAlias());
        } catch(Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
        }
    }

    public FollowResponse follow(FollowRequest request) {
        if (request == null ||
                request.getCurrImageURL() == null ||
                request.getFolloweeImageURL() == null ||
                request.getFolloweeAlias() == null ||
                request.getFolloweeName() == null ||
                request.getCurrUserName() == null ||
                request.getAuthToken() == null ||
                request.getCurrUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Invalid request" );
        }
        try {
            FollowResponse response = factory.getFollowDAO().follow(request);
            factory.getUserDAO().incrementFollow(request);

            return response;
        } catch(Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
        }
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if (request == null ||
                request.getSelectedUserAlias() == null ||
                request.getAuthToken() == null ||
                request.getCurrUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Invalid request" );
        }
        try {
            UnfollowResponse response = factory.getFollowDAO().unfollow(request);
            factory.getUserDAO().decrementFollow(request);

            return response;
        } catch(Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
        }
    }

    public IsFollowerResponse isFolloweer(IsFollowerRequest request) {
        if (request == null || request.getAuthToken() == null || request.getCurrUserAlias() == null || request.getSelectedUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Invalid request" );
        }
        try {
            return factory.getFollowDAO().isFollower(request);
        } catch(Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
        }
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        if (request == null || request.getAuthToken() == null || request.getSelectedUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Invalid request" );
        }
        try {
            //followersCount on UserDAO
            return factory.getUserDAO().getFollowersCount(request);
        } catch(Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
        }
    }

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        if (request == null || request.getAuthToken() == null || request.getSelectedUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Invalid request" );
        }
        try {
            //followingCount on UserDAO
            return factory.getUserDAO().getFollowingCount(request);
        } catch(Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
        }
    }

}
