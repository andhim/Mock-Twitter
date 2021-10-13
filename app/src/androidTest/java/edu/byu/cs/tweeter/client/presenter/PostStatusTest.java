package edu.byu.cs.tweeter.client.presenter;

import android.view.animation.AnimationSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.PostStatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class PostStatusTest {

    private MainPresenter.MainView mockMainView;
    private PostStatusService mockPostStatusService;
    private AuthToken mockAuthToken;

    private MainPresenter mainPresenterSpy;

    @Before
    public void setup() {
        mockMainView = Mockito.mock(MainPresenter.MainView.class);
        mockPostStatusService = Mockito.mock(PostStatusService.class);
        mockAuthToken = Mockito.mock(AuthToken.class);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockMainView));
        Mockito.doReturn(mockPostStatusService).when(mainPresenterSpy).getPostStatusService();
    }

    @Test
    public void testPostStatus_postStatusSucceeds() {
        String post = "test";

        //Setup the test case
        Answer<Void> postStatusSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Status status = invocation.getArgument(1);
                PostStatusService.PostStatusObserver observer = invocation.getArgument(2);

                Assert.assertEquals(invocation.getArgument(0), mockAuthToken);
                Assert.assertEquals(status.post, post);
                Assert.assertEquals(observer, mainPresenterSpy);
                observer.postStatusSucceeded();
                return null;
            }
        };

        Mockito.doAnswer(postStatusSucceededAnswer).when(mockPostStatusService).postStatus(Mockito.any(),Mockito.any(), Mockito.any());

        // Run the test case
        mainPresenterSpy.postStatus(mockAuthToken, post, new User());

        // Interview the mocks and spies to make sure they were called correctly

        // Verify that the expected methods were called on the main view
        Mockito.verify(mockMainView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockMainView).displayInfoMessage("Successfully Posted!");
    }

    @Test
    public void testPostStatus_postStatusFailed() {
        String post = "test";

        //Setup the test case
        Answer<Void> postStatusFailedAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Status status = invocation.getArgument(1);
                PostStatusService.PostStatusObserver observer = invocation.getArgument(2);

                Assert.assertEquals(invocation.getArgument(0), mockAuthToken);
                Assert.assertEquals(status.post, post);
                Assert.assertEquals(observer, mainPresenterSpy);
                observer.handleFailed("Failed to post the status: ");
                return null;
            }
        };

        Mockito.doAnswer(postStatusFailedAnswer).when(mockPostStatusService).postStatus(Mockito.any(),Mockito.any(), Mockito.any());

        // Run the test case
        mainPresenterSpy.postStatus(mockAuthToken, post, new User());

        // Interview the mocks and spies to make sure they were called correctly

        // Verify that the expected methods were called on the main view
        Mockito.verify(mockMainView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockMainView).displayErrorMessage("Failed to post the status: ");
    }

    @Test
    public void testPostStatus_postStatusThrewException() {
        String post = "test";

        Mockito.doThrow(new RuntimeException("exception")).when(mockPostStatusService).postStatus(Mockito.any(),Mockito.any(), Mockito.any());

        // Run the test case
        mainPresenterSpy.postStatus(mockAuthToken, post, new User());

        // Interview the mocks and spies to make sure they were called correctly

        // Verify that the expected methods were called on the main view
        Mockito.verify(mockMainView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockMainView).displayErrorMessage("Failed to post the status because of exception: exception");
    }
}
