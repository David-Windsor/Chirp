package davidwindsor.firstproj.models;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * TODO
 */

public class ImageChirp extends Chirp implements Serializable {
    private Bitmap image;

    public ImageChirp(User user, String date, String userID, Bitmap picture) {
        super(user, date, userID);
        image = picture;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
