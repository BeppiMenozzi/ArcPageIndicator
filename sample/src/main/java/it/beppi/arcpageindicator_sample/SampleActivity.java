package it.beppi.arcpageindicator_sample;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import it.beppi.arcpageindicator.ArcPageIndicator;
import it.beppi.arcpageindicator_sample.utils.BTimer;

import static it.beppi.arcpageindicator.ArcPageIndicator.*;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // A very basic ViewPager implementation

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new CustomPagerAdapter(findViewById(R.id.activity_sample)));
        viewPager.setOffscreenPageLimit(12);

//        automaticDemo();
    }













    // ********************** AUTOMATIC DEMO ********************//


    static int n = 0;
    static int example = 0;
    BTimer bt;
    ArcPageIndicator api1, api2;
    ViewPager vp;

    void automaticDemo() {
        vp = (ViewPager) findViewById(R.id.view_pager);

        api1 = (ArcPageIndicator) findViewById(R.id.arc_pi_1);
        api2 = (ArcPageIndicator) findViewById(R.id.arc_pi_2);

        Runnable r = new Runnable() {
            @Override public void run() {
                switch (n % 8) {
                    case 0: showExample(example); example++; break;
                    case 1: vp.setCurrentItem(7); break;
                    case 2: vp.setCurrentItem(8); break;
                    case 3: vp.setCurrentItem(9); break;
                    case 4: vp.setCurrentItem(10); break;
                    case 5: vp.setCurrentItem(9); break;
                    case 6: vp.setCurrentItem(8); break;
                    case 7: vp.setCurrentItem(7); break;
                }
                n++;
            }};
        bt = new BTimer(400, r);

        bt.start();
    }
    void showExample(int n) {
        reset();
        switch (n) {
            case 0: setAnimation(AnimationType.color); break;
            case 1: setAnimation(AnimationType.slide); break;
            case 2: setAnimation(AnimationType.pinch); break;
            case 3: setAnimation(AnimationType.bump); break;
            case 4: setAnimation(AnimationType.rotate); break;
            case 5: setAnimation(AnimationType.rotate_pinch); break;
            case 6: setAnimation(AnimationType.cover); break;
            case 7: setAnimation(AnimationType.fill); break;
            case 8: setAnimation(AnimationType.surround); break;
            case 9: setAnimation(AnimationType.necklace); break;
            case 10: setAnimation(AnimationType.necklace2); break;
            case 11: setAnimation(AnimationType.bump); setHandEnabled(true); break;
            case 12: setAnimation(AnimationType.slide); setSquare(true); break;

            default: stopbt(); break;
        }
        n++;
    }

    void reset() {
        api1.setInvertDirection(false); api2.setInvertDirection(false);
        api1.setHandEnabled(false); api2.setHandEnabled(false);
        api1.setSpotShape(SpotShape.circle); api2.setSpotShape(SpotShape.circle);
    }
    void setAnimation(AnimationType at) {
        api1.setAnimationType(at); api2.setAnimationType(at);
    }
    void setInvertDirection(boolean d) {
        api1.setInvertDirection(d); api2.setInvertDirection(d);
    }
    void setHandEnabled(boolean h) {
        api1.setHandEnabled(h); api2.setHandEnabled(h);
    }
    void setSquare(boolean s) {
        api1.setSpotShape(SpotShape.roundedSquare); api2.setSpotShape(SpotShape.roundedSquare);
    }

    void stopbt() {
        bt.stop();
        n = 0; example = 0;
        // finish();
    }
}
