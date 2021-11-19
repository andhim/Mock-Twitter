package edu.byu.cs.tweeter.server.dao;

public abstract class DAOFactory {
    public abstract IUserDAO getUserDAO();
    public abstract IAuthTokenDAO getAuthTokenDAO();
    public abstract IStoryDAO getStoryDAO();
    public abstract IFeedDAO getFeedDAO();
    public abstract IFollowDAO getFollowDAO();

}
