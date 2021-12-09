package edu.byu.cs.tweeter.server.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.IStoryDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.StoryDAO;

public class StoryServiceTest {

    private StatusService statusServiceSpy;
    private DAOFactory factoryMock;
    private IStoryDAO storyDAOMock;
    private List<Status> mockData;

    @BeforeEach
    public void setup() {
        factoryMock = Mockito.mock(DAOFactory.class);
        storyDAOMock = Mockito.mock(StoryDAO.class);

        statusServiceSpy = Mockito.spy(new StatusService(factoryMock));
        Mockito.doReturn(true).when(statusServiceSpy).validateAuthToken(Mockito.any());
        Mockito.doReturn(storyDAOMock).when(factoryMock).getStoryDAO();

        mockData = createMockData();
    }

    private List<Status> createMockData() {
        User user = new User("@testAlias");
        List<Status> mockData = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Status status = new Status("post" + i, user, "datetime" + i, null, null);
            mockData.add(status);
        }

        return mockData;
    }

    private void mockReturnGetStory(GetStoryRequest request) {
        if (request.getLastStatus() == null) {
            int endIndex = request.getLimit()-1 >= mockData.size() ? mockData.size() : request.getLimit();
            List newArray = new ArrayList(mockData.subList(0, endIndex));
            GetStoryResponse response = new GetStoryResponse(true, newArray);
            Mockito.doReturn(response).when(storyDAOMock).getStory(Mockito.any());
        } else {
            for (int i = 0; i < mockData.size(); i++) {
                if (request.getLastStatus().getPost().equals(mockData.get(i).getPost())) {
                    int endIndex = i + request.getLimit()-1 >= mockData.size() ? mockData.size() : i + request.getLimit();
                    List newArray = new ArrayList(mockData.subList(i, endIndex));

                    GetStoryResponse response = new GetStoryResponse(endIndex == mockData.size() ? false : true, newArray);
                    Mockito.doReturn(response).when(storyDAOMock).getStory(Mockito.any());
                    break;
                }
            }
        }
    }

    @Test
    public void getStoryFirstFive() {
        //Returning first five
        GetStoryRequest request = new GetStoryRequest(new AuthToken(), "@testAlias", 5, null);
        mockReturnGetStory(request);
        GetStoryResponse actual = statusServiceSpy.getStory(request);

        Assertions.assertEquals(true, actual.getHasMorePages());

        List<Status> actualStories = actual.getStories();
        for (int i = 0; i < request.getLimit(); i++) {
            Assertions.assertEquals("post"+i, actualStories.get(i).getPost());
            Assertions.assertEquals("datetime"+i, actualStories.get(i).getDatetime());
            Assertions.assertEquals(null, actualStories.get(i).getMentions());
            Assertions.assertEquals(null, actualStories.get(i).getUrls());
            Assertions.assertEquals("@testAlias", actualStories.get(i).getUser().getAlias());
        }
    }

    @Test
    public void getStoryMiddleFive() {
        //Returning from post4 to post8
        Status fourth = new Status("post4", new User(), null, null, null);
        GetStoryRequest request = new GetStoryRequest(new AuthToken(), "@testAlias", 5, fourth);
        mockReturnGetStory(request);
        GetStoryResponse actual = statusServiceSpy.getStory(request);

        Assertions.assertEquals(true, actual.getHasMorePages());

        List<Status> actualStories = actual.getStories();
        for (int i = 4; i < 9; i++) {
            Assertions.assertEquals("post"+i, actualStories.get(i-4).getPost());
            Assertions.assertEquals("datetime"+i, actualStories.get(i-4).getDatetime());
            Assertions.assertEquals(null, actualStories.get(i-4).getMentions());
            Assertions.assertEquals(null, actualStories.get(i-4).getUrls());
            Assertions.assertEquals("@testAlias", actualStories.get(i-4).getUser().getAlias());
        }
    }

    @Test
    public void getStoryLastFour() {
        //Returning from post7 to post10
        Status sixth = new Status("post6", new User(), null, null, null);
        GetStoryRequest request = new GetStoryRequest(new AuthToken(), "@testAlias", 5, sixth);
        mockReturnGetStory(request);
        GetStoryResponse actual = statusServiceSpy.getStory(request);

        Assertions.assertEquals(false, actual.getHasMorePages());

        List<Status> actualStories = actual.getStories();
        for (int i = 6; i < 10; i++) {
            Assertions.assertEquals("post"+i, actualStories.get(i-6).getPost());
            Assertions.assertEquals("datetime"+i, actualStories.get(i-6).getDatetime());
            Assertions.assertEquals(null, actualStories.get(i-6).getMentions());
            Assertions.assertEquals(null, actualStories.get(i-6).getUrls());
            Assertions.assertEquals("@testAlias", actualStories.get(i-6).getUser().getAlias());
        }
    }
}

