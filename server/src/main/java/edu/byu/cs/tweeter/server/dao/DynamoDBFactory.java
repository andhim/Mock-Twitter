package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

import edu.byu.cs.tweeter.server.dao.dynamoDB.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.FeedDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.FollowDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.StoryDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.UserDAO;

public class DynamoDBFactory extends DAOFactory {
    private IUserDAO userDAO;
    private IAuthTokenDAO authTokenDAO;
    private IStoryDAO storyDAO;
    private IFeedDAO feedDAO;
    private IFollowDAO followDAO;

    private DynamoDB dynamoDB;

    private DynamoDB getDynamoDB() {
        if (dynamoDB == null) {
            AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                    .withRegion("us-west-2")
                    .build();

            this.dynamoDB = new DynamoDB(client);
        }

        return dynamoDB;
    }

    @Override
    public IUserDAO getUserDAO() {
        if (userDAO == null) {
            userDAO = new UserDAO(getDynamoDB().getTable("User"));
        }

        return userDAO;
    }

    @Override
    public IAuthTokenDAO getAuthTokenDAO() {
        if (authTokenDAO == null) {
            authTokenDAO = new AuthTokenDAO(getDynamoDB().getTable("AuthToken"));
        }

        return authTokenDAO;
    }

    @Override
    public IStoryDAO getStoryDAO() {
        if (storyDAO == null) {
            storyDAO = new StoryDAO(getDynamoDB().getTable("Story"));
        }
        return storyDAO;
    }

    @Override
    public IFeedDAO getFeedDAO() {
        if (feedDAO == null) {
            feedDAO = new FeedDAO(getDynamoDB().getTable("Feed"));
        }
        return feedDAO;
    }

    @Override
    public IFollowDAO getFollowDAO() {
        if (followDAO == null) {
            followDAO = new FollowDAO(getDynamoDB().getTable("Follow"));
        }
        return followDAO;
    }


}
