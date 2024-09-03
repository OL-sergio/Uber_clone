package udemy.java.uber_clone.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConfiguration {

    private static DatabaseReference reference;
    private static FirebaseAuth auth;

    public static DatabaseReference getFirebaseDatabase() {
        if (reference == null) {
            reference = FirebaseDatabase.getInstance().getReference();
        }
        return reference;
    }

    public static FirebaseAuth getFirebaseAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

}
