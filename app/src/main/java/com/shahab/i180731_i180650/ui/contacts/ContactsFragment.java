package com.shahab.i180731_i180650.ui.contacts;



import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shahab.i180731_i180650.ContactRVAdapter;
import com.shahab.i180731_i180650.ContactRVModel;
import com.shahab.i180731_i180650.LoginActivity;
import com.shahab.i180731_i180650.Profile;
import com.shahab.i180731_i180650.R;
import com.shahab.i180731_i180650.databinding.FragmentContactsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContactsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private ContactsViewModel notificationsViewModel;
    private FragmentContactsBinding binding;

    List<ContactRVModel> ls = new ArrayList<ContactRVModel>();
    RecyclerView rv;
    ContactRVAdapter adapter;

    SearchView searchView;
    ArrayList<ContactRVModel> arrayList = new ArrayList<ContactRVModel>();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(ContactsViewModel.class);

        binding = FragmentContactsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // ******************* WATCHOUT, NASTY BUGS AHEAD  ***********************
        rv = root.findViewById(R.id.contactRV);
        adapter = new ContactRVAdapter(getActivity(), ls);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);

        getContactsList();
        arrayList.addAll(ls);

        ls.add(new ContactRVModel("Shahab", "0"));
        ls.add(new ContactRVModel("Piyush", "1"));
        ls.add(new ContactRVModel("Usama", "2"));
        ls.add(new ContactRVModel("Zain", "3"));
        ls.add(new ContactRVModel("Jenny", "4"));
        ls.add(new ContactRVModel("Janet", "5"));
        ls.add(new ContactRVModel("Chad", "6"));

        //FILTERING RESULTS
//        adapter = new ContactRVAdapter(getActivity(), arrayList);
////        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
////        rv.setLayoutManager(lm);
//        rv.setAdapter(adapter);
//
        searchView = root.findViewById(R.id.contacts_search);
        searchView.setOnQueryTextListener(this);
//
//        adapter = new ContactRVAdapter(getActivity(), ls);
//        rv.setAdapter(adapter);

        //*********************************************************************
        return root;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
//        notifyDataSetChanged();
        adapter.filter(text);
        return true;
    }

    // NOTE: make sure you've given permission to the app to read contacts from the setting
    //get all name and contact number from mobile's contact list
    private void getContactsList() {
        ContentResolver resolver = getActivity().getContentResolver();
        Map<Long, List<String>> phones = new HashMap<>();
        Cursor getContactsCursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.NUMBER},
                null, null, null);

        if (getContactsCursor != null) {
            while (getContactsCursor.moveToNext()) {
                long contactId = getContactsCursor.getLong(0);
                String phone = getContactsCursor.getString(1);
                List<String> list;
                if (phones.containsKey(contactId)) {
                    list = phones.get(contactId);
                } else {
                    list = new ArrayList<>();
                    phones.put(contactId, list);
                }
                list.add(phone);
            }
            getContactsCursor.close();
        }    getContactsCursor = resolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                new String[]{
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI},
                null, null, null);
        while (getContactsCursor != null &&
                getContactsCursor.moveToNext()) {
            long contactId = getContactsCursor.getLong(0);
            String name = getContactsCursor.getString(1);
            String photo = getContactsCursor.getString(2);
            List<String> contactPhones = phones.get(contactId);
            if (contactPhones != null) {
                for (String phone :
                        contactPhones) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference contact_ref = database.getReference().child("users");

                    contact_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot data:snapshot.getChildren()){
                                String frienduserid = data.getKey().toString();
                                DatabaseReference frienduser = contact_ref.child(frienduserid);
                                DatabaseReference profileref = frienduser.child("Profile");
                                DatabaseReference contact_no_ref = profileref.child("contact_no");

                                contact_no_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        String contact_no = snapshot.getValue().toString();
//
//                                        if (contact_no.equals(phone)){
//                                            Toast.makeText(getActivity(), name+" "+phone, Toast.LENGTH_SHORT).show();
//                                            ls.add(new ContactRVModel(name, phone));
////                                            Toast.makeText(getActivity(), ls.toString(), Toast.LENGTH_SHORT).show();
//
//                                            adapter.notifyDataSetChanged();
//
//                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.d("TAG", phone);

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                    ls.add(new ContactRVModel(name, phone));
                }
            }
        }
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}