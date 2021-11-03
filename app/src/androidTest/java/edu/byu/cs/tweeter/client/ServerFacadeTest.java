package edu.byu.cs.tweeter.client;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.util.FakeData;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class ServerFacadeTest {
    private ServerFacade serverFacade;
    private AuthToken authToken;

    private FakeData fakeData;
    private User user;

    @Before
    public void setup() {
        serverFacade = new ServerFacade();
        authToken = new AuthToken();
        fakeData = new FakeData();
        user = fakeData.getFirstUser();
    }



    @Test
    public void registerTest() {
        User expectedUser = user;

        RegisterResponse expected = new RegisterResponse(expectedUser, authToken);
        RegisterRequest request = new RegisterRequest(expectedUser.getFirstName(), expectedUser.getLastName(), expectedUser.getAlias(), "password", expectedUser.getImageUrl());

        try {
            RegisterResponse actual = serverFacade.register(request, "/register");

            assertTrue(actual.isSuccess());
            assertNull(actual.getMessage());

            assertEquals(expected.getAuthToken().getClass(), actual.getAuthToken().getClass());
            assertEquals(expected.getMessage(), actual.getMessage());
            assertEquals(expected.getClass(), actual.getClass());

            User actualUser = actual.getUser();

            //Erase the lines below for MileStone 4 and add assertEquals(expectedUser.class(), actualUser.class());
            assertEquals(expectedUser.getImageUrl(), actualUser.getImageUrl());
            assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
            assertEquals(expectedUser.getLastName(), actualUser.getLastName());
            assertEquals(expectedUser.getAlias(), actualUser.getAlias());
            assertEquals(expectedUser.getImageBytes(), actualUser.getImageBytes());
            assertEquals(expectedUser.getName(), actualUser.getName());
        } catch (IOException e) {
            assertTrue("Caught an IO exception: " + e.getMessage(), false);
        } catch (TweeterRemoteException e) {
            assertTrue("Caught a TweeterRemoteException: " + e.getMessage(), false);
        }
    }

    @Test
    public void getFollowersTest() {
        GetFollowersRequest request = new GetFollowersRequest(authToken, user.getAlias(), 10, null);
        GetFollowersResponse expected = new GetFollowersResponse(fakeData.getFakeUsers(), true);

        try {
            GetFollowersResponse actual = serverFacade.getFollowers(request, "/getfollowers");

            assertEquals(expected.isSuccess(), actual.isSuccess());
            assertEquals(expected.getMessage(), actual.getMessage());
            assertEquals(expected.getHasMorePages(), actual.getHasMorePages());

            List<User> expectedUsers = expected.getFollowers();
            List<User> actualUsers = actual.getFollowers();

            for (int i = 0; i < actualUsers.size(); i++) {
                User expectedUser = expectedUsers.get(i);
                User actualUser = actualUsers.get(i);

                assertEquals(expectedUser.getImageUrl(), actualUser.getImageUrl());
                assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
                assertEquals(expectedUser.getLastName(), actualUser.getLastName());
                assertEquals(expectedUser.getAlias(), actualUser.getAlias());
                assertEquals(expectedUser.getImageBytes(), actualUser.getImageBytes());
                assertEquals(expectedUser.getName(), actualUser.getName());
            }

        } catch (IOException e) {
            assertTrue("Caught an IO exception: " + e.getMessage(), false);
        } catch (TweeterRemoteException e) {
            assertTrue("Caught a TweeterRemoteException: " + e.getMessage(), false);
        }
    }

    @Test
    public void getFollowingCountTest() {
        GetFollowingCountRequest request = new GetFollowingCountRequest(authToken, user.getAlias());
        GetFollowingCountResponse expected = new GetFollowingCountResponse(21);

        try {
            GetFollowingCountResponse actual = serverFacade.getFollowingCount(request, "/getfollowingcount");

            assertEquals(expected.isSuccess(),actual.isSuccess());
            assertEquals(expected.getMessage(), actual.getMessage());
            assertEquals(expected.getFollowingCount(), actual.getFollowingCount());

        } catch (IOException e) {
            assertTrue("Caught an IO exception: " + e.getMessage(), false);
        } catch (TweeterRemoteException e) {
            assertTrue("Caught a TweeterRemoteException: " + e.getMessage(), false);
        }
    }

    @Test
    public void getFollowersCountTest() {
        GetFollowersCountRequest request = new GetFollowersCountRequest(authToken, user.getAlias());
        GetFollowersCountResponse expected = new GetFollowersCountResponse(21);

        try {
            GetFollowersCountResponse actual = serverFacade.getFollowersCount(request, "/getfollowerscount");

            assertEquals(expected.isSuccess(),actual.isSuccess());
            assertEquals(expected.getMessage(), actual.getMessage());
            assertEquals(expected.getFollowersCount(), actual.getFollowersCount());

        } catch (IOException e) {
            assertTrue("Caught an IO exception: " + e.getMessage(), false);
        } catch (TweeterRemoteException e) {
            assertTrue("Caught a TweeterRemoteException: " + e.getMessage(), false);
        }

    }

}
