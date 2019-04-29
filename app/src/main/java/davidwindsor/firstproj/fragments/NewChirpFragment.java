package davidwindsor.firstproj.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import davidwindsor.firstproj.R;
import davidwindsor.firstproj.activities.LoginActivity;
import davidwindsor.firstproj.activities.NewChirpActivity;
import davidwindsor.firstproj.models.RequestQueueWrapper;
import davidwindsor.firstproj.models.SparkRequester;
import davidwindsor.firstproj.models.TextChirp;
import davidwindsor.firstproj.models.User;

/**
 * <b>ONLY NewChirpActivity SHOULD BE THE PARENT OF THIS FRAGMENT</b>
 * TODO DOCUMENTATION
 */

public class NewChirpFragment extends Fragment {
    private EditText messageField;
    private User user;
    NewChirpActivity parentActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_chirp, container, false);
        parentActivity = (NewChirpActivity) getActivity();
        messageField = view.findViewById(R.id.new_chirp_message);
        messageField.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);

        Button postButton = view.findViewById(R.id.new_chirp_post_button);
        postButton.setOnClickListener(v -> {
            //attempt the post
            attemptPost(messageField.getText().toString().trim(), new Date());
        });

        return view;
    }

    @SuppressLint("SimpleDateFormat")
    private void attemptPost(String message, Date date) {
        //the server expects a string as the date, so take date to a string
        String sDate = new SimpleDateFormat("MM/dd/yyyy").format(date);
        if (!validateChirp(message)) {
            Toast.makeText(parentActivity, R.string.chirp_invalid_message, Toast.LENGTH_SHORT).show();
            return;
        }
        final TextChirp textChirp = new TextChirp(user, sDate, user.getId(), message);
        String chirpJsonString = new Gson().toJson(textChirp);
        try {
            JSONObject chirpJSON = new JSONObject(chirpJsonString);
            JsonObjectRequest newChirpRequest = new JsonObjectRequest(Request.Method.POST, SparkRequester.SERVER_URL + SparkRequester.POST_NEW_CHIRP, chirpJSON,
                    response -> {
                        if (response != null) {
                            Toast.makeText(parentActivity, R.string.new_post_success, Toast.LENGTH_SHORT).show();
                            parentActivity.finish();
                        }
                    }, error -> { //TODO HANDLE ERROR

            });
            RequestQueueWrapper.getRequestQueue(parentActivity).add(newChirpRequest);
        } catch (JSONException e) {
            Toast.makeText(parentActivity, "json sucks", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean validateChirp(String message) {
        return message.length() <= 281 && !(message.trim().length() == 0);
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        assert args != null;
        user = (User) args.getSerializable(LoginActivity.USER_BUNDLE_KEY);
    }
}
