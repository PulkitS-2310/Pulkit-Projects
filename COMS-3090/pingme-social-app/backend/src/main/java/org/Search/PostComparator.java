package org.Search;

import java.time.LocalDateTime;
import java.util.Comparator;

public class PostComparator implements Comparator<Post>
{

    @Override
    public int compare(Post p1, Post p2) {

        LocalDateTime t1 = LocalDateTime.parse(p1.getTimeRaw());
        LocalDateTime t2 = LocalDateTime.parse(p2.getTimeRaw());

        if (t1.isBefore(t2))
        {
            return 1; // used to be -1 and sorted in reverse
        } else if (t1.isAfter(t2))
        {
            return -1; // used to be 1 and sorted in reverse
        } else
        {
            return 0;
        }




    }
}
