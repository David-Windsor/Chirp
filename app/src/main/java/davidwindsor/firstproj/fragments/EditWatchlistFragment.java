package davidwindsor.firstproj.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import davidwindsor.firstproj.R;
import davidwindsor.firstproj.activities.EditWatchlistActivity;
import davidwindsor.firstproj.activities.LoginActivity;
import davidwindsor.firstproj.models.RequestQueueWrapper;
import davidwindsor.firstproj.models.SparkRequester;
import davidwindsor.firstproj.models.User;

/**
 * Communicating with the server isn't so hot right now, so for speed and getting it done, watchlists will be stored locally
 * Filenames will be saved as handle+email.wl and contain the IDs of the users they want to follow
 */

public class EditWatchlistFragment extends Fragment{
    private User user;
    private EditWatchlistActivity parentActivity;
    private EditText watchField;
    private Button addButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_watchlist, container, false);
        watchField = view.findViewById(R.id.edit_watchlist_handle_field);
        parentActivity = (EditWatchlistActivity) getActivity();

        addButton = view.findViewById(R.id.edit_watchlist_add_button);
        addButton.setOnClickListener(v -> attemptAdd());
        return view;
    }

    private void attemptAdd() {
        //get the handle, find the user by handle, and add to the list
        String otherHandle = watchField.getText().toString().trim();
        JsonObjectRequest handleRequest = new JsonObjectRequest(Request.Method.GET, SparkRequester.SERVER_URL + SparkRequester.FIND_BY_HANDLE, null
        , (JSONObject response) -> {
            if(response == null) {
                Toast.makeText(parentActivity, R.string.no_such_user, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    FileOutputStream fileOutputStream = parentActivity.openFileOutput(response.getString("handle") + response.getString("email") + ".wl", Context.MODE_PRIVATE);
                    OutputStreamWriter output = new OutputStreamWriter(fileOutputStream);
                    output.write(otherHandle + "\n");
                    output.flush();
                } catch (FileNotFoundException ignored) {} catch (JSONException jsone) {} catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Toast.makeText(parentActivity, R.string.add_watchlist_error, Toast.LENGTH_SHORT).show());
        RequestQueueWrapper.getRequestQueue(parentActivity).add(handleRequest);
    }


    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        user = (User) args.getSerializable(LoginActivity.USER_BUNDLE_KEY);
    }


}
