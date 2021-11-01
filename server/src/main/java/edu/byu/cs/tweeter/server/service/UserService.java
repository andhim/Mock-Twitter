package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.util.FakeData;

public class UserService {

    public LoginResponse login(LoginRequest request) {
        if (request == null ||
            request.getUsername() == null ||
            request.getPassword() == null) {
            throw new RuntimeException("[BadRequest]");
        }

        try {
            return getUserDAO().login(request);
        } catch (Exception ex) {
            throw new RuntimeException("[BadRequest]" + ex.getMessage());
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
//        if (invalidAuthToken) {
//            throw new RuntimeException("[AuthFailure] ..." ) table that has userAlias, authToken, and how current it is
//        }
        try {
            return getUserDAO().register(request);
        } catch (Exception ex) {
            throw new RuntimeException("[BadRequest]" + ex.getMessage());
        }
    }

    UserDAO getUserDAO() {
        return new UserDAO();
    }

}
