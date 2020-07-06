package de.sowrong.together.ui.init;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.sowrong.together.R;
import de.sowrong.together.data.Group;
import de.sowrong.together.data.Role;
import de.sowrong.together.data.User;

public class CreateGroupActivity extends AppCompatActivity {
    User self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        self = null;

        getOwnUserData();

        findViewById(R.id.createGroup).setOnClickListener(view -> {
            createNewGroup();
        });
    }


    void createNewGroup() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        String id = Group.randomId(8);
        DatabaseReference groupReference = db.getReference().child("groups/" + id);

        groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null && self != null) {
                    Log.d("CREATE", "Creating new group with id " + id);

                    final FirebaseDatabase database = FirebaseDatabase.getInstance();

                    self.setGroupId(id);
                    DatabaseReference refUser = database.getReference("users/" + self.getId());
                    refUser.setValue(self);

                    Role role = new Role();
                    role.setUserId(self.getId());
                    role.setRole("admin");

                    DatabaseReference refGroup = database.getReference("groups/" + id + "/member/" + self.getId());
                    refGroup.setValue(role);


                    //TODO add details!!

                    finish();
                } else {
                    getOwnUserData();

                    Toast.makeText(getApplicationContext(), "Fehler, bitte versuch es erneut", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("CREATE", "failed to create group", error.toException());
            }
        });
    }

    private void getOwnUserData() {
        String username = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        DatabaseReference userReference = db.getReference().child("users/" + username);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    self = dataSnapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("MAIN", "failed to update users", error.toException());
            }
        });
    }
}
