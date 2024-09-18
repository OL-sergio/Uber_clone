package udemy.java.uber_clone.adpter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.auth.User;

import java.util.List;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.model.Request;
import udemy.java.uber_clone.model.Users;

public class RequestsAdpter extends RecyclerView.Adapter<RequestsAdpter.MyViewHolder> {

    private List<Request> requests;
    private final Context context;
    private final Users driver;

    public RequestsAdpter(List<Request> requests, Context context, Users driver) {
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
        holder.distance.setText("1.5 km - distancia aproximada");

    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}

