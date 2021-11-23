package edu.byu.cs.tweeter.server.dao.dynamoDB;

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
        Item item = null;
        AuthToken token = null;
        try {
            GetItemSpec spec = new GetItemSpec().withPrimaryKey("id", id);
            item = table.getItem(spec);

            token = new AuthToken(item.getString("id"),
                    item.getString("alias"),
                    item.getLong("timestamp"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch AuthToken");
        }

        return token;
    }

    @Override
    public void deleteAuthToken(AuthToken authToken) {
        try {
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey("id", authToken.getId());
            table.deleteItem(deleteItemSpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete AuthToken");
        }
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
