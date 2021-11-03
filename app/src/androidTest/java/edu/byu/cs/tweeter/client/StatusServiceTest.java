package edu.byu.cs.tweeter.client;

import static org.junit.Assert.*;
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
    private GetStoryRequest request;
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

//        request = new GetStoryRequest(new AuthToken(), "@allen", 10, null);

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
//        Answer<Void> getStorySuccededAnswers = new Answer<Void>() {
//            @Override
//            public Void answer(InvocationOnMock invocation) throws Throwable {
//                AuthToken authTokenParam = invocation.getArgument(0);
//                User userParam = invocation.getArgument(1);
//                int limitParam = invocation.getArgument(2);
//                Status lastStatus = invocation.getArgument(3);
//                StatusService.GetStoryObserver observer = invocation.getArgument(4);
//
//                assertEquals(authTokenParam, authToken);
//                assertEquals(userParam.getName(), user.getName());
//                assertEquals(userParam.getAlias(), user.getAlias());
//                assertEquals(userParam.getImageBytes(), user.getImageBytes());
//                assertEquals(userParam.getImageUrl(), user.getImageUrl());
//                assertEquals(limitParam, limit);
//                assertNull(lastStatus);
//
//
//
//                return null;
//            }
//        };
//        Mockito.doAnswer(getStorySuccededAnswers).when(statusServiceSpy).getStory(Mockito.any(), Mockito.any(), limit, Mockito.any(), Mockito.any());


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

        List<Status> expectedStatus = new ArrayList<>();
        List<Status> allStatuses = fakeData.getFakeStatuses();

        for (int i = 0; i < 10; i++){
            expectedStatus.add(allStatuses.get(i));
        }

        Mockito.verify(mockObserver).getItemSucceeded(Mockito.anyListOf(Status.class), Mockito.anyObject(), Mockito.anyBoolean());

//        Mockito.verify(mockObserver).getItemSucceeded(expectedStatus, expectedStatus.get(9), true);
//        Mockitowhen(mockObserver.getItemSucceeded(Mockito.any(), Mockito.any(), Mockito.any()));

    }




}
