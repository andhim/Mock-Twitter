package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.server.dao.GetFollowersDAO;
import edu.byu.cs.tweeter.server.dao.GetFollowingDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class GetFollowersService {

    /**
     * Returns the users that the user specified in the request is followed by. Uses information in
     * the request object to limit the number of followers returned and to return the next set of
     * followers after any that were returned in a previous request. Uses the {@link GetFollowingDAO} to
     * get the followers.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followers.
     */
    public FollowersResponse getFollowers(FollowersRequest request) {
        return getFollowersDAO().getFollowers(request); //noDao
    }

    /**
     * Returns an instance of {@link GetFollowingDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    GetFollowersDAO getFollowersDAO() {
        return new GetFollowersDAO();
    }
}
