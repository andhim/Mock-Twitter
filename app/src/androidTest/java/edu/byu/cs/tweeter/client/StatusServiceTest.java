package edu.byu.cs.tweeter.client;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.util.FakeData;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;

public class StatusServiceTest {
    private StatusService statusServiceSpy;
    private StatusService.GetStoryObserver mockObserver;

    private AuthToken authToken;
    private User user;
    private int limit;
    private Status lastStatus;
    private FakeData fakeData;

    private CountDownLatch countDownLatch;


    @Before
    public void setup() {
        statusServiceSpy = Mockito.spy(new StatusService());
        mockObserver = Mockito.mock(StatusService.GetStoryObserver.class);

        authToken = new AuthToken();
        fakeData = new FakeData();
        user = fakeData.getFirstUser();
        limit = 10;
        lastStatus = null;

        resetCountDownLatch();
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    @Test
    public void getStory() {
        Answer<Void> getItemsSuccededAnswers = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                countDownLatch.countDown();

                return null;
            }
        };

        Mockito.doAnswer(getItemsSuccededAnswers).when(mockObserver).getItemSucceeded(Mockito.any(), Mockito.any(), Mockito.anyBoolean());

        try {
            statusServiceSpy.getStory(authToken, user, limit, lastStatus, mockObserver);
            awaitCountDownLatch();
        } catch (InterruptedException e) {
            assertTrue("Caught an InterruptedException: " + e.getMessage(), false);
        }

        Mockito.verify(mockObserver, times(1)).getItemSucceeded(Mockito.anyListOf(Status.class), Mockito.anyObject(), Mockito.anyBoolean());
    }
}
