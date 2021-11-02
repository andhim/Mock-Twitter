package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.Status;

public class GetFeedResponse extends PagedResponse{
    private List<Status> feed;

    public GetFeedResponse(boolean hasMorePages, List<Status> feed) {
        super(true, hasMorePages);
        this.feed = feed;
    }

    public GetFeedResponse(String message) {
        super(false, message, false);
    }

    public List<Status> getFeed() {
        return feed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetFeedResponse that = (GetFeedResponse) o;
        return (Objects.equals(feed, that.feed) &&
                Objects.equals(this.getMessage(), that.getMessage()) &&
                this.isSuccess() == that.isSuccess());
    }

    @Override
    public int hashCode() {
        return Objects.hash(feed);
    }
}
