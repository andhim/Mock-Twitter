package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.JsonSerializer;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.SqsRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.util.Utils;

public class StatusService extends Service {

    public StatusService(DAOFactory factory) {
        super(factory);
    }

    public GetFeedResponse getFeed(GetFeedRequest request) {
        if (request == null || request.getUserAlias() == null || request.getAuthToken() == null || request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Invalid request");
        }
        if (!validateAuthToken(request.getAuthToken())) {
            throw new RuntimeException("[BadRequest] Invalid token" );
        }
        try {
            return factory.getFeedDAO().getFeed(request);
        } catch(Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
        }
    }

    public GetStoryResponse getStory(GetStoryRequest request) {
        if (request == null || request.getUserAlias() == null || request.getAuthToken() == null || request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Invalid request");
        }
        if (!validateAuthToken(request.getAuthToken())) {
            throw new RuntimeException("[BadRequest] Invalid token");
        }
        try {
            return factory.getStoryDAO().getStory(request);
        } catch(Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
        }
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if (request == null || request.getNewStatus() == null || request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Invalid request");
        }
        if (!validateAuthToken(request.getAuthToken())) {
            throw new RuntimeException("[BadRequest] Invalid token" );
        }
        try {
            User user = request.getNewStatus().getUser();
            user.setImageBytes(null);
            Status statusWithOutImageBytes = request.getNewStatus();
            statusWithOutImageBytes.setUser(user);

            SqsRequest sqsRequest = new SqsRequest(statusWithOutImageBytes, null);
            String messageBody = Utils.serialize(sqsRequest);
            /*
            TODO: In order to write to a feed table
                1. Uncomment the code below
                2. Deploy PostStatus lambda function with the new jar
                3. Hook up the lambda functions with SQS (Two)
             */
//            if (Utils.sendToSQS(messageBody, Utils.POST_STATUS_QUEUE_URL) == null) {
//                throw new RuntimeException("Sending to PostStatusQueue failed");
//            };
            factory.getStoryDAO().postStory(request);
            return new PostStatusResponse();
        } catch(Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
        }
    }

    public void postFeed(SQSEvent event) {
        try {
            for (SQSEvent.SQSMessage msg : event.getRecords()) {
                SqsRequest request = Utils.deserialize(msg.getBody(), SqsRequest.class);
                factory.getFeedDAO().postFeed(request.getFollowerAliases(), request.getStatus());
            }
        } catch(Exception ex) {
            throw new RuntimeException("[ServerError]" + ex.getMessage());
        }
    }
}
