package edu.byu.cs.tweeter.server.lambda;


import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDBFactory;

public class HandlerConfig {

    private static DAOFactory factory;

    public static DAOFactory getFactory() {
        if (factory == null) {
            factory = new DynamoDBFactory();
        }
        return factory;
    }



}

