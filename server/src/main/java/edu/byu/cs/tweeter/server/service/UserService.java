package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.UserDAO;

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
            LoginResponse response = factory.getUserDAO().login(request);
            getAuthTokenDAO().putAuthToken(response.getAuthToken());
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
            RegisterResponse response = factory.getUserDAO().register(request);
            getAuthTokenDAO().putAuthToken(response.getAuthToken());
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
            getAuthTokenDAO().deleteAuthToken(request.getAuthToken());
            return factory.getUserDAO().logout(request);
        } catch (Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
        }
    }


    private IAuthTokenDAO getAuthTokenDAO() {
        return factory.getAuthTokenDAO();
    }
}