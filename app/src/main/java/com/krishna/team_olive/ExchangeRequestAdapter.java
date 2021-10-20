package com.krishna.team_olive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.krishna.team_olive.SendNotificationPack.APIService;
import com.krishna.team_olive.SendNotificationPack.Client;
import com.krishna.team_olive.SendNotificationPack.Data;
import com.krishna.team_olive.SendNotificationPack.MyResponse;
import com.krishna.team_olive.SendNotificationPack.NotificationSender;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExchangeRequestAdapter extends RecyclerView.Adapter<ExchangeRequestAdapter.ViewHolder>{

    private Context context;
    private List<ExchangeModel> mylist;
    private APIService apiService;

    public ExchangeRequestAdapter(Context context, List<ExchangeModel> mylist) {
        this.context = context;
        this.mylist = mylist;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_customer_name, tv_item_name;
        private Button btn_accept_req, btn_decline_req;
        private RelativeLayout rl_exchange_req;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_customer_name = itemView.findViewById(R.id.tv_custumer_name);
            tv_item_name = itemView.findViewById(R.id.tv_item_name_exchange_req);
            btn_accept_req = itemView.findViewById(R.id.btn_accept_exchange_req);
            btn_decline_req = itemView.findViewById(R.id.btn_decline_exchange_req);
            rl_exchange_req = itemView.findViewById(R.id.rl_exchange_req);
        }
    }

    @NonNull
    @Override
    public ExchangeRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.exchange_req_item,parent,false);
        return new ExchangeRequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExchangeRequestAdapter.ViewHolder holder, int position) {
        ExchangeModel object_exc_req = mylist.get(position);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        holder.tv_item_name.setText(object_exc_req.getItem_name());
        holder.tv_customer_name.setText(object_exc_req.getClient_name());

        holder.btn_accept_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Myexchanges").child(object_exc_req.getClient_uid()).push();
                firebaseDatabase.setValue(object_exc_req.getPostid());

                DatabaseReference firebaseDatabase2 = FirebaseDatabase.getInstance().getReference().child("Notifications").child(object_exc_req.getClient_uid());
                NotificationsModel notificationsModel = new NotificationsModel(object_exc_req.getUser_uid(), "accepted your request", object_exc_req.getPostid(), 2);
                firebaseDatabase2.push().setValue(notificationsModel);

                FirebaseDatabase.getInstance().getReference().child("Exchange Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(object_exc_req.getRequest_uid()).removeValue();

                String client_uid = object_exc_req.getClient_uid();

                FirebaseDatabase.getInstance().getReference().child("Tokens").child(client_uid).child("token").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String token = snapshot.getValue(String.class);
                        sendNotifications(token, "Exchange request accepted", "Your exchange request for "+object_exc_req.getItem_name()+" has been accepted");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                ////DELETE bhi krna h !!!!!!!!!!!
            }
        });

        holder.btn_decline_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("Exchange Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(object_exc_req.getRequest_uid()).removeValue();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mylist.size();
    }

    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(context, "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }
}
