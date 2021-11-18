package com.shahab.i180731_i180650.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.shahab.i180731_i180650.ChatRVAdapter;
import com.shahab.i180731_i180650.ChatRVModel;
import com.shahab.i180731_i180650.NewChatActivity;
import com.shahab.i180731_i180650.R;
import com.shahab.i180731_i180650.databinding.FragmentChatBinding;

import java.util.ArrayList;
import java.util.List;

import android.widget.SearchView;
import android.widget.Toast;

import javax.security.auth.callback.Callback;


public class ChatFragment extends Fragment implements SearchView.OnQueryTextListener{
    boolean onlineStatus;

    private ChatViewModel dashboardViewModel;
    private FragmentChatBinding binding;

    List<ChatRVModel> ls;
    RecyclerView rv;
    ChatRVAdapter adapter;

    SearchView searchView;
    ArrayList<ChatRVModel> arraylist = new ArrayList<ChatRVModel>();

    ImageView add_chat;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(ChatViewModel.class);

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textChat;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        rv=root.findViewById(R.id.chatRV);

        arraylist.add(new ChatRVModel("john snow", "she is ma queen", "01:35"));
        arraylist.add(new ChatRVModel("mad queen","drakarys ...", "06:40"));
        arraylist.add(new ChatRVModel("dumb and dumber","dany forgot ...", "06:09"));

        adapter=new ChatRVAdapter(getActivity(),arraylist);
        RecyclerView.LayoutManager lm=new LinearLayoutManager(getActivity());
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);

        getMessages();


        add_chat = root.findViewById(R.id.add_chat);
        add_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newChatIntent = new Intent(getActivity(), NewChatActivity.class);
                startActivity(newChatIntent);
            }
        });


        // Locate the EditText in listview_main.xml
        searchView = root.findViewById(R.id.chat_search);
        searchView.setOnQueryTextListener(this);

        return root;
    }

    private void getMessages() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("app_values", Context.MODE_PRIVATE);
        String user_id = sharedPref.getString("userid", "none");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference messages_ref = database.getReference("users/"+user_id+"/messages");

        messages_ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data:snapshot.getChildren()){
                    String friend_id = data.getKey();

                    FirebaseDatabase ddataSnapshotatabase2 = FirebaseDatabase.getInstance();
                    DatabaseReference profile_name = database.getReference("users/"+friend_id+"/Profile");



                    profile_name.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String friend_name = snapshot.child("name").getValue().toString();
                            String onlineStatus = snapshot.child("online_status").getValue().toString();
                            boolean isOnline = false;
                            if (onlineStatus.equals("online")) {
                                isOnline = true;
                            }
                            arraylist.add(new ChatRVModel(friend_name,"idk", onlineStatus, friend_id, isOnline));
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference group_messages_ref = database.getReference("groupchat");

        group_messages_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data:snapshot.getChildren()){
                    String group_name = data.child("groupname").getValue(String.class);
                    arraylist.add(new ChatRVModel(group_name,"Decide", "7:01", data.getKey(), false));
                    adapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return true;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}