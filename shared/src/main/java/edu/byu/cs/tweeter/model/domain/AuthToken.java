package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an auth token in the system.
 */
public class AuthToken implements Serializable {
    private String id;
    private String alias;
    private long timestamp;

    public AuthToken() {}

    public AuthToken(String id, String alias, long timestamp) {
        this.id = id;
        this.alias = alias;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthToken authToken = (AuthToken) o;
        return timestamp == authToken.timestamp && Objects.equals(id, authToken.id) && Objects.equals(alias, authToken.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, alias, timestamp);
    }

    @Override
    public String toString() {
        return "AuthToken{" +
                "id='" + id + '\'' +
                ", alias='" + alias + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
