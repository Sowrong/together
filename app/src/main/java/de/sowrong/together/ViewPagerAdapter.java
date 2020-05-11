package de.sowrong.together;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.sowrong.together.ui.CardFragment;
import de.sowrong.together.ui.calendar.CalendarFragment;
import de.sowrong.together.ui.cleaning.CleaningFragment;
import de.sowrong.together.ui.shoppingList.ShoppingListFragment;
import de.sowrong.together.ui.wallet.WalletFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int CARD_ITEM_SIZE = 4;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull @Override public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return new CleaningFragment();
            case 1:
                return new CalendarFragment();
            case 2:
                return new WalletFragment();
            case 3:
                return new ShoppingListFragment();
            default:
                return CardFragment.newInstance(position);
        }
    }
    @Override public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
}