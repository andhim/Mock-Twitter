package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.Status;

public class GetFeedResponse extends PagedResponse{
    private List<Status> feeds;

    public GetFeedResponse(boolean hasMorePages, List<Status> feeds) {
        super(true, hasMorePages);
        this.feeds = feeds;
    }

    public GetFeedResponse(String message) {
        super(false, message, false);
    }

    public List<Status> getFeeds() {
        return feeds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetFeedResponse that = (GetFeedResponse) o;
        return (Objects.equals(feeds, that.feeds) &&
                Objects.equals(this.getMessage(), that.getMessage()) &&
                this.isSuccess() == that.isSuccess());
    }

    @Override
    public int hashCode() {
        return Objects.hash(feeds);
    }
}
