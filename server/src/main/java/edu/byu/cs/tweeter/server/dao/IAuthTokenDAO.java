package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface IAuthTokenDAO {
    AuthToken getAuthToken(String id);
    void deleteAuthToken(AuthToken authToken);
    void putAuthToken(AuthToken authToken);
}
