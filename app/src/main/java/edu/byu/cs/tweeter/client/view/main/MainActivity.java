package edu.byu.cs.tweeter.client.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.client.view.login.LoginActivity;
import edu.byu.cs.tweeter.client.view.login.StatusDialogFragment;
import edu.byu.cs.tweeter.client.view.util.ImageUtils;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The main activity for the application. Contains tabs for feed, story, following, and followers.
 */
public class MainActivity extends AppCompatActivity implements StatusDialogFragment.Observer, MainPresenter.View {



    public static final String CURRENT_USER_KEY = "CurrentUser";

    //GetFollowersCount
    @Override
    public void setFollowersCount(int count) {
        followerCount.setText(getString(R.string.followerCount, String.valueOf(count)));
    }

    //GetFollowingCount
    @Override
    public void setFollowingCount(int count) {
        followeeCount.setText(getString(R.string.followeeCount, String.valueOf(count)));
    }

    //IsFollower
    @Override
    public void setFollower() {
        followButton.setText(R.string.following);
        followButton.setBackgroundColor(getResources().getColor(R.color.white));
        followButton.setTextColor(getResources().getColor(R.color.lightGray));
    }

    @Override
    public void setNotFollower() {
        followButton.setText(R.string.follow);
        followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    //Unfollow
    @Override
    public void updateFollow(boolean removed) {
        updateSelectedUserFollowingAndFollowers();
        updateFollowButton(removed);
    }

    @Override
    public void setFollowButton(boolean setEnabled) {
        followButton.setEnabled(true);
    }

    //Logout
    @Override
    public void logoutUser() {
        //Revert to login screen.
        Intent intent = new Intent(this, LoginActivity.class);
        //Clear everything so that the main activity is recreated with the login page.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Clear user data (cached data).
        Cache.getInstance().clearCache();
        startActivity(intent);
    }


    @Override
    public void displayErrorMessage(String type, String message) {
        //TODO: if branches => valid for View?
        if (type == "logout") {
            logOutToast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
            logOutToast.show();
        } else if (type == "postStatus") {
            postingToast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
            postingToast.show();
        } else {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void displayInfoMessage(String type, String message) {
        //TODO: if branches => valid for View?
        if (type == "logout") {
            logOutToast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
            logOutToast.show();
        } else if (type =="postStatus"){
            clearPostingMessage();
            postingToast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
            postingToast.show();
        } else {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void clearPostingMessage() {
        if (postingToast != null) {
            postingToast.cancel();
            postingToast = null;
        }
    }


    private Toast logOutToast;
    private Toast postingToast;
    private User selectedUser; //TODO
    private TextView followeeCount;
    private TextView followerCount;
    private Button followButton;
    private MainPresenter presenter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: don't user private variable?
        selectedUser = (User) getIntent().getSerializableExtra(CURRENT_USER_KEY);
        //TODO: presenter?
        if (selectedUser == null) {
            throw new RuntimeException("User not passed to activity");
        }

        presenter = new MainPresenter(this);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), selectedUser);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusDialogFragment statusDialogFragment = new StatusDialogFragment();
                statusDialogFragment.show(getSupportFragmentManager(), "post-status-dialog");
            }
        });

        updateSelectedUserFollowingAndFollowers();

        TextView userName = findViewById(R.id.userName);
        userName.setText(selectedUser.getName());

        TextView userAlias = findViewById(R.id.userAlias);
        userAlias.setText(selectedUser.getAlias());

        ImageView userImageView = findViewById(R.id.userImage);
        userImageView.setImageDrawable(ImageUtils.drawableFromByteArray(selectedUser.getImageBytes()));

        followeeCount = findViewById(R.id.followeeCount);
        followeeCount.setText(getString(R.string.followeeCount, "..."));

        followerCount = findViewById(R.id.followerCount);
        followerCount.setText(getString(R.string.followerCount, "..."));

        followButton = findViewById(R.id.followButton);

        //TODO: if branch => View or Presenter?
        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) { //NOTE: If they are equal
            followButton.setVisibility(View.GONE);
        } else {
            followButton.setVisibility(View.VISIBLE);
            presenter.isFollower(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser(), selectedUser);
        }

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: not belong to view?
                followButton.setEnabled(false);

                if (followButton.getText().toString().equals(v.getContext().getString(R.string.following))) {
                    presenter.unfollow(Cache.getInstance().getCurrUserAuthToken(), selectedUser);
                    //TODO: okay to use displayInfoMessage?
                    displayInfoMessage("UNFOLLOW","Removing " + selectedUser.getName() + "...");
                    //Toast.makeText(MainActivity.this, "Removing " + selectedUser.getName() + "...", Toast.LENGTH_LONG).show();
                } else {
                    presenter.follow(Cache.getInstance().getCurrUserAuthToken(), selectedUser);
                    //TODO: okay to use displayInfoMessage?
                    displayInfoMessage("FOLLOW","Adding " + selectedUser.getName() + "...");
                    //Toast.makeText(MainActivity.this, "Adding " + selectedUser.getName() + "...", Toast.LENGTH_LONG).show();
                }

                //TODO: not belong to view?
//                followButton.setEnabled(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutMenu) {
            presenter.logout(Cache.getInstance().getCurrUserAuthToken());
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onStatusPosted(String post) {
        presenter.postStatus(Cache.getInstance().getCurrUserAuthToken(),post, Cache.getInstance().getCurrUser());


//        try {
//            //TODO: PostStatus Task
////            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
//
////            PostStatusTask statusTask = new PostStatusTask(Cache.getInstance().getCurrUserAuthToken(),
////                    newStatus, new PostStatusHandler());
////            ExecutorService executor = Executors.newSingleThreadExecutor();
////            executor.execute(statusTask);
//        } catch (Exception ex) {
//            Log.e(LOG_TAG, ex.getMessage(), ex);
//            Toast.makeText(this, "Failed to post the status because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
//        }
    }







    public void updateSelectedUserFollowingAndFollowers() {
        //TODO: Can I just use 'newSingleThreadExecutor()' twice instead of using the below?
//        ExecutorService executor = Executors.newFixedThreadPool(2);

        //TODO: GetFollowersCount Task
        // Get count of most recently selected user's followers.
//        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
//                selectedUser, new GetFollowersCountHandler());
//        executor.execute(followersCountTask);

        //TODO: GetFollowingCount Task
        // Get count of most recently selected user's followees (who they are following)
//        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
//                selectedUser, new GetFollowingCountHandler());
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.execute(followingCountTask);
        presenter.getFollowersCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser);
        presenter.getFollowingCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser);

    }

    //TODO: presenter?
    public void updateFollowButton(boolean removed) {
        // If follow relationship was removed.
        if (removed) {
            followButton.setText(R.string.follow);
            followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            followButton.setText(R.string.following);
            followButton.setBackgroundColor(getResources().getColor(R.color.white));
            followButton.setTextColor(getResources().getColor(R.color.lightGray));
        }
    }

}
