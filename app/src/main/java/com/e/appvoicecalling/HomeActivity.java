package com.e.appvoicecalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.e.appvoicecalling.Adapter.AllUsersAdapter;
import com.e.appvoicecalling.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    SinchClient sinchClient;
    Call call;
    ArrayList<User> userArrayList;
    DatabaseReference reference;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         if (item.getItemId()==R.id.menu_logout)
         {
             if (firebaseUser!=null)
             {
                 auth.signOut();
                 finish();
                 Intent i=new Intent(HomeActivity.this,MainActivity.class);
                 startActivity(i);
             }
         }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        auth=FirebaseAuth.getInstance();
        firebaseUser=auth.getCurrentUser();
        userArrayList=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference().child("User");



        sinchClient= Sinch.getSinchClientBuilder()
                .context(this)
                .userId(firebaseUser.getUid())
                .applicationKey("4568055f-b882-4918-90c4-7d213511f590")
                .applicationSecret("MdgE+ZmeUkWMCzm1UeVkPQ==")
                .environmentHost("clientapi.sinch.com")
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();


        sinchClient.getCallClient().addCallClientListener(new CallClientListener() {
            @Override
            public void onIncomingCall(CallClient callClient, final com.sinch.android.rtc.calling.Call incomingcall) {

                AlertDialog alertDialog=new AlertDialog.Builder(HomeActivity.this).create();
                alertDialog.setTitle("Calling");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        call.hangup();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Pick", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        call=incomingcall;
                        call.answer();
                        call.addCallListener(new SinchCallListner());
                        Toast.makeText(HomeActivity.this, "Call is started", Toast.LENGTH_SHORT).show();


                    }
                });

                alertDialog.show();

            }
        });
        sinchClient.start();
        fetchAllData();


    }

    private void fetchAllData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userArrayList.clear();
                for (DataSnapshot dss:dataSnapshot.getChildren())
                {
                    User user=dss.getValue(User.class);
                    userArrayList.add(user);
                }


                AllUsersAdapter adapter=new AllUsersAdapter(HomeActivity.this,userArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(HomeActivity.this, "error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class SinchCallListner implements CallListener
    {

        @Override
        public void onCallProgressing(com.sinch.android.rtc.calling.Call call) {
            Toast.makeText(HomeActivity.this, "call is ringing...", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCallEstablished(com.sinch.android.rtc.calling.Call call) {

            Toast.makeText(HomeActivity.this, "call is established", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCallEnded(com.sinch.android.rtc.calling.Call endedcall) {
            Toast.makeText(HomeActivity.this, "call ended", Toast.LENGTH_SHORT).show();
            call=null;
            endedcall.hangup();
        }

        @Override
        public void onShouldSendPushNotification(com.sinch.android.rtc.calling.Call call, List<PushPair> list) {

        }
    }

    public void callUser(User user)
    {
        if (call==null) {
            call = sinchClient.getCallClient().callUser(user.getUserid());
            call.addCallListener(new SinchCallListner());

            openCallerDialog(call);
        }
    }

    private void openCallerDialog(final Call call) {

        AlertDialog alertDialog=new AlertDialog.Builder(HomeActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Calling");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Hangup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                call.hangup();
            }
        });

        alertDialog.show();
    }
}
