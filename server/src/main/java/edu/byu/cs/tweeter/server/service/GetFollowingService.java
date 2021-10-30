package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowingResponse;
import edu.byu.cs.tweeter.server.dao.GetFollowingDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class GetFollowingService {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link GetFollowingDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public GetFollowingResponse getFollowees(GetFollowingRequest request) {
        return getFollowingDAO().getFollowees(request); //noDao
    }

    /**
     * Returns an instance of {@link GetFollowingDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    GetFollowingDAO getFollowingDAO() {
        return new GetFollowingDAO();
    }
}
