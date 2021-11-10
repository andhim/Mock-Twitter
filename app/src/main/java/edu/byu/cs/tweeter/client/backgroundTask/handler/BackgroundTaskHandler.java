package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.model.service.ServiceObserver;
import edu.byu.cs.tweeter.client.model.service.ServiceOperationObserver;

public abstract class BackgroundTaskHandler <T extends ServiceObserver> extends Handler {

    protected T observer;

    protected BackgroundTaskHandler(T observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        StringBuffer message = new StringBuffer();
        boolean success = msg.getData().getBoolean(BackgroundTask.SUCCESS_KEY);
        if (success) {
            handleSuccess(msg);
        } else if (msg.getData().containsKey(BackgroundTask.MESSAGE_KEY)) {
            message.append(getFailedMessagePrefix() + msg.getData().getString(BackgroundTask.MESSAGE_KEY));
            if (observer instanceof ServiceOperationObserver) {
                ((ServiceOperationObserver) observer).handleFailedWithOperations(message.toString());
            } else {
                observer.handleFailed(message.toString());
            }
        } else if (msg.getData().containsKey(BackgroundTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(BackgroundTask.EXCEPTION_KEY);
            message.append(getFailedMessagePrefix() + ex.getMessage());
            message.replace(message.length()-2, message.length()-1, " because of exception: ");
            observer.handleFailed(message.toString());
        }
    }

    protected abstract void handleSuccess(Message msg);

    protected abstract String getFailedMessagePrefix();

}

