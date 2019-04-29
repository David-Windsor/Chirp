package davidwindsor.firstproj.models;

import java.io.Serializable;

/**
 * Base chirp class
 * Will be used to subclass ImageChirp and TextChirp
 */

public abstract class Chirp implements Serializable {
    private User user;
    private String userId;
    private String date;
    Chirp(User user, String date, String _userID) {
        this.user = user;
        this.date = date;
        userId = _userID;
    }

    public String getDate() {
        return date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserID() {
        return userId;
    }
}
