package davidwindsor.firstproj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import davidwindsor.firstproj.R;
import davidwindsor.firstproj.fragments.HomeFragment;

/**
 * HomeActivity where Chirps are displayed. Once a user gets here they will be able to see their timeline.
 * Besides that the user has three buttons, either making a new chirp, editing their watchlist, or logging out
 */

public class HomeActivity extends AppCompatActivity {
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_frameholder);
        homeFragment = new HomeFragment();
        homeFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.home_frameholder, homeFragment).commit();
    }

    /**
     * We don't care about any actual results, all we want to do is have the fragment update its chirps
     * everytime we try to make a new chirp or edit the watchlist and waiting for a result will tell us that happened
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        homeFragment.updateChirps();
    }
}
