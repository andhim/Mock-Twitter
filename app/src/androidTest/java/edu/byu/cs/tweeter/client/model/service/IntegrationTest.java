package edu.byu.cs.tweeter.client.model.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;

public class IntegrationTest {
    /**
     * 1. Login a User
     * 2. Post a status from the user to the server by calling the "post status" operation on the relevant Presenter.
     * 3. Verify that the "Successfully Posted!" message was displayed to the user.
     * 4. Retrieve the user's story from the server to verify that the new status was correctly appended to the user's story,
     *      and that all status details are correct.
     */

    /**
     * Question
     * 1. Do I actually talk to the backend> (What to do to verify a user is logged in?)
     */

    private ServerFacade serverFacade;
    private MainPresenter.MainView mainViewMock;
    private MainPresenter mainPresenterSpy;
    private CountDownLatch countDownLatch;

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    @Before
    public void setup() {
        serverFacade = new ServerFacade();

        mainViewMock = Mockito.mock(MainPresenter.MainView.class);
        mainPresenterSpy = Mockito.spy(new MainPresenter(mainViewMock));

        resetCountDownLatch();
    }

    @Test
    public void postStatusTest() throws IOException, TweeterRemoteException, InterruptedException {
        //login
        LoginRequest loginRequest = new LoginRequest("@warmer", "123123");
        LoginResponse loginResponse = serverFacade.login(loginRequest, "/login");

        User currUser = loginResponse.getUser();
        AuthToken authToken = loginResponse.getAuthToken();

        //Testing if login has processed correctly
        assertEquals("@warmer", currUser.getAlias());
        assertEquals("@warmer", authToken.getAlias());

        //postStatus
        Answer<Void> waitForServer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                countDownLatch.countDown();
                return null;
            }
        };

        Mockito.doAnswer(waitForServer).when(mainViewMock).displayInfoMessage("Successfully Posted!");
        String post = UUID.randomUUID().toString();
        mainPresenterSpy.postStatus(authToken, post, currUser);
        awaitCountDownLatch();

        Mockito.verify(mainViewMock).displayInfoMessage("Posting Status...");
        Mockito.verify(mainViewMock).displayInfoMessage("Successfully Posted!");

        //getStory
        GetStoryRequest getStoryRequest = new GetStoryRequest(authToken, currUser.getAlias(), 1, null);
        GetStoryResponse storyResponse = serverFacade.getStory(getStoryRequest, "/getstory");

        Status status = storyResponse.getStories().get(0);
        User statusUser = status.getUser();

        assertEquals(currUser, statusUser);
        assertEquals(post, status.getPost());
        assertEquals(0, status.getMentions().size());
        assertEquals(0, status.getUrls().size());



    }

}
