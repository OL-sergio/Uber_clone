package udemy.java.uber_clone.helpers;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import udemy.java.uber_clone.R;
import udemy.java.uber_clone.activity.PassengerActivity;
import udemy.java.uber_clone.activity.RequestsActivity;
import udemy.java.uber_clone.model.Request;
import udemy.java.uber_clone.model.RequestActive;


public class TripSummaryDialog extends AppCompatActivity {

    public void dialogTripSummaryClose(String result , Request retriveRequest , Context context ) {

        Activity activity = (Activity) context;

        if (activity.isFinishing() || activity.isDestroyed()) {
            return; // Do not show the dialog if the activity is not valid
        }

       AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(R.string.total_da_viagem)
                .setMessage(getString(R.string.total_da_viagem_pagar) + result)
                .setCancelable(false)
                .setNegativeButton(R.string.encerrar_viagem, (dialog, which) -> {
                    try {
                        retriveRequest.setStatus(Request.STATUS_CLOSED);
                        retriveRequest.updateRequest();
                        Intent intent = new Intent(activity, PassengerActivity.class);
                        activity.startActivity(intent);;
                        activity.finish();
                    } catch (Exception e) {
                        // Handle the exception, e.g., show a Toast or log the error
                        Toast.makeText(activity, R.string.erro_ao_encerrar_a_viagem, Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void dialogTripSummary(String result, Context context) {

        Activity activity = (Activity) context;

        if (activity.isFinishing() || activity.isDestroyed()) {
            return; // Do not show the dialog if the activity is not valid
        }

       AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle( R.string.total_da_viagem )
                .setMessage(getString( R.string.total_pagar_pelo_cliente) + result )
                .setCancelable(false)
                .setNegativeButton( R.string.encerrar_viagem, (dialog, which) -> {
                    Intent intent = new Intent(activity, RequestsActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
