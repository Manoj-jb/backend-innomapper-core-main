package com.innowell.core.features.fileMetadata.utlis;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for handling date-related operations for file metadata.
*/
public class FileMetadataDateUtils {
    
    /**
     * Adds the specified number of days to the current date and returns the new date.
     *
     * @param days Number of days to add to the current date.
     * @return Date The new date after adding the specified number of days.
     */
    public static Date addTime(Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date()); // Set the current date
        calendar.add(Calendar.DATE, days);  // Add the specified number of days
        return calendar.getTime(); // Return the new date
    }
}
