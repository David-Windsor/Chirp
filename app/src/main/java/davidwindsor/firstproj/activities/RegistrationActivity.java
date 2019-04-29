package davidwindsor.firstproj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import davidwindsor.firstproj.R;
import davidwindsor.firstproj.models.RequestQueueWrapper;
import davidwindsor.firstproj.models.SparkRequester;

/**
 * Activity to handle registrations.
 * When a registration is complete it will send back the email and password so the LoginActivity can login automatically
 * Also same with LoginActivity and all activities, if I can remove the server communications from the activities I will
 */
@SuppressWarnings("FieldCanBeLocal")
public class RegistrationActivity extends AppCompatActivity {
    private EditText emailPrompt, passwordPrompt, handlePrompt;
    private Button registerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        emailPrompt = findViewById(R.id.registration_email_prompt);
        passwordPrompt = findViewById(R.id.registration_password_prompt);
        handlePrompt = findViewById(R.id.registration_handle_prompt);
        registerButton = findViewById(R.id.registration_confirm_button);
        registerButton.setOnClickListener(v -> attemptRegistration());
    }

    private void attemptRegistration() {
        if (validateNewUserParams()) {
            JSONObject newUser = new JSONObject();
            try {
                newUser.put("email", emailPrompt.getText().toString().trim());
                newUser.put("password", passwordPrompt.getText().toString().trim());
                newUser.put("handle", handlePrompt.getText().toString().trim());
            } catch (JSONException ignored) {
            }//TODO handle error, but don't let it continue if there is an issue
            JsonObjectRequest newUserRequest = new JsonObjectRequest(Request.Method.POST, SparkRequester.SERVER_URL + SparkRequester.POST_NEW_USER, newUser,
                    response -> {
                        //check if creating the user was successful
                        try {
                            if (response.getBoolean("user_created")) {
                                Toast.makeText(this, R.string.new_user_success, Toast.LENGTH_SHORT).show();
                                Intent data = new Intent();
                                data.putExtra(LoginActivity.NEW_EMAIL, emailPrompt.getText().toString().trim());
                                data.putExtra(LoginActivity.NEW_PASSWORD, passwordPrompt.getText().toString().trim());
                                setResult(LoginActivity.RESULT_OK, data);
                                finish();
                            }
                        } catch (JSONException ignored) {
                        } //TODO don't just ignore it
                    }, error -> Toast.makeText(this, R.string.server_error, Toast.LENGTH_SHORT).show());
            RequestQueueWrapper.getRequestQueue(this).add(newUserRequest);
        }

    }

    private boolean validateNewUserParams() {
        String newEmail = emailPrompt.getText().toString();
        String newPassword = passwordPrompt.getText().toString();
        String newHandle = handlePrompt.getText().toString();
        if (newEmail.trim().equals("") || newPassword.trim().equals("")) {
            Toast.makeText(this, R.string.empty_registration, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newEmail.contains("@") && !newEmail.contains(".")) {
            Toast.makeText(this, R.string.invalid_email, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newPassword.length() > 24) {
            Toast.makeText(this, R.string.invalid_password, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newHandle.length() > 12) {
            Toast.makeText(this, R.string.invalid_handle, Toast.LENGTH_SHORT).show();
            return false;
        }
        //check for special characters
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(newHandle);
        if (m.matches()) {
            Toast.makeText(this, R.string.invalid_handle, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
