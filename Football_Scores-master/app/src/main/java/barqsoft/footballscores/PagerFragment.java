package barqsoft.footballscores;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

import java.util.Locale;

public class PagerFragment extends Fragment
{
    public static final int NUM_PAGES = 5;
    public ViewPager mPagerHandler;
    private MyPagerAdapter mPagerAdapter;
    private MainScreenFragment[] viewFragments = new MainScreenFragment[NUM_PAGES];

    public void selectDate(int year, int month, int day) {
        LocalDate date = new LocalDate(year, month, day);
        setupViewFragments(date);
    }

    private void setupViewFragments(LocalDate date) {
        for (int i = 0;i < NUM_PAGES; i++)
        {
            viewFragments[i] = new MainScreenFragment();
            viewFragments[i].setFragmentDate(date.plusDays(i-2), getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        MyPagerAdapter mPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
        setupViewFragments(new LocalDate());
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setCurrentItem(MainActivity.current_fragment);
        return rootView;
    }


    private class MyPagerAdapter extends FragmentStatePagerAdapter
    {
        @Override
        public Fragment getItem(int i)
        {
            return viewFragments[i];
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }

        public MyPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            LocalDate date = viewFragments[position].getFragmentDate();
            String dayName = getDayName(getActivity(),date);
            return String.format(
                    getString(R.string.day_title_format),
                    dayName,
                    date.toString(getString(R.string.month_day_format))
            );
        }

        public String getDayName(Context context, LocalDate date) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.
            LocalDate today = new LocalDate();
            if (date.equals(today)) {
                return context.getString(R.string.today);
            } else if (date.equals(today.plusDays(1))) {
                return context.getString(R.string.tomorrow);
            } else if (date.equals(today.minusDays(1))) {
                return context.getString(R.string.yesterday);
            } else {
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                return date.toString(getString(R.string.day_of_week_format), Locale.getDefault());
            }
        }
    }
}
