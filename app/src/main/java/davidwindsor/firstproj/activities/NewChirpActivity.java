package davidwindsor.firstproj.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import davidwindsor.firstproj.R;
import davidwindsor.firstproj.fragments.NewChirpFragment;

/**
 * TODO DOCUMENTATION
 */

public class NewChirpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //we can just reuse the frame holder
        setContentView(R.layout.activity_home_frameholder);
        NewChirpFragment chirpFragment = new NewChirpFragment();
        chirpFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.home_frameholder, chirpFragment).commit();
    }
}
