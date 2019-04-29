package davidwindsor.firstproj.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * TODO DOCUMENTATION
 */

public class RequestQueueWrapper {
    private static RequestQueueWrapper wrapper;

    @NonNull
    public static RequestQueue getRequestQueue(Context context) {
        return Volley.newRequestQueue(context);
    }
}
