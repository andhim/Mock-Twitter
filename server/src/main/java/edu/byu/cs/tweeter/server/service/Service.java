package edu.byu.cs.tweeter.server.service;

import java.time.Instant;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.DAOFactory;


public class Service {

    protected final DAOFactory factory;

    public Service(DAOFactory factory) {
        this.factory = factory;
    }

    public boolean validateAuthToken(AuthToken authToken) {
        return checkId(authToken) && checkTimestamp(authToken);
    }

    private boolean checkId(AuthToken authToken) {
        AuthToken tokenFromDB = factory.getAuthTokenDAO().getAuthToken(authToken.getId());
        return authToken.equals(tokenFromDB);
    }

    private boolean checkTimestamp(AuthToken authToken) {
        long now = Instant.now().toEpochMilli();
        long twentyMinutesFromNow = 20 * 60 * 1000;
        long timestamp = authToken.getTimestamp();

        return now < (twentyMinutesFromNow + timestamp);
    }
}
