package davidwindsor.firstproj.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import davidwindsor.firstproj.R;
import davidwindsor.firstproj.activities.EditWatchlistActivity;
import davidwindsor.firstproj.activities.HomeActivity;
import davidwindsor.firstproj.activities.LoginActivity;
import davidwindsor.firstproj.activities.NewChirpActivity;
import davidwindsor.firstproj.models.Chirp;
import davidwindsor.firstproj.models.ImageChirp;
import davidwindsor.firstproj.models.RequestQueueWrapper;
import davidwindsor.firstproj.models.SparkRequester;
import davidwindsor.firstproj.models.TextChirp;
import davidwindsor.firstproj.models.User;

/**
 * <b>Parent activity should always be an instance of HomeActivity</b>
 * <p>
 * Fragment that will handle displaying the watchlist, and launching a NewChirpActivity or EditWatchListActivity instance
 * Will also be told the user by the HomeActivity, and from there the fragment will handle everything, unlike how it would've worked in LoginActivity and RegistrationActivity
 */
@SuppressWarnings("FieldCanBeLocal")
public class HomeFragment extends Fragment {
    private User user;
    private HomeActivity parentActivity;

    private TextView userField;
    private ImageButton newChirpButton;
    private Button editWatchlistButton, logoutButton;
    private RecyclerView watchlistRecycler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_watchlist, container, false);
        parentActivity = (HomeActivity) getActivity();
        newChirpButton = view.findViewById(R.id.home_new_chirp_button);
        newChirpButton.setOnClickListener(v -> {
            Intent intent = new Intent(parentActivity, NewChirpActivity.class);
            intent.putExtra(LoginActivity.USER_BUNDLE_KEY, user);
            startActivityForResult(intent, 1);
        });

        logoutButton = view.findViewById(R.id.home_logout_button);
        logoutButton.setOnClickListener(v -> {
            //TODO remove the logout file and cleanup any work in here
            parentActivity.finish();
        });

        editWatchlistButton = view.findViewById(R.id.home_edit_watchlist_button);
        editWatchlistButton.setOnClickListener(v -> {
            Intent intent = new Intent(parentActivity, EditWatchlistActivity.class);
            intent.putExtra(LoginActivity.USER_BUNDLE_KEY, user);
            startActivityForResult(intent, 1); //request code doesn't matter, when we get a result we tell the fragment to update its chirps
        });

        userField = view.findViewById(R.id.home_user_field);
        userField.setText("&" + user.getHandle());

        watchlistRecycler = view.findViewById(R.id.home_watchlist_recycler);
        updateChirps();
        return view;
    }

     public void updateChirps() {
         //get the chirp list, and when we do set the adapter
         JsonArrayRequest chirpsRequest = new JsonArrayRequest(Request.Method.GET, SparkRequester.SERVER_URL + SparkRequester.GET_CHIRPS, null,
                 response -> {
                     if (response == null) {
                         Log.d("GET CHIRPS RESPONSE: ", "No chirps available");
                         watchlistRecycler.setAdapter(new WatchlistRecyclerAdapter(new ArrayList<>()));
                     } else {
                         try {
                             ArrayList<Chirp> chirps = new ArrayList<>();
                             for (int i = 0; i < 6; ++i) {
                                 JSONObject current = response.getJSONObject(i);
                                 TextChirp chirp = new TextChirp(new User(),
                                         current.getString("date"),
                                         current.getString("userId"),
                                         current.getString("message"));
                                 chirps.add(chirp);
                             }
                             //chirps have an idea but no User, set the user field
                             replaceIDs(chirps);
                             //chirps should be finalized, throw it into the adapter
                             watchlistRecycler.setAdapter(new WatchlistRecyclerAdapter(chirps));
                             watchlistRecycler.setLayoutManager(new LinearLayoutManager(parentActivity));
                         } catch (JSONException e) {
                             Toast.makeText(parentActivity, "JSON EXCEPTION", Toast.LENGTH_SHORT).show();
                         }
                     }
                 }
                 , error ->
                 Toast.makeText(parentActivity, "SERVER ERROR IN GETTING CHIRPS", Toast.LENGTH_SHORT).show());
         RequestQueueWrapper.getRequestQueue(parentActivity).add(chirpsRequest);
     }

    /**
     * @param chirps List of chirps to be gone through
     *               This method's purpose is to set the user field in each chirp. Since the db doesn't store the User itself in association with the chirp, we need to set the field
     */
    private void replaceIDs(ArrayList<Chirp> chirps) {
        for (Chirp c : chirps) {
            JsonObjectRequest userRequest = new JsonObjectRequest(Request.Method.GET, SparkRequester.SERVER_URL + SparkRequester.FIND_BY_ID + c.getUserID(), null,
                    response -> {
                        if (response != null) {
                            try {
                                c.setUser(new User(response.getString("email"),
                                        response.getString("handle"),
                                        response.getString("password"),
                                        response.getString("id")));
                                watchlistRecycler.setAdapter(new WatchlistRecyclerAdapter(chirps));
                                watchlistRecycler.setLayoutManager(new LinearLayoutManager(parentActivity));
                            } catch (JSONException ignored) {
                            } //TODO DON'T IGNORE
                        }
                    }, error -> Toast.makeText(parentActivity, "SERVER ERROR IN CHANGING CHIRPS", Toast.LENGTH_SHORT).show());
            RequestQueueWrapper.getRequestQueue(parentActivity).add(userRequest);
        }
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null) {
            user = (User) args.getSerializable(LoginActivity.USER_BUNDLE_KEY);
        } else {
            Toast.makeText(parentActivity, R.string.bad_error, Toast.LENGTH_SHORT).show();
        }
    }

    class WatchlistRecyclerAdapter extends RecyclerView.Adapter<WatchlistRecyclerAdapter.ChirpViewHolder> {
        private ArrayList<Chirp> chirps;

        WatchlistRecyclerAdapter(ArrayList<Chirp> _chirps) {
            chirps = _chirps;
        }

        @NonNull
        @Override
        public ChirpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ChirpViewHolder(getLayoutInflater().inflate(R.layout.chirp_card, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ChirpViewHolder holder, int position) {
            holder.bind(chirps.get(position));
        }

        @Override
        public int getItemCount() {
            return chirps.size();
        }

        /**
         * View Holder for the Chirps
         */
        class ChirpViewHolder extends RecyclerView.ViewHolder {
            private TextView ownerField, messageField, postDate;
            private ImageView imageField;
            private Chirp chirp;

            ChirpViewHolder(View itemView) {
                super(itemView);
                ownerField = itemView.findViewById(R.id.chirp_owner_field);
                messageField = itemView.findViewById(R.id.chirp_message_field);
                postDate = itemView.findViewById(R.id.chirp_date_field);
                imageField = itemView.findViewById(R.id.chirp_image_field);

            }

            @SuppressLint("SimpleDateFormat")
            void bind(Chirp _chirp) {
                chirp = _chirp;
                ownerField.setText(chirp.getUser().getHandle());
                postDate.setText(chirp.getDate());
                if (chirp instanceof ImageChirp) {
                    imageField.setVisibility(View.VISIBLE);
                    //TODO scale
                    Bitmap bitmap = ((ImageChirp) chirp).getImage();
                    int x = bitmap.getWidth();
                    int y = bitmap.getHeight();
                    if (x > 128 || y > 128)
                        bitmap = resizeBitmap(bitmap);
                    imageField.setImageBitmap(bitmap);
                } else {
                    messageField.setVisibility(View.VISIBLE);
                    messageField.setText(((TextChirp) chirp).getMessage());
                }
            }
        }
    }

    @NonNull
    private Bitmap resizeBitmap(@NonNull Bitmap bitmap) {
        //TODO implement
        double width = bitmap.getWidth();
        double height = bitmap.getHeight();
        double aspectRatio = width * 1.0 / height;
        if (width > height) {
            width = 128;
            height /= aspectRatio;
        } else if (height > width) {
            height = 128;
            width /= aspectRatio;
        }
        //they're equal so we have a square
        else {
            width = height = 128;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
        return bitmap;
    }


}
