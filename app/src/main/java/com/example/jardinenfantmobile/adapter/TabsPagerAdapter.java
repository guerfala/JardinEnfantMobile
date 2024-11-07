package com.example.jardinenfantmobile.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.jardinenfantmobile.fragment.FirstFragment;
import com.example.jardinenfantmobile.fragment.SecondFragment;

public class TabsPagerAdapter extends FragmentStateAdapter {

    public TabsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FirstFragment(); // Remplace par le fragment du premier onglet
            case 1:
                return new SecondFragment(); // Remplace par le fragment du deuxième onglet
            default:
                return new FirstFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Nombre total d'onglets
    }
}
