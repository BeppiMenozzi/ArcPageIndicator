package it.beppi.arcpageindicator_sample;

import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * Created by Beppi on 30/12/2016.
 */

public class CustomPagerAdapter extends PagerAdapter {
    View contentView;

    public CustomPagerAdapter(View contentView) {
        this.contentView = contentView;

    }

    public Object instantiateItem(View collection, int position) {
        int resId = 0;
        switch (position) {
            case 0: resId = R.id.page_1; break;
            case 1: resId = R.id.page_2; break;
            case 2: resId = R.id.page_3; break;
            case 3: resId = R.id.page_4; break;
            case 4: resId = R.id.page_5; break;
            case 5: resId = R.id.page_6; break;
            case 6: resId = R.id.page_7; break;
            case 7: resId = R.id.page_8; break;
            case 8: resId = R.id.page_9; break;
            case 9: resId = R.id.page_10; break;
            case 10: resId = R.id.page_11; break;
            case 11: resId = R.id.page_12; break;
        }
        return contentView.findViewById(resId);
    }
    @Override
    public int getCount() {
        return 12;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }
}
