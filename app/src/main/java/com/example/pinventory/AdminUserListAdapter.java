package com.example.pinventory;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminUserListAdapter extends RecyclerView.Adapter<AdminUserListAdapter.UserViewHolder> {

    private List<User> userList;

    public AdminUserListAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.txtEmail.setText(user.userName);
        holder.txtRole.setText(user.role);

        holder.btnPromote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promoteToAdmin(user,holder);
            }
        });

        holder.btnDisableAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableAccount(user,holder);
            }
        });

        holder.btnEnableAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableAccount(user,holder);
            }
        });

        if ("admin".equals(user.role)) {
            holder.btnPromote.setVisibility(View.GONE);
            holder.ivStar.setVisibility(View.VISIBLE);
        } else {
            holder.btnPromote.setVisibility(View.VISIBLE);
            holder.ivStar.setVisibility(View.GONE);
        }

        if (user.status == false) {
            holder.btnDisableAcc.setVisibility(View.GONE);
            holder.btnEnableAcc.setVisibility(View.VISIBLE);

        } else {
            holder.btnDisableAcc.setVisibility(View.VISIBLE);
            holder.btnEnableAcc.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,ivStar;
        TextView txtEmail, txtRole;
        Button btnPromote, btnDisableAcc, btnEnableAcc;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            ivStar = itemView.findViewById(R.id.IV_star);
            txtEmail = itemView.findViewById(R.id.txt_Email);
            txtRole = itemView.findViewById(R.id.txt_user_role);
            btnPromote = itemView.findViewById(R.id.btn_promote);
            btnDisableAcc = itemView.findViewById(R.id.btn_disable_acc);
            btnEnableAcc = itemView.findViewById(R.id.btn_enable_acc);
        }
    }
    private void promoteToAdmin(User user,UserViewHolder holder) {
        // Update the user's role to "admin" in Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = database.getReference("Users");

        // Assuming your user node has a unique identifier (UID)
        DatabaseReference currentUserReference = usersReference.child(user.getUid());

        // Update the role
        currentUserReference.child("role").setValue("admin");
        holder.btnPromote.setVisibility(View.GONE);
        holder.ivStar.setVisibility(View.VISIBLE);
    }

    private void disableAccount(User user,UserViewHolder holder) {
        // Update the user's role to "admin" in Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = database.getReference("Users");

        // Assuming your user node has a unique identifier (UID)
        DatabaseReference currentUserReference = usersReference.child(user.getUid());

        // Update the role
        currentUserReference.child("status").setValue(false);
        holder.btnDisableAcc.setVisibility(View.GONE);
        holder.btnEnableAcc.setVisibility(View.VISIBLE);
    }

    private void enableAccount(User user,UserViewHolder holder) {
        // Update the user's role to "admin" in Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = database.getReference("Users");

        // Assuming your user node has a unique identifier (UID)
        DatabaseReference currentUserReference = usersReference.child(user.getUid());

        // Update the role
        currentUserReference.child("status").setValue(true);
        holder.btnEnableAcc.setVisibility(View.GONE);
        holder.btnDisableAcc.setVisibility(View.VISIBLE);
    }
}
