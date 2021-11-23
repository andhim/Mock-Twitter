package edu.byu.cs.tweeter.server.dao.dynamoDB;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.util.Utils;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO implements IFollowDAO {

    private static final String PARTITION_KEY = "followerAlias";
    private static final String INDEX_PARTITION_KEY = "followeeAlias";

    private Table table;

    public FollowDAO(Table followTable) {
        this.table = followTable;
    }

    public FollowResponse follow(FollowRequest request) {
        try {
            Item item = new Item()
                    .withPrimaryKey("followerAlias", request.getCurrUserAlias())
                    .withString("followeeAlias", request.getFolloweeAlias())
                    .withString("followerName", request.getCurrUserName())
                    .withString("followerImageURL", request.getCurrImageURL())
                    .withString("followeeName", request.getFolloweeName())
                    .withString("followeeImageURL", request.getFolloweeImageURL());
            table.putItem(item);
        } catch (Exception e) {
            throw new RuntimeException("Failed to follow");
        }
        return new FollowResponse();
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        try {
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey("followerAlias",request.getCurrUserAlias(), "followeeAlias", request.getSelectedUserAlias());
            table.deleteItem(deleteItemSpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to unfollow");
        }
        return new UnfollowResponse();
    }

    /**
     * Check if a currentUser is following followee
     */
    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        Item item = null;
        try {
            GetItemSpec spec = new GetItemSpec().withPrimaryKey("followerAlias", request.getCurrUserAlias(), "followeeAlias", request.getSelectedUserAlias());
            item = table.getItem(spec);
        } catch (Exception e) {
            throw new RuntimeException("Database error");
        }
        boolean isFollower = item == null ? false : true;

        return new IsFollowerResponse(isFollower);
    }

    /**
     * Gets the users from the database that the user specified in the request is followed by. Uses
     * information in the request object to limit the number of followers returned and to return the
     * next set of followers after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * Get the users who follow the selected User
     *
     * @return the followers.
     */
    @Override
    public GetFollowersResponse getFollowers(String alias, Integer limit, String lastFollowerAlias) {
        List<User> followers = null;
        ItemCollection<QueryOutcome> items = null;

        try {
            QuerySpec spec = Utils.getBasicSpec(INDEX_PARTITION_KEY, alias, limit);

            if (lastFollowerAlias != null) {
                PrimaryKey lastItemKey = new PrimaryKey(INDEX_PARTITION_KEY, alias, PARTITION_KEY, lastFollowerAlias);
                spec.withExclusiveStartKey(lastItemKey);
            }

            items = table.getIndex("followeeAlias-followerAlias-index").query(spec);
            Iterator<Item> iterator = items.iterator();

            Item item = null;
            followers = new ArrayList<>();

            while (iterator.hasNext()) {
                item = iterator.next();
                String[] followerNames = item.getString("followerName").split("\\s+");
                User follower = new User(followerNames[0], followerNames[1], item.getString("followerAlias"), item.getString("followerImageURL"));

                followers.add(follower);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get followers");
        }


        return new GetFollowersResponse(followers, Utils.checkHasMore(items));
    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * Get the users who are followed by the selected User
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public GetFollowingResponse getFollowees(GetFollowingRequest request) {
        ItemCollection<QueryOutcome> items = null;
        List<User> followees = null;
        try {
            QuerySpec spec = Utils.getBasicSpec(PARTITION_KEY, request.getFollowerAlias(), request.getLimit());

            if (request.getLastFolloweeAlias() != null) {
                PrimaryKey lastItemKey = new PrimaryKey(PARTITION_KEY, request.getFollowerAlias(), INDEX_PARTITION_KEY, request.getLastFolloweeAlias());
                spec.withExclusiveStartKey(lastItemKey);
            }

            items = table.query(spec);
            Iterator<Item> iterator = items.iterator();

            Item item = null;
            followees = new ArrayList<>();

            while (iterator.hasNext()) {
                item = iterator.next();
                String[] names = item.getString("followeeName").split("\\s+");
                User followee = new User(names[0], names[1], item.getString("followeeAlias"), item.getString("followeeImageURL"));

                followees.add(followee);
            }
        } catch(Exception e) {
            throw new RuntimeException("Failed to get followees");
        }

        return new GetFollowingResponse(followees, Utils.checkHasMore(items));
    }
}
