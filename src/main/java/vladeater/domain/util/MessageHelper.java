package vladeater.domain.util;

import vladeater.domain.User;

/**
 * @author Vlados Guskov
 */
public abstract class MessageHelper {
    public static String getAuthorName(User author){
        return author != null ? author.getUsername() : "<none>";
    }
}
