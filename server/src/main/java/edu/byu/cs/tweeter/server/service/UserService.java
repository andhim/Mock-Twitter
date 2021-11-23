package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dynamoDB.S3DAO;
import edu.byu.cs.tweeter.server.util.Utils;

public class UserService extends Service {

    public UserService(DAOFactory factory) {
        super(factory);
    }

    public LoginResponse login(LoginRequest request) {
        if (request == null ||
            request.getUsername() == null ||
            request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Invalid input");
        }
        try {
            String hashedPassword = Utils.hashPassword(request.getPassword());
            LoginResponse response = factory.getUserDAO().login(request, hashedPassword);
            factory.getAuthTokenDAO().putAuthToken(response.getAuthToken());
            return response;
        } catch (Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
        }
    }

    public RegisterResponse register(RegisterRequest request) {
        if (request == null ||
            request.getFirstName() == null ||
            request.getLastName() == null ||
            request.getImageBytesBase64() == null ||
            request.getUsername() == null ||
            request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Invalid input");
        }
        try {
            String hashedPassword = Utils.hashPassword(request.getPassword());
            String imageURL = new S3DAO().upload(request.getUsername(), request.getImageBytesBase64());

            RegisterResponse response = factory.getUserDAO().register(request, hashedPassword, imageURL);
            factory.getAuthTokenDAO().putAuthToken(response.getAuthToken());
            return response;
        } catch (Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
        }
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if (request == null ||
            request.getAlias() == null ||
            request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Invalid input");
        }
        if (!validateAuthToken(request.getAuthToken())) {
            throw new RuntimeException("[BadRequest] Invalid token" );
        }
        try {
            return factory.getUserDAO().getUser(request);
        } catch (Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
        }
    }

    public LogoutResponse logout(LogoutRequest request) {
        if (request == null || request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Invalid input");
        }
        try {
            factory.getAuthTokenDAO().deleteAuthToken(request.getAuthToken());
            return factory.getUserDAO().logout(request);
        } catch (Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
        }
    }
}