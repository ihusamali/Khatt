package com.husamali.khatt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Contacts extends Fragment {

    RecyclerView recyclerView;
    ContactAdapter contactAdapter;
    List<UserProfile> users;

    SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts,container,false);

        searchView = view.findViewById(R.id.search);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(contactAdapter);

        users = new ArrayList<>();

        retrieveUsers();


        return view;
    }

    private void filterList(String newText) {
        List<UserProfile> filteredList = new ArrayList<>();
        for (UserProfile user : users){
            if (user.getUsername().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(user);
            }

                contactAdapter.setFilteredList(filteredList);

        }
    }

    private void retrieveUsers() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    UserProfile user = dataSnapshot.getValue(UserProfile.class);

                    assert user !=null;
                    assert firebaseUser !=null;
                    if (!user.getUserUID().equals(firebaseUser.getUid())){
                        users.add(user);
                    }
                }

                contactAdapter = new ContactAdapter(getContext(), users);
                recyclerView.setAdapter(contactAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
