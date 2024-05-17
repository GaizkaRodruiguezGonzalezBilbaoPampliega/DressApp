// ViewPagerAdapter.java
package com.example.dressapp.adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.dressapp.ArmarioFragment;
import com.example.dressapp.CreatePublicacionFragment;
import com.example.dressapp.ProfileFragment;
import com.example.dressapp.PublicacionesFragment;


public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull AppCompatActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PublicacionesFragment();
            case 1:
                return new CreatePublicacionFragment();
            case 2:
                return new ProfileFragment();
            case 3:
                return new ArmarioFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 4; // número de fragments en el ViewPager2
    }
}
