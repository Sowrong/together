package de.sowrong.together.ui.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import de.sowrong.together.R;

public class WalletFragment extends Fragment {

    private WalletViewModel walletViewModel;
    private ViewGroup walletGroup;
    private final boolean POSITIVE = true;
    private final boolean NEGATIVE = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tasks_wallet, container, false);

        walletGroup = root.findViewById(R.id.walletDetailItems);

        walletGroup.addView(createWalletItem(inflater, "Marco", 1.50, POSITIVE));
        walletGroup.addView(createWalletItem(inflater, "Lisa", 0.50, POSITIVE));
        walletGroup.addView(createWalletItem(inflater, "Daniel", 0.00, POSITIVE));
        walletGroup.addView(createWalletItem(inflater, "Simon", 2.00, NEGATIVE));

        return root;
    }

    private View createWalletItem(LayoutInflater inflater, String name, double value, boolean positive) {
        View walletItem = inflater.inflate(R.layout.wallet_item, null);
        TextView nameView = walletItem.findViewById(R.id.name);
        TextView balanceView = walletItem.findViewById(R.id.balance);

        nameView.setText(name);
        balanceView.setText(String.format("%s%.2f€", positive? "+":"-", value));

        if (value == 0d) {
            balanceView.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        else if (positive) {
            balanceView.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        else {
            balanceView.setTextColor(getResources().getColor(R.color.colorAccent));
        }

        return walletItem;
    }
}
