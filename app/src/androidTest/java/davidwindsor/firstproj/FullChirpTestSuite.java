package davidwindsor.firstproj;

import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

import davidwindsor.firstproj.models.User;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FullChirpTestSuite {
    private final User testUser = new User("test@test.com", "test", "test", "test");

}
