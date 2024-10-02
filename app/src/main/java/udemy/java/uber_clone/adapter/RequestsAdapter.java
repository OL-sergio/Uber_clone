package udemy.java.uber_clone.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.helpers.Locations;
import udemy.java.uber_clone.model.Request;
import udemy.java.uber_clone.model.Users;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.MyViewHolder> {

    private List<Request> requests;
    private final Context context;
    private final Users driver;

    public RequestsAdapter(List<Request> requests, Context context, Users driver) {
        this.requests = requests;
        this.context = context;
        this.driver = driver;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView distance;
        TextView name;
                public MyViewHolder(@NonNull View parent) {
                    super(parent);
                    name = parent.findViewById(R.id.textView_passengerName);
                    distance = parent.findViewById(R.id.textView_requestDistance);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_passenger_requests, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Request request = requests.get(position);
        Users passenger = request.getPassenger();

        Log.d("DEBUG", "onBindViewHolder: " + "name: " +passenger.getName() + "Name: "+ request.getPassenger().getName());
        holder.name.setText(passenger.getName());

        if (driver != null) {
            // Calculate the distance between two points
            LatLng driverLocation = new LatLng(
                    Double.parseDouble(driver.getLatitude()),
                    Double.parseDouble(driver.getLongitude())
            );

            LatLng driverPasseger= new LatLng(
                    Double.parseDouble(passenger.getLatitude()),
                    Double.parseDouble(passenger.getLongitude())
            );

            float distance = Locations.calculateDistance(driverLocation, driverPasseger);
            String distanceFormat = Locations.formatDistance(distance);

            holder.distance.setText(  distanceFormat +"km - distancia aproximada");



        }



    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}

