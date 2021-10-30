package edu.byu.cs.tweeter.server.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowingResponse;
import edu.byu.cs.tweeter.server.dao.GetFollowingDAO;

public class GetFollowingServiceTest {

    private GetFollowingRequest request;
    private GetFollowingResponse expectedResponse;
    private GetFollowingDAO mockGetFollowingDAO;
    private GetFollowingService getFollowingServiceSpy;

    @BeforeEach
    public void setup() {
        AuthToken authToken = new AuthToken();

        User currentUser = new User("FirstName", "LastName", null);

        User resultUser1 = new User("FirstName1", "LastName1",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        User resultUser2 = new User("FirstName2", "LastName2",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
        User resultUser3 = new User("FirstName3", "LastName3",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");

        // Setup a request object to use in the tests
        request = new GetFollowingRequest(authToken, currentUser.getAlias(), 3, null);

        // Setup a mock FollowDAO that will return known responses
        expectedResponse = new GetFollowingResponse(Arrays.asList(resultUser1, resultUser2, resultUser3), false);
        mockGetFollowingDAO = Mockito.mock(GetFollowingDAO.class);
        Mockito.when(mockGetFollowingDAO.getFollowees(request)).thenReturn(expectedResponse);

        getFollowingServiceSpy = Mockito.spy(GetFollowingService.class);
        Mockito.when(getFollowingServiceSpy.getFollowingDAO()).thenReturn(mockGetFollowingDAO);
    }

    /**
     * Verify that the {@link GetFollowingService#getFollowees(GetFollowingRequest)}
     * method returns the same result as the {@link GetFollowingDAO} class.
     */
    @Test
    public void testGetFollowees_validRequest_correctResponse() {
        GetFollowingResponse response = getFollowingServiceSpy.getFollowees(request);
        Assertions.assertEquals(expectedResponse, response);
    }
}
