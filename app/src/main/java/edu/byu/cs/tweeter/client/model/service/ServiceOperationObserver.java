package edu.byu.cs.tweeter.client.model.service;

import android.app.Service;

public interface ServiceOperationObserver extends ServiceObserver {
    void handleFailedWithOperations(String message);
}
