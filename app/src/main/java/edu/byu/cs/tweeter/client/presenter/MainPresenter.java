package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.CountService;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.LogoutService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter implements LogoutService.LogoutObserver, CountService.GetFollowersCountObserver, CountService.GetFollowingCountObserver, FollowService.IsFollowerObserver, FollowService.UnfollowObserver, FollowService.FollowObserver, StatusService.PostStatusObserver {

    //View
    public interface View {
        void logoutUser();
        void displayErrorMessage(String message);

        void displayInfoMessage(String message);

        void setFollowersCount(int count);
        void setFollowingCount(int count);

        //IsFollower
        void setFollower();
        void setNotFollower();

        //Unfollow
        void updateFollow(boolean removed);
        void setFollowButton(boolean setEnabled);
    }

    //GetFollowersCountService
    @Override
    public void getFollowersCountSucceeded(int count) {
        view.setFollowersCount(count);
    }

    @Override
    public void getFollowersCountFailed(String message) {
        view.displayErrorMessage("Failed to get followers count: " + message);
    }

    @Override
    public void getFollowersCountThrewException(Exception ex) {
        view.displayErrorMessage("Failed to get followers count because of exception: " + ex.getMessage());
    }

    //GetFollowingCountService
    @Override
    public void getFollowingCountSucceeded(int count) {
        view.setFollowingCount(count);
    }

    @Override
    public void getFollowingCountFailed(String message) {
        view.displayErrorMessage("Failed to get following count: " + message);
    }

    @Override
    public void getFollowingCountThrewException(Exception ex) {
        view.displayErrorMessage("Failed to get following count because of exception: " + ex.getMessage());
    }

    //Logout
    @Override
    public void logoutSucceeded() {
        view.logoutUser();
    }

    @Override
    public void handleFailed(String message) {
        view.displayErrorMessage(message);
    }

    //IsFollower
    @Override
    public void isFollowerSucceeded(boolean isFollower) {
        if (isFollower) {
            view.setFollower();
        } else {
            view.setNotFollower();
        }
    }

    @Override
    public void isFollowerFailed(String message) {
        view.displayErrorMessage("Failed to determine following relationship: " + message);
    }

    @Override
    public void isFollowerThrewException(Exception ex) {
        view.displayErrorMessage("Failed to determine following relationship because of exception: " + ex.getMessage());
    }

    //Unfollow
    @Override
    public void unfollowSucceeded() {
        view.updateFollow(true);
        view.setFollowButton(true);
    }

    @Override
    public void unfollowFailed(String message) {
        view.displayErrorMessage("Failed to unfollow: " + message);
        view.setFollowButton(true);
    }

    @Override
    public void unfollowThrewException(Exception ex) {
        view.displayErrorMessage("Failed to unfollow because of exception: " + ex.getMessage());
        view.setFollowButton(true);
    }

    //Follow
    @Override
    public void followSucceeded() {
    view.updateFollow(false);
    view.setFollowButton(true);
    }

    @Override
    public void followFailed(String message) {
        view.displayErrorMessage("Failed to follow: " + message);
        view.setFollowButton(true);
    }

    @Override
    public void followThrewException(Exception ex) {
        view.displayErrorMessage("Failed to follow because of exception: " + ex.getMessage());
        view.setFollowButton(true);
    }

    //PostStatus

    @Override
    public void postStatusSucceeded() {
        view.displayInfoMessage("Successfully Posted!");
    }

    @Override
    public void postStatusFailed(String message) {
        view.displayErrorMessage("Failed to post status: " + message);
    }

    @Override
    public void postStatusThrewException(Exception ex) {
        view.displayErrorMessage("Failed to post status because of exception: " + ex.getMessage());
    }

    private View view;
    private static final String LOG_TAG = "MainActivity";

    public MainPresenter(View view) {
        this.view = view;
    }

    public void logout(AuthToken authToken) {
        view.displayInfoMessage("Logging Out...");
        new LogoutService().logout(authToken, this);
    }

    public void getFollowersCount(AuthToken authToken, User selectedUser) {
        new CountService().getFollowersCount(authToken, selectedUser, this);
    }

    public void getFollowingCount(AuthToken authToken, User selectedUser) {
        new CountService().getFollowingCount(authToken, selectedUser, this);
    }

    public void isFollower(AuthToken authToken, User currUser, User selectedUser) {
        new FollowService().isFollower(authToken, currUser, selectedUser, this);
    }

    public void unfollow(AuthToken authToken, User selectedUser) {
        new FollowService().unfollow(authToken, selectedUser, this);
    }

    public void follow(AuthToken authToken, User selectedUser) {
        new FollowService().follow(authToken, selectedUser, this);
    }

    //postStatus
    public void postStatus(AuthToken authToken, String post, User currUser)  {
        try {
            view.displayInfoMessage("Pending Status...");
            Status newStatus = new Status(post, currUser, getFormattedDateTime(), parseURLs(post), parseMentions(post));
            new StatusService().postStatus(authToken, newStatus, this);
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            view.displayErrorMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }

    private String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

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

}
