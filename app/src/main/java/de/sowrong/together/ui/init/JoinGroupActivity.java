package de.sowrong.together.ui.init;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.sowrong.together.R;
import de.sowrong.together.data.Group;
import de.sowrong.together.data.Role;
import de.sowrong.together.data.User;

public class JoinGroupActivity extends AppCompatActivity {
    User self;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        self = null;

        getOwnUserData();

        setContentView(R.layout.activity_join_group);

        findViewById(R.id.joinGroup).setOnClickListener(view -> {
            TextView textView = findViewById(R.id.groupIdTextView);
            String id = textView.getText().toString();

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference groupReference = db.getReference().child("groups/" + id);
            groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Log.d("JOIN", "Group " + id + " exists");

                        if (self != null) {
                            self.setGroupId(id);

                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference refUser = database.getReference("users/" + self.getId());
                            refUser.setValue(self);

                            Role role = new Role();
                            role.setUserId(self.getId());
                            role.setRole("user");

                            DatabaseReference refGroup = database.getReference("groups/" + id + "/member/" + self.getId());
                            refGroup.setValue(role);

                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Fehler beim lesen der Benutzerdaten", Toast.LENGTH_SHORT).show();
                            getOwnUserData();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Keine Gruppe mit der eingegebenen ID gefunden", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("MAIN", "failed to update users", error.toException());
                }
            });
        });
    }
}
