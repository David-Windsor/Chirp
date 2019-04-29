package davidwindsor.firstproj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import davidwindsor.firstproj.R;
import davidwindsor.firstproj.models.RequestQueueWrapper;
import davidwindsor.firstproj.models.SparkRequester;
import davidwindsor.firstproj.models.User;

/**
 * @author David Windsor
 *         The first activity the App should launch into
 *         The Activity won't have a fragment because nothing here will need to be reused by any other parts of the activity
 *         This will also allow us to avoid the communication between Activity and fragment and allow for a quicker login
 *         Everything after this and RegistrationActivity should rely on fragments unless it is something that doesn't have any reusability
 *         <p>
 *         <b><i>*WARNING* Emails and Passwords will just be handeled in cleartext, no salting and hashing or anytyhing like that
 *         If this was actually going to production it would need to change, but it isn't because we have Twitter, so this will do *WARNING*</i></b>
 *         <p>
 *         If I can figure out dealing with Volley returning to the main thread server communication will be taken out of this activity
 */
@SuppressWarnings("FieldCanBeLocal")
public class LoginActivity extends AppCompatActivity {
    private static final String LOGIN_FILE_NAME = "AUTO_LOGIN_FILE";
    public static final String USER_BUNDLE_KEY = "USER_BUNDLE_KEY";
    public static final String NEW_EMAIL = "NEW_EMAIL";
    public static final String NEW_PASSWORD = "NEW_PASSWORD";
    private static final int NEW_USER_REQUEST = 0;
    private EditText emailPrompt, passwordPrompt;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailPrompt = findViewById(R.id.login_email_prompt);
        passwordPrompt = findViewById(R.id.login_password_prompt);

        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> attemptLogin(false));

        if (new File(getFilesDir(), LOGIN_FILE_NAME).exists())
            attemptLogin(true);
    }

    /**
     * @param requestCode The request code we sent, we only have one so we won't need to use this
     * @param resultCode  The result, if we get back RESULT_CANCELLED we can just exit, as we didn't get a new user
     * @param data        If successful, Intent holding our new Email and password
     *                    This method is here to know whether or not to send a toast informing the user to input their new email and password
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, R.string.new_user_success, Toast.LENGTH_SHORT).show();
            emailPrompt.setText(data.getStringExtra(NEW_EMAIL));
            passwordPrompt.setText(data.getStringExtra(NEW_PASSWORD));
            attemptLogin(false);
        }
    }

    /**
     * Login File will have a simple format, two strings, first being email and next being password
     */
    private void attemptLogin(boolean isAutoLogin) {
        String email, password;
        if (isAutoLogin) {
            Scanner fin;
            try {
                fin = new Scanner(openFileInput(LOGIN_FILE_NAME));
            } catch (FileNotFoundException e) {
                //we were trying for an auto login, so return and wait for another, non-automatic attempt
                return;
            }
            email = fin.next();
            password = fin.next();
        } else {
            email = emailPrompt.getText().toString();
            password = passwordPrompt.getText().toString();
        }
        //now request the object, and on response we will process it and determine what to do\
        JsonObjectRequest userRequest = new JsonObjectRequest(Request.Method.GET, SparkRequester.SERVER_URL + SparkRequester.FIND_BY_EMAIL + email.trim(), null,
                response -> {
                    if (response == null) {
                        Toast.makeText(this, R.string.null_response, Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            User user = new User(response.getString("email"),
                                    response.getString("handle"),
                                    response.getString("password"),
                                    response.getString("id"));
                            if (user.getPassword().equals(password.trim())) {
                                //this is now a valid user, package, make a new auto-login file, and send it to the HomeActivity
                                Intent intent = new Intent(this, HomeActivity.class);
                                intent.putExtra(USER_BUNDLE_KEY, user);
                                makeAutoLoginFile(email, password);
                                startActivity(intent);
                            } else {
                                Toast.makeText(this, R.string.invalid_password, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, R.string.login_failed_toast_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                , error -> {
            Toast.makeText(this, R.string.server_error, Toast.LENGTH_SHORT).show();
        });
        RequestQueueWrapper.getRequestQueue(this).add(userRequest);
    }


    public void onRegisterClick(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivityForResult(intent, NEW_USER_REQUEST);
    }

    private void makeAutoLoginFile(String email, String password) {
        //delete the current file, then make a new one containing the info we want
        this.deleteFile(LOGIN_FILE_NAME);
        try {
            FileOutputStream fout = openFileOutput(LOGIN_FILE_NAME, MODE_PRIVATE);
            OutputStreamWriter output = new OutputStreamWriter(fout);
            output.write(email);
            output.write(" ");
            output.write(password);
            output.flush();
            output.close();
        } catch (IOException ioe) {
            Toast.makeText(this, R.string.auto_login_file_creation_failed, Toast.LENGTH_LONG).show();
        }
    }
}
