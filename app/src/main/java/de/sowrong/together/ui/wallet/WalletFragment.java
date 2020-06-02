package de.sowrong.together.ui.wallet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import java.util.Map;

import de.sowrong.together.R;
import de.sowrong.together.data.User;
import de.sowrong.together.data.Users;

public class WalletFragment extends Fragment implements OnRefreshListener {

    private ViewGroup walletGroup;
    private WalletViewModel model;
    private TextView textViewOwnBalance;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tasks_wallet, container, false);

        textViewOwnBalance = root.findViewById(R.id.ownBalancePig);
        model = ViewModelProviders.of(requireActivity()).get(WalletViewModel.class);

        userId = Users.getOwnId();

        model.getUsers().observe(this, usersMap -> {
            Log.d("WalletFragment", "called observe");


            Log.d("WalletFragment", "#elements: " + usersMap.size());
            Log.d("WalletFragment", "userId: " + userId);

            if (usersMap.isEmpty() || !usersMap.containsKey(userId))
                return;


            setTextViewToBalance(textViewOwnBalance, usersMap.get(userId).getBalance());


            walletGroup = root.findViewById(R.id.walletDetailItems);
            walletGroup.removeAllViews();

            for (Map.Entry<String, User> entry : usersMap.entrySet()) {
                User user = entry.getValue();
                walletGroup.addView(createWalletItem(inflater, user.getName(), user.getBalance()));

                Log.d("WalletFragment", "creating entry for user " + user.getName());
            }
        });

        swipeRefreshLayout = root.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        return root;
    }


    private View createWalletItem(LayoutInflater inflater, String name, double value) {
        View walletItem = inflater.inflate(R.layout.wallet_item, null);
        TextView nameView = walletItem.findViewById(R.id.name);
        TextView balanceView = walletItem.findViewById(R.id.balance);

        setTextViewToBalance(balanceView, value);
        nameView.setText(name);

        return walletItem;
    }

    private void setTextViewToBalance(TextView balanceView, double balance) {
        balanceView.setText(String.format("%s%.2f€", balance > 0? "+":"", balance));

        if (balance == 0d) {
            balanceView.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        else if (balance > 0) {
            balanceView.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        else {
            balanceView.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    public void onRefresh() {
        Log.d("WalletFragment", "Started refresh");

        /*
        model.getBalance().observe(this, balance -> {
            setTextViewToBalance(textViewOwnBalance, balance);
        });
         */

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
