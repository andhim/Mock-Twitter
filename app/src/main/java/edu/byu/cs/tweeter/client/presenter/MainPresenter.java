package edu.byu.cs.tweeter.client.presenter;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter implements UserService.LogoutObserver, FollowService.GetFollowersCountObserver, FollowService.GetFollowingCountObserver, FollowService.IsFollowerObserver, FollowService.UnfollowObserver, FollowService.FollowObserver, StatusService.PostStatusObserver {
    private static final String LOG_TAG = "Main Presenter";

    private StatusService statusService;

    public MainPresenter(MainView view) {
        super(view);
        this.statusService = getStatusService();
        //TODO: other services
    }

    public void logout(AuthToken authToken) {
        view.displayInfoMessage("Logging Out...");
        new UserService().logout(authToken, this);
    }

    public void getFollowersCount(AuthToken authToken, User selectedUser) {
        new FollowService().getFollowersCount(authToken, selectedUser, this);
    }

    public void getFollowingCount(AuthToken authToken, User selectedUser) {
        new FollowService().getFollowingCount(authToken, selectedUser, this);
    }

    public void isFollower(AuthToken authToken, User currUser, User selectedUser) {
        new FollowService().isFollower(authToken, currUser, selectedUser, this);
    }

    public void unfollow(AuthToken authToken, User currUser, User selectedUser) {
        new FollowService().unfollow(authToken, currUser, selectedUser, this);
    }

    public void follow(AuthToken authToken, User currUser, User selectedUser) {
        new FollowService().follow(authToken, currUser, selectedUser, this);
    }

    //postStatus
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void postStatus(AuthToken authToken, String post, User currUser)  {
        try {
            view.displayInfoMessage("Posting Status...");

            Status newStatus = new Status(post, currUser, getFormattedDateTime(), parseURLs(post), parseMentions(post));
            getStatusService().postStatus(authToken, newStatus, this);
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            view.displayErrorMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }

    public StatusService getStatusService() {
        if (statusService == null) {
            return new StatusService();
        }
        return statusService;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm:ss aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    private List<String> parseURLs(String post) throws MalformedURLException {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    private int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    private List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }


    @Override
    public void handleFailed(String message) {
        view.displayErrorMessage(message);
    }

    //Follow && Unfollow
    @Override
    public void handleFailedWithOperations(String message) {
        ((MainView) view).displayErrorMessage(message);
        ((MainView) view).setFollowButton(true);
    }

    //GetFollowersCountService
    @Override
    public void getFollowersCountSucceeded(int count) {
        ((MainView) view).setFollowersCount(count);
    }


    //GetFollowingCountService
    @Override
    public void getFollowingCountSucceeded(int count) {
        ((MainView) view).setFollowingCount(count);
    }

    //Logout
    @Override
    public void logoutSucceeded() {
        //Clear user data (cached data).
        Cache.getInstance().clearCache();
        ((MainView) view).logoutUser();
    }

    //IsFollower
    @Override
    public void isFollowerSucceeded(boolean isFollower) {
        if (isFollower) {
            ((MainView) view).setFollower();
        } else {
            ((MainView) view).setNotFollower();
        }
    }

    //Unfollow
    @Override
    public void unfollowSucceeded() {
        ((MainView) view).updateFollow(true);
        ((MainView) view).setFollowButton(true);
    }

    //Follow
    @Override
    public void followSucceeded() {
        ((MainView) view).updateFollow(false);
        ((MainView) view).setFollowButton(true);
    }

    //PostStatus
    @Override
    public void postStatusSucceeded() {
        ((MainView) view).displayInfoMessage("Successfully Posted!");
    }

    //View
    public interface MainView extends Presenter.View{
        void logoutUser();

        void setFollowersCount(int count);
        void setFollowingCount(int count);

        //IsFollower
        void setFollower();
        void setNotFollower();

        //Unfollow
        void updateFollow(boolean removed);
        void setFollowButton(boolean setEnabled);
    }
}
