package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.model.service.ServiceObserver;

public abstract class BackgroundTaskHandler <T extends ServiceObserver> extends Handler {

    protected T observer;

    protected BackgroundTaskHandler(T observer) {
        this.observer = observer;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(BackgroundTask.SUCCESS_KEY);
        if (success) {
            handleSuccess(msg);
        } else if (msg.getData().containsKey(BackgroundTask.MESSAGE_KEY)) {
            String message = getFailedMessagePrefix() + msg.getData().getString(BackgroundTask.MESSAGE_KEY);
            observer.handleFailed(message);
        } else if (msg.getData().containsKey(BackgroundTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(BackgroundTask.EXCEPTION_KEY);
            String message = getFailedMessagePrefix() + ex.getMessage();
            observer.handleFailed(message);
        }
    }

    protected abstract void handleSuccess(Message msg);

    protected abstract String getFailedMessagePrefix();

}

