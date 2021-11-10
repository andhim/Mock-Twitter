package edu.byu.cs.tweeter.client.presenter;

import static org.mockito.Mockito.times;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class PostStatusTest {

    private MainPresenter.MainView mockMainView;
    private StatusService mockStatusService;
    private AuthToken mockAuthToken;

    private MainPresenter mainPresenterSpy;

    @Before
    public void setup() {
        mockMainView = Mockito.mock(MainPresenter.MainView.class);
        mockStatusService = Mockito.mock(StatusService.class);
        mockAuthToken = Mockito.mock(AuthToken.class);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockMainView));
        Mockito.doReturn(mockStatusService).when(mainPresenterSpy).getStatusService();
    }

    @Test
    public void testPostStatus_postStatusSucceeds() {
        String post = "test";

        //Setup the test case
        Answer<Void> postStatusSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Status status = invocation.getArgument(1);
                StatusService.PostStatusObserver observer = invocation.getArgument(2);

                Assert.assertEquals(invocation.getArgument(0), mockAuthToken);
                Assert.assertEquals(status.post, post);
                Assert.assertEquals(observer, mainPresenterSpy);
                observer.postStatusSucceeded();
                return null;
            }
        };

        Mockito.doAnswer(postStatusSucceededAnswer).when(mockStatusService).postStatus(Mockito.any(),Mockito.any(), Mockito.any());

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
                StatusService.PostStatusObserver observer = invocation.getArgument(2);

                Assert.assertEquals(invocation.getArgument(0), mockAuthToken);
                Assert.assertEquals(status.post, post);
                Assert.assertEquals(observer, mainPresenterSpy);
                observer.handleFailed("Failed to post the status: ");
                return null;
            }
        };

        Mockito.doAnswer(postStatusFailedAnswer).when(mockStatusService).postStatus(Mockito.any(),Mockito.any(), Mockito.any());

        // Run the test case
        mainPresenterSpy.postStatus(mockAuthToken, post, new User());


        // Verify that the expected methods were called on the main view
        Mockito.verify(mockMainView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockMainView).displayErrorMessage("Failed to post the status: ");
    }

    @Test
    public void testPostStatus_postStatusThrewException() {
        String post = "test";


        //For parseURLs && parseMentions
        Mockito.doThrow(new RuntimeException("exception")).when(mockStatusService).postStatus(Mockito.any(),Mockito.any(), Mockito.any());
        mainPresenterSpy.postStatus(mockAuthToken, post, new User());
        Mockito.verify(mockMainView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockMainView).displayErrorMessage("Failed to post the status because of exception: exception");

        Answer<Void> postStatusExceptionAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Status status = invocation.getArgument(1);
                StatusService.PostStatusObserver observer = invocation.getArgument(2);

                Assert.assertEquals(invocation.getArgument(0), mockAuthToken);
                Assert.assertEquals(status.post, post);
                Assert.assertEquals(observer, mainPresenterSpy);

                observer.handleFailed("Failed to post the status because of the exception: ");
                return null;
            }
        };

        Mockito.doAnswer(postStatusExceptionAnswer).when(mockStatusService).postStatus(Mockito.any(),Mockito.any(), Mockito.any());

        // Run the test case
        mainPresenterSpy.postStatus(mockAuthToken, post, new User());

        // Verify that the expected methods were called on the main view
        Mockito.verify(mockMainView, times(2)).displayInfoMessage("Posting Status...");
        Mockito.verify(mockMainView).displayErrorMessage("Failed to post the status because of the exception: ");


    }

}
