package udemy.java.uber_clone.config;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import udemy.java.uber_clone.activity.PassengerActivity;
import udemy.java.uber_clone.activity.RequestsActivity;
import udemy.java.uber_clone.model.Users;

public class UserFirebase {

    private static FirebaseUser getUserFirebase() {
        FirebaseAuth user = FirebaseConfiguration.getFirebaseAuth();
        return user.getCurrentUser();

    }


public static Users getUserDataLogged(){
        FirebaseUser firebaseUser = getUserFirebase();
        Users user = new Users();
        user.setId(firebaseUser.getUid());
        user.setName(firebaseUser.getDisplayName());
        user.setEmail(firebaseUser.getEmail());
        return user;
    }

    public static boolean updateUserName(String name) {
        try {

            FirebaseUser user = getUserFirebase();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();
            user.updateProfile(profile).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    System.out.println("Error updating name");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true ;
    }


    public static void rediretUserLoogedIn(Activity activity) {

        FirebaseUser user = getUserFirebase();

        if (user != null) {

            DatabaseReference userRef = FirebaseConfiguration.getFirebaseDatabase()
                    .child("users")
                    .child(getUserId());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Users user = snapshot.getValue(Users.class);
                    assert user != null;
                    String userType = user.getUserType();

                    if ( userType.equals("D") ) {
                        activity.startActivity(new Intent(activity, RequestsActivity.class));
                        System.out.println("Driver");

                    } else if ( userType.equals("P") ) {
                        activity.startActivity(new Intent(activity, PassengerActivity.class));
                        System.out.println("Passenger");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public static String getUserId() {
        return getUserFirebase().getUid();
    }

    public static void updatedDataLocation(double latitude, double longitude) {
        DatabaseReference userLocation = FirebaseConfiguration.getFirebaseDatabase()
                .child("location_user");

        GeoFire geoFire = new GeoFire(userLocation);

        Users userLogged =  UserFirebase.getUserDataLogged();

        geoFire.setLocation(
                userLogged.getId()
                ,new GeoLocation(
                        latitude , longitude
                ),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null) {
                            Log.d("Erro", "Erro saving location");
                        }else {
                            Log.d("Success", "Location saved");
                        }
                    }
                }
        );
    }
}
