package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.util.Pair;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedTask<User> {
    private static final String LOG_TAG = "GetFollowingTask";

    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollowee, messageHandler);
    }

    @Override
    protected boolean runTask() throws IOException {
        Pair<List<User>, Boolean> pageOfUsers = getFakeData().getPageOfUsers(lastItem, limit, targetUser);
        this.items = pageOfUsers.getFirst();
        this.hasMorePages = pageOfUsers.getSecond();

        for (User u : items) {
            BackgroundTaskUtils.loadImage(u);
        }

        return true;
    }
}
