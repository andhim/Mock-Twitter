package edu.byu.cs.tweeter.server.dao.dynamoDB;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.IUserDAO;

public class UserDAO implements IUserDAO {
    private Table table;

    public UserDAO(Table userTable) {
        this.table = userTable;
    }

    @Override
    public LoginResponse login(LoginRequest request) throws RuntimeException {
        //user
        String securePassword = hashPassword(request.getPassword());
        User user = getUserFromDB(request.getUsername(), securePassword);

        //authToken
        String uuid = UUID.randomUUID().toString();
        long timestamp = Instant.now().toEpochMilli();
        AuthToken authToken = new AuthToken(uuid, user.getAlias(), timestamp);

        return new LoginResponse(user, authToken);
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        String username = request.getUsername();
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String image = request.getImageBytesBase64();

        //imageURL
        String imageURL = new S3DAO().upload(username, image);

        //hashing and salting
        String password = request.getPassword();
        String securePassword = hashPassword(password);

        Item item = new Item()
                .withPrimaryKey("alias", username)
                .withString("firstName", firstName)
                .withString("lastName", lastName)
                .withString("password", securePassword)
                .withString("imageURL",imageURL);

        PutItemOutcome outcome = table.putItem(item);

        //creating user
        User user = new User(firstName,lastName,username,imageURL);

        //creating authToken
        String uuid = UUID.randomUUID().toString();
        long timestamp = Instant.now().toEpochMilli();
        AuthToken authToken = new AuthToken(uuid, user.getAlias(), timestamp);

        return new RegisterResponse(user, authToken);
    }

    @Override
    public GetUserResponse getUser(GetUserRequest request) {
        User user = getUserFromDB(request.getAlias(), null);
        return new GetUserResponse(user);
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse();
    }

    private User getUserFromDB(String alias, String password) throws RuntimeException {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("alias", alias);
        Item item = null;
        try {
            item = table.getItem(spec);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if (item == null) {
            throw new RuntimeException("User does not exist");
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

    private static String hashPassword(String passwordToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }
}
