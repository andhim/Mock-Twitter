package edu.byu.cs.tweeter.server.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static long stringToEpoch(String datetime) {
        long epoch = 0;
        try {
            SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy hh:mm:ss aa");
            Date date = df.parse(datetime);
            epoch = date.getTime();
        } catch(Exception e) {
            throw new RuntimeException("Failed to convert datetime into epoch.");
        }
        return epoch;
    }

}
