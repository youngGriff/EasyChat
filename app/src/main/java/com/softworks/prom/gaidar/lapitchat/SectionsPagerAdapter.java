package com.softworks.prom.gaidar.lapitchat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
/**
 * Created by Andrew on 25.08.2017.
 */

class SectionsPagerAdapter extends FragmentPagerAdapter {
     SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatsFragment();
            case 1:
                return new FriendsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {

            case 0:
                return "Chats";
            case 1:
                return "Friends";
            default:
                return "";
        }

    }
}
