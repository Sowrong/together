package de.sowrong.together.ui.wallet;

import android.content.Context;
import android.content.Intent;
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
import de.sowrong.together.data.Group;
import de.sowrong.together.data.Member;
import de.sowrong.together.ui.calendar.DetailsCalenderEntryActivity;

public class WalletFragment extends Fragment {
    private ViewGroup walletGroup;
    private WalletViewModel model;
    private TextView textViewOwnBalance;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tasks_wallet, container, false);

        textViewOwnBalance = root.findViewById(R.id.ownBalancePig);
        model = ViewModelProviders.of(requireActivity()).get(WalletViewModel.class);

        model.getMembers().observe(this, membersMap -> {

            String userId = Group.getInstance().getOwnUserId();

            Log.d("WalletFragment", "called observe");
            Log.d("WalletFragment", "#elements: " + membersMap.size());
            Log.d("WalletFragment", "userId: " + userId);

            if (membersMap.isEmpty() || !membersMap.containsKey(userId))
                return;

            setTextViewToBalance(textViewOwnBalance, membersMap.get(userId).getBalance());

            walletGroup = root.findViewById(R.id.walletDetailItems);
            walletGroup.removeAllViews();

            for (Map.Entry<String, Member> entry : membersMap.entrySet()) {
                Member member = entry.getValue();
                walletGroup.addView(createWalletItem(inflater, member.getName(), member.getBalance()));

                Log.d("WalletFragment", "creating entry for user " + member.getName());
            }
        });


        root.findViewById(R.id.walletDetailsCard).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListTransactionsActivity.class);
            startActivity(intent);
        });
        root.findViewById(R.id.walletDetailItems).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListTransactionsActivity.class);
            startActivity(intent);
        });
        root.findViewById(R.id.walletDetailsTextView).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListTransactionsActivity.class);
            startActivity(intent);
        });

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
}
