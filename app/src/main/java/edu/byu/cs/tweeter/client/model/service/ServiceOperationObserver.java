package edu.byu.cs.tweeter.client.model.service;

public interface ServiceOperationObserver extends ServiceObserver {
    void handleFailedWithOperations(String message);
}
