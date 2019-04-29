package davidwindsor.firstproj.models;

import java.io.Serializable;

/**
 * TODO DOCUMENTATION
 */

public class TextChirp extends Chirp implements Serializable {
    private String message;

    public TextChirp(User user, String date, String userID, String _message) {
        super(user, date, userID);
        message = _message;
    }

    public String getMessage() {
        return message;
    }
}
