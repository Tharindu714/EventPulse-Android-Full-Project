package com.deltacodex.EventPulse.Utils;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {

    private static final OkHttpClient client = new OkHttpClient();

    public static boolean isInternetAvailable() {
        Request request = new Request.Builder()
                .url("https://www.google.com")  // Ping Google
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isFirestoreAvailable() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> task = db.collection("test").document("ping").get();

        try {
            Tasks.await(task, 3, TimeUnit.SECONDS);  // Timeout in 3 seconds
            return task.isSuccessful() && task.getResult().exists();
        } catch (Exception e) {
            return false;
        }
    }
}
