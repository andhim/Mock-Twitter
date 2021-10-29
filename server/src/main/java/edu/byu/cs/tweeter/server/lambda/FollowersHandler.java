package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.server.service.GetFollowersService;

/**
 * An AWS lambda function that returns the users a user is followed by.
 */
public class FollowersHandler implements RequestHandler<FollowersRequest, FollowersResponse> {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request.
     *
     * @param request contains the data required to fulfill the request.
     * @param context the lambda context.
     * @return the followees.
     */
    @Override
    public FollowersResponse handleRequest(FollowersRequest request, Context context) {
        GetFollowersService service = new GetFollowersService();
        return service.getFollowers(request);
    }
}
