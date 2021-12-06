package edu.byu.cs.tweeter.server.dao.dynamoDB;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.IUserDAO;

public class UserDAO implements IUserDAO {
    private static final String PARTITION_KEY = "alias";
    private static final String TABLE_NAME = "User";

    private Table table;
    private DynamoDB dynamoDB;

    public UserDAO(Table userTable, DynamoDB dynamoDB) {
        this.table = userTable;
        this.dynamoDB = dynamoDB;
    }

    @Override
    public LoginResponse login(LoginRequest request, String hashedPassword) throws RuntimeException {
        User user = null;
        AuthToken authToken = null;
        try {
            //user
            user = getUserFromDB(request.getUsername(), hashedPassword);

            //authToken
            String uuid = UUID.randomUUID().toString();
            long timestamp = Instant.now().toEpochMilli();
            authToken = new AuthToken(uuid, user.getAlias(), timestamp);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return new LoginResponse(user, authToken);
    }

    @Override
    public RegisterResponse register(RegisterRequest request, String hashedPassword, String imageURL) {
        User user = null;
        AuthToken authToken = null;
        try {
            Item item = new Item()
                    .withPrimaryKey(PARTITION_KEY, request.getUsername())
                    .withString("firstName", request.getFirstName())
                    .withString("lastName", request.getLastName())
                    .withString("password", hashedPassword)
                    .withString("imageURL",imageURL);

            PutItemOutcome outcome = table.putItem(item);

            user = new User(request.getFirstName(), request.getLastName(), request.getUsername(), imageURL);

            //creating authToken
            String uuid = UUID.randomUUID().toString();
            long timestamp = Instant.now().toEpochMilli();
            authToken = new AuthToken(uuid, user.getAlias(), timestamp);

        } catch (Exception e) {
            throw new RuntimeException("Failed to register a new user");
        }
        return new RegisterResponse(user, authToken);
    }

    @Override
    public GetUserResponse getUser(GetUserRequest request) {
        User user = null;
        try {
            user = getUserFromDB(request.getAlias(), null);
        } catch (Exception e ) {
            throw new RuntimeException("Failed to get user data");
        }
        return new GetUserResponse(user);
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse();
    }

    @Override
    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        try {
            Item item = getUserItem(request.getSelectedUserAlias());
            if (item.getNumber("followerCount") != null) {
                return new GetFollowersCountResponse(item.getNumber("followerCount").intValue());
            } else {
                return new GetFollowersCountResponse(0);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get followers count");
        }
    }

    @Override
    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        try {
            Item item = getUserItem(request.getSelectedUserAlias());
            if (item.getNumber("followeeCount") != null) {
                return new GetFollowingCountResponse(item.getNumber("followeeCount").intValue());
            } else {
                return new GetFollowingCountResponse(0);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get following count");
        }
    }

    @Override
    public void incrementFollow(FollowRequest request) {
        try {
            //increment followerCount for followee
            updateCount(request.getFolloweeAlias(), "followerCount", 1);
            //increment followingCount for follower
            updateCount(request.getCurrUserAlias(), "followeeCount", 1);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void decrementFollow(UnfollowRequest request) {
        try {
            //decrement followerCount for followee
            updateCount(request.getSelectedUserAlias(), "followerCount", -1);
            //decrement followingCount for follower
            updateCount(request.getCurrUserAlias(), "followeeCount", -1);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void addUserBatch(List<User> users) {

        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems(TABLE_NAME);

        // Add each user into the TableWriteItems object
        for (User user : users) {
            Item item = new Item()
                    .withPrimaryKey("alias", user.getAlias())
                    .withString("firstName", user.getFirstName())
                    .withString("lastName", user.getLastName())
                    .withString("imageURL", user.getImageUrl());
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                DAOUtils.loopBatchWrite(items, dynamoDB);
                items = new TableWriteItems(TABLE_NAME);
            }
        }

        // Write any leftover items
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            DAOUtils.loopBatchWrite(items, dynamoDB);
        }
    }

    private void updateCount(String alias, String attrName, int value) {
        String errorMessage = String.format("UpdateCount %s", attrName);
        try {
            Item item = getUserItem(alias);
            int updatedCount = value;
            if (item.getNumber(attrName) != null) {
                updatedCount += item.getNumber(attrName).intValue();
            } else {
                updatedCount = updatedCount > 0 ? updatedCount : 0;
            }

            String updateExpressionString = String.format("set %s = :c", attrName);

            UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(PARTITION_KEY, alias)
                    .withUpdateExpression(updateExpressionString)
                    .withValueMap(new ValueMap().withNumber(":c", updatedCount));

            table.updateItem(updateItemSpec);
        } catch (Exception e) {
            throw new RuntimeException(errorMessage);
        }
    }

    private User getUserFromDB(String alias, String password) throws RuntimeException {
        Item item = null;
        try {
            item = getUserItem(alias);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if (password != null && !password.equals(item.getString("password"))) {
            throw new RuntimeException("Incorrect password");
        }

        User user = new User(item.getString("firstName"),
                item.getString("lastName"),
                item.getString("alias"),
                item.getString("imageURL"));

        return user;
    }

    private Item getUserItem(String alias) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey(PARTITION_KEY, alias);
        Item item = null;
        try {
            item = table.getItem(spec); //userTable
        } catch (Exception e) {
            throw new RuntimeException("User Table Error");
        }

        if (item == null) {
            throw new RuntimeException("User does not exist");
        }

        return item;
    }


}
