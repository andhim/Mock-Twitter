package edu.byu.cs.tweeter.client.model.service;

public interface ServiceObserver {
//    void succeeded();
    void handleFailed(String message);
    void handleException(Exception ex);
}
