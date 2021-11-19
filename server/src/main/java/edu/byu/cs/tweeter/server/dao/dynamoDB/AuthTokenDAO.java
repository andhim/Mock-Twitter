package edu.byu.cs.tweeter.server.dao.dynamoDB;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.IAuthTokenDAO;

public class AuthTokenDAO implements IAuthTokenDAO {
    private Table table;

    public AuthTokenDAO(Table authTokenTable) {
        this.table = authTokenTable;
    }

    @Override
    public AuthToken getAuthToken(String id) throws RuntimeException {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("id", id);
        Item item = null;
        try {
            item = table.getItem(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get token from AuthToken table");
        }

        AuthToken token = new AuthToken(item.getString("id"),
                            item.getString("alias"),
                            item.getLong("timestamp"));
        return token;
    }

    @Override
    public void deleteAuthToken(AuthToken authToken) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey("id", authToken.getId());
        table.deleteItem(deleteItemSpec);
    }

    @Override
    public void putAuthToken(AuthToken authToken) {
        Item item = new Item()
                .withPrimaryKey("id", authToken.getId())
                .withString("alias",authToken.getAlias() )
                .withLong("timestamp", authToken.getTimestamp());
        PutItemOutcome outcome = table.putItem(item);
    }
}
