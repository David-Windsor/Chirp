package davidwindsor.firstproj.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * SparkRequester is responsible for making connections to the server and requesting different data
 * will remain static so only one connection gets opened at a time
 * Depreciating to find usages of this still. Unfortunately because Volley return to the main thread
 * I'm having timing issue and just general threading issues I haven't been able to figure out
 */
public abstract class SparkRequester {
    public static final String SERVER_URL = "http://chirpserver-env2.us-east-2.elasticbeanstalk.com/";
    public static final String FIND_BY_EMAIL = "users/fe/";
    public static final String FIND_BY_HANDLE = "users/fh/";
    public static final String POST_NEW_USER = "users/c";
    public static final String GET_CHIRPS = "chirps";
    public static final String FIND_BY_ID = "users/fi/";
    public static final String POST_NEW_CHIRP = "chirps/a";
    //TODO make fields for all the param directories if that's how it works

    @Deprecated
    public synchronized static boolean isExistentUser(@NonNull String email, @NonNull String password, Context context) {
        final ServerResultHandler test = new ServerResultHandler();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, SERVER_URL + FIND_BY_EMAIL + email.trim(),
                response -> {
                    if (response != null && response.contains(email.trim()))
                        test.setResult(true);
                    else
                        test.setResult(false);
                }
                , error -> {
        });
        RequestQueueWrapper.getRequestQueue(context).add(stringRequest);
        return test.getResult();
    }

    @Deprecated
    @Nullable
    public synchronized static User getUserByEmail(@NonNull String email, @NonNull String password, Context context) {
        //get it and return it, otherwise null and let the caller deal with that
        final User user = new User();
        //We're expecting a JSON object in return
        JsonObjectRequest userRequest = new JsonObjectRequest(Request.Method.GET,
                SERVER_URL + FIND_BY_EMAIL + email.trim(), null,
                response -> {
                    if (response != null) {
                        try {
                            //set our user fields
                            user.setEmail(response.getString("email"));
                            user.setPassword(response.getString("password"));
                            user.setHandle(response.getString("handle"));
                            user.setId(response.getString("id"));
                        } catch (JSONException ignored) {
                        }
                    }
                }, error -> {
        });
        RequestQueueWrapper.getRequestQueue(context).add(userRequest);
        //if the ID is still at default we failed and should return null, or if the passwords don't match return null
        if (user.getId().equals("") && user.getPassword().equals(password.trim()))
            return user;
        return null;
    }


    /**
     * @param email    email to be used by new user
     * @param password password to be used by new user
     * @param handle   handle to be used by new user
     * @return return true if the new user has been made, false if any param is in use already, since the RegistrationActivity should've checked each field already
     */
    @Deprecated
    public synchronized static boolean makeNewUser(@NonNull String email, @NonNull String password, @NonNull String handle, Context context) {
        //check that the user doesn't already exist by email and password, and the handle isn't already taken
        ServerResultHandler test = new ServerResultHandler();

        if (!isExistentUser(email, password, context) && !isExistentHandle(handle, context)) {
            JSONObject newUser = new JSONObject();

            try {
                newUser.put("email", email.trim());
                newUser.put("password", password.trim());
                newUser.put("handle", handle.trim());
            } catch (JSONException ignored) {
            }
            //TODO Make the user and inform of success
            //TODO consider using an ErrorListener and actually doing something with it
            JsonObjectRequest userPost = new JsonObjectRequest(Request.Method.POST, SERVER_URL + POST_NEW_USER, newUser,
                    response -> {
                        try {
                            if (response != null && response.getBoolean("user_created"))
                                test.setResult(true);
                        } catch (JSONException e) {
                            test.setResult(false);
                        }
                    }, null);
            RequestQueueWrapper.getRequestQueue(context).add(userPost);
            return test.getResult();
        }
        return false;
    }

    @Deprecated
    private synchronized static boolean isExistentHandle(@NonNull String handle, Context context) {
        final ServerResultHandler test = new ServerResultHandler();
        JsonObjectRequest handleRequest = new JsonObjectRequest(Request.Method.GET, SERVER_URL + FIND_BY_HANDLE + handle.trim(), null,
                response -> {
                    try {
                        if (response != null && response.getString("handle").equals(handle.trim()))
                            test.setResult(true);
                        else
                            test.setResult(false);
                        //TODO consider throwing these exceptions up so we know the difference between false and just not working properly
                    } catch (JSONException ignored) {
                        test.setResult(false);
                    }
                }, error -> {
        });
        RequestQueueWrapper.getRequestQueue(context).add(handleRequest);
        return test.getResult();
    }
}
