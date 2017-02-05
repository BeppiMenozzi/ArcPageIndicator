package it.beppi.arcpageindicator;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;
import static it.beppi.arcpageindicator.ArcPageIndicator.AnimationType.bump;
import static it.beppi.arcpageindicator.ArcPageIndicator.AnimationType.color;
import static it.beppi.arcpageindicator.ArcPageIndicator.AnimationType.cover;
import static it.beppi.arcpageindicator.ArcPageIndicator.AnimationType.fill;
import static it.beppi.arcpageindicator.ArcPageIndicator.AnimationType.necklace;
import static it.beppi.arcpageindicator.ArcPageIndicator.AnimationType.necklace2;
import static it.beppi.arcpageindicator.ArcPageIndicator.AnimationType.none;
import static it.beppi.arcpageindicator.ArcPageIndicator.AnimationType.pinch;
import static it.beppi.arcpageindicator.ArcPageIndicator.AnimationType.rotate;
import static it.beppi.arcpageindicator.ArcPageIndicator.AnimationType.rotate_pinch;
import static it.beppi.arcpageindicator.ArcPageIndicator.AnimationType.slide;
import static it.beppi.arcpageindicator.ArcPageIndicator.AnimationType.surround;
import static it.beppi.arcpageindicator.ArcPageIndicator.ArcOrientation.toDown;
import static it.beppi.arcpageindicator.ArcPageIndicator.ArcOrientation.toDownLeft;
import static it.beppi.arcpageindicator.ArcPageIndicator.ArcOrientation.toDownRight;
import static it.beppi.arcpageindicator.ArcPageIndicator.ArcOrientation.toLeft;
import static it.beppi.arcpageindicator.ArcPageIndicator.ArcOrientation.toRight;
import static it.beppi.arcpageindicator.ArcPageIndicator.ArcOrientation.toUp;
import static it.beppi.arcpageindicator.ArcPageIndicator.ArcOrientation.toUpLeft;
import static it.beppi.arcpageindicator.ArcPageIndicator.ArcOrientation.toUpRight;
import static it.beppi.arcpageindicator.ArcPageIndicator.SpotShape.circle;
import static it.beppi.arcpageindicator.ArcPageIndicator.SpotShape.roundedSquare;
import static it.beppi.arcpageindicator.ArcPageIndicator.SpotShape.square;

/**
 * Created by Beppi on 30/12/2016.
 */

public class ArcPageIndicator extends View implements ViewPager.OnPageChangeListener {
    public enum AnimationType {
        none,
        color,
        slide,
        pinch,
        bump,
        rotate,
        rotate_pinch,
        necklace,
        necklace2,
        cover,
        fill,
        surround
    }
    public enum ArcOrientation {
        toUp,
        toDown,
        toRight,
        toLeft,
        toUpRight,
        toUpLeft,
        toDownRight,
        toDownLeft
    }
    public enum SpotShape {
        circle,
        roundedSquare,
        square
    }

    // attributes
    ViewPager viewPager = null;
    int viewPagerRes = 0;
    int spotsColor = 0x33cccccc;
    int selectedSpotColor = 0xcccccccc;
    int spotsRadius = 5;
    boolean intervalMeasureAngle = false;
    AnimationType animationType = color;
    ArcOrientation arcOrientation = toUp;
    boolean invertDirection = false;
    SpotShape spotShape = circle;

    boolean handEnabled = false;
    int handColor = spotsColor;
    int handWidth = 4;
    float handRelativeLength = 0.8f;

    // variables
    float verticalRadius, horizontalRadius, centerX, centerY;

    public ArcPageIndicator(Context context, View contentView) {
        super(context);
        init(context, null);
    }

    public ArcPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ArcPageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArcPageIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }


    void init(Context ctx, AttributeSet attrs) {
        this.ctx = ctx;
        loadAttributes(attrs);
        initTools();
    }

    /**
     * Load attributes from the xml and set them into variables
     * @param attrs
     */
    void loadAttributes(AttributeSet attrs) {
        if (attrs == null) return;

        TypedArray typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.ArcPageIndicator);

        viewPagerRes = typedArray.getResourceId(R.styleable.ArcPageIndicator_apiViewPager, 0);

        spotsColor = typedArray.getColor(R.styleable.ArcPageIndicator_apiSpotsColor, spotsColor);
        selectedSpotColor = typedArray.getColor(R.styleable.ArcPageIndicator_apiSelectedSpotColor, selectedSpotColor);
        spotsRadius = typedArray.getDimensionPixelSize(R.styleable.ArcPageIndicator_apiSpotsRadius, spotsRadius);

        invertDirection = typedArray.getBoolean(R.styleable.ArcPageIndicator_apiInvertDirection, invertDirection);

        handEnabled = typedArray.getBoolean(R.styleable.ArcPageIndicator_apiHandEnabled, handEnabled);
        handColor = typedArray.getColor(R.styleable.ArcPageIndicator_apiHandColor, handColor);
        handWidth = typedArray.getDimensionPixelSize(R.styleable.ArcPageIndicator_apiHandWidth, handWidth);
        handRelativeLength = typedArray.getFloat(R.styleable.ArcPageIndicator_apiHandRelativeLength, handRelativeLength);

        String ima = typedArray.getString(R.styleable.ArcPageIndicator_apiIntervalMeasure);
        if (ima != null) intervalMeasureAngle = ima.equals("0");

        String anim = typedArray.getString(R.styleable.ArcPageIndicator_apiAnimationType);
        if (anim != null) {
            if (anim.equals("0")) animationType = none;
            else if (anim.equals("1")) animationType = color;
            else if (anim.equals("2")) animationType = slide;
            else if (anim.equals("3")) animationType = pinch;
            else if (anim.equals("4")) animationType = bump;
            else if (anim.equals("5")) animationType = rotate;
            else if (anim.equals("6")) animationType = rotate_pinch;
            else if (anim.equals("7")) animationType = necklace;
            else if (anim.equals("8")) animationType = necklace2;
            else if (anim.equals("9")) animationType = cover;
            else if (anim.equals("10")) animationType = fill;
            else if (anim.equals("11")) animationType = surround;
        }

        String orie = typedArray.getString(R.styleable.ArcPageIndicator_apiArcOrientation);
        if (orie != null) {
            if (orie.equals("0"))       arcOrientation = toUp;
            else if (orie.equals("1"))  arcOrientation = toDown;
            else if (orie.equals("2"))  arcOrientation = toRight;
            else if (orie.equals("3"))  arcOrientation = toLeft;
            else if (orie.equals("4"))  arcOrientation = toUpRight;
            else if (orie.equals("5"))  arcOrientation = toUpLeft;
            else if (orie.equals("6"))  arcOrientation = toDownRight;
            else if (orie.equals("7"))  arcOrientation = toDownLeft;
        }

        String sha = typedArray.getString(R.styleable.ArcPageIndicator_apiSpotShape);
        if (sha != null) {
            if (sha.equals("0"))        spotShape = circle;
            else if (sha.equals("1"))   spotShape = roundedSquare;
            else if (sha.equals("2"))   spotShape = square;
        }

        typedArray.recycle();
    }

    /**
     * Look for the View Pager
     */
    void findViewPager() {
        if (viewPager != null) return;
        if (viewPagerRes == 0) return;

        Context ctx = getContext();
        if (ctx instanceof Activity) {
            Activity activity = (Activity) getContext();
            View view = activity.findViewById(viewPagerRes);  // FALLISCE

            if (view != null && view instanceof ViewPager) {
                configureViewPager((ViewPager) view);
            }
        }
    }
    void configureViewPager(ViewPager viewPager) {
        releaseViewPager();
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
        pagerAdapter = viewPager.getAdapter();
        currentPosition = viewPager.getCurrentItem();

    }

    public void releaseViewPager() {
        if (viewPager != null) {
            viewPager.removeOnPageChangeListener(this);
            viewPager = null;
        }
    }

    private Paint paint;
    private Context ctx;
    PagerAdapter pagerAdapter = null;

    /**
     * Initialize what can be initialized at the beginning
     */
    void initTools() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(1);
        paint.setStyle(FILL);
        paint.setAntiAlias(true);
    }

    // ***************** Overrides **

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        Resources r = Resources.getSystem();
        if(widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST){
            widthSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }

        if(heightMode == MeasureSpec.UNSPECIFIED || heightSize == MeasureSpec.AT_MOST){
            heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        findViewPager();

        calcDirection();
        calcRadiusAndCenter();
        prepareFormulaForCostantArcData();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        calcValues();
        paintSpotsAndHand(canvas);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        currentPosition = position;
        currentPositionOffset = positionOffset;
        correctedCurrentPositionOffset = currentDirection ? currentPositionOffset : -currentPositionOffset;
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        findViewPager();
    }

    //**** Colors, shapes and animations ***

    boolean currentDirection = true;
    void calcDirection() {
        if (       arcOrientation == toLeft
                || arcOrientation == toDown
//                || arcOrientation == toDownRight
                || arcOrientation == toDownLeft)
            currentDirection = false;
        else currentDirection = true;
        if (animationType == rotate || animationType == rotate_pinch || animationType == necklace || animationType == necklace2)
            currentDirection = !currentDirection;
        if (invertDirection) currentDirection = !currentDirection;
    }

    /**
     * Calculate radius and center of the portion of ellipse to be drawn, based on orientation and size of the window
     */
    void calcRadiusAndCenter() {
        final int width = getWidth();
        final int height = getHeight();

        // horizontal
        if (arcOrientation == toUp){
            verticalRadius = height - spotsRadius * 2;
            horizontalRadius = width / 2 - spotsRadius;
            centerX = width / 2;
            centerY = height - spotsRadius;
        }
        else if (arcOrientation == toDown) {
            verticalRadius = height - spotsRadius * 2;
            horizontalRadius = width / 2 - spotsRadius;
            centerX = width / 2;
            centerY = spotsRadius;
        }
        // vertical
        else if (arcOrientation == toRight)  {
            verticalRadius = height / 2 - spotsRadius;
            horizontalRadius = width - spotsRadius * 2;
            centerX = spotsRadius;
            centerY = height / 2;
        }
        else if (arcOrientation == toLeft)  {
            verticalRadius = height / 2 - spotsRadius;
            horizontalRadius = width - spotsRadius * 2;
            centerX = width - spotsRadius;
            centerY = height / 2;
        }
        // corners
        else if (arcOrientation == toUpRight) {
            verticalRadius = height - 2 * spotsRadius;
            horizontalRadius = width - 2 * spotsRadius;
            centerX = spotsRadius;
            centerY = height - spotsRadius;
        }
        else if (arcOrientation == toUpLeft) {
            verticalRadius = height - 2 * spotsRadius;
            horizontalRadius = width - 2 * spotsRadius;
            centerX = width - spotsRadius;
            centerY = height - spotsRadius;
        }
        else if (arcOrientation == toDownRight) {
            verticalRadius = height - 2 * spotsRadius;
            horizontalRadius = width - 2 * spotsRadius;
            centerX = spotsRadius;
            centerY = spotsRadius;
        }
        else if (arcOrientation == toDownLeft) {
            verticalRadius = height - 2 * spotsRadius;
            horizontalRadius = width - 2 * spotsRadius;
            centerX = width - spotsRadius;
            centerY = spotsRadius;
        }
    }

    int calcSelectedSpotColor() {
        if (animationType == color) return proportionalScaleColor(selectedSpotColor, spotsColor, currentPositionOffset);
        if (animationType == slide || animationType == pinch || animationType == bump) return (currentPositionOffset == 0 ? 0 : spotsColor);
        if (animationType == surround) return spotsColor;
        return selectedSpotColor;
    }

    int calcCurrentSpotsColor() {
        if (animationType == color) return proportionalScaleColor(spotsColor, selectedSpotColor, currentPositionOffset);
        return spotsColor;
    }

    /**
     * Paint the spot number n with that color!
     * @param canvas
     * @param n
     * @param color
     */
    void paintSpot(Canvas canvas, int n, float positionOffset, int color, float radius, boolean stroke) {
        paint.setStyle(stroke ? STROKE : FILL);
        paint.setColor(color);
        double angle = calcAngle(n, positionOffset);
        double x = centerX + horizontalRadius * Math.sin(angle);
        double y = centerY - verticalRadius * Math.cos(angle);
        if (spotShape == circle)
            canvas.drawCircle((float)x, (float)y, radius, paint);
        else if (spotShape == roundedSquare) {
            float roundRadius = 2f * radius / 3f;
            canvas.drawRoundRect(new RectF((float) x - radius, (float) y - radius, (float) x + radius, (float) y + radius), roundRadius, roundRadius, paint);
        }
        else if (spotShape == square)
            canvas.drawRect(new RectF((float)x-radius, (float)y-radius, (float)x+radius, (float)y+radius), paint);
    }
    void paintHand(Canvas canvas, int n, float positionOffset) {
        paint.setColor(handColor);
        paint.setStrokeWidth(handWidth);
        double angle = calcAngle(n, positionOffset);
        double x = centerX + horizontalRadius * Math.sin(angle) * handRelativeLength;
        double y = centerY - verticalRadius * Math.cos(angle) * handRelativeLength;
        canvas.drawLine(centerX, centerY, (float)x, (float)y, paint);
    }

    /**
     * When rotating one extra spot must be added
     * @param canvas
     */
    void paintLeftMostSpot(Canvas canvas) {
        if (animationType != rotate && animationType != rotate_pinch && animationType != necklace && animationType != necklace2) return;
        if (currentPositionOffset == 0) return;
        paintSpot(canvas, -1, correctedCurrentPositionOffset, spotsColor, (float)calcSpotsRadius(0), false);
    }

    double calcSpotsRadius(int n) {
        if (animationType == rotate_pinch) return calcPinchRadius();
        else if (animationType == surround) return calcSurroundRadius();
        else if (animationType == necklace) return calcNecklaceRadius(n);
        else if (animationType == necklace2) return calcNecklace2Radius(n);
        else return spotsRadius;
    }
    double calcSelectedSpotRadius() {
        if (animationType == surround) return calcSurroundRadius();
        else if (animationType == necklace) return calcNecklaceRadius(currentPosition);
        else return spotsRadius;
    }

    /**
     * certain animations need an additional selected spot to be moving over the normal spots
     * @param canvas
     */
    void paintMovingSpot(Canvas canvas) {
        if (animationType == pinch)
            paintSpot(canvas, currentPosition, correctedCurrentPositionOffset, selectedSpotColor, (float) calcPinchRadius(), false);
        else if (animationType == bump)
            paintSpot(canvas, currentPosition, correctedCurrentPositionOffset, selectedSpotColor, (float)calcBumpRadius(), false);
        else if (animationType == slide)
            paintSpot(canvas, currentPosition, correctedCurrentPositionOffset, selectedSpotColor, spotsRadius, false);
        else if (animationType == surround)
            paintSpot(canvas, currentPosition, correctedCurrentPositionOffset, selectedSpotColor, spotsRadius, true);
    }

    /**
     * Specific for animations Cover && Fill, the selected spot reduces its radius and increases back
     * @param canvas
     */
    void paintFillSpot(Canvas canvas) {
        if (animationType != cover && animationType != fill) return;
        paint.setStyle(FILL);
        if (currentPositionOffset < 0.5)
            paintSpot(canvas, currentPosition, 0, selectedSpotColor, (float) calcFillRadius(), false);
        else
            paintSpot(canvas, currentPosition+1, 0, selectedSpotColor, (float) calcFillRadius(), false);
    }

    /**
     * used for Pinch animation
     * @return
     */
    double calcPinchRadius() {
        double radius;
        if (currentPositionOffset == 0) radius = spotsRadius;
        else if (currentPositionOffset < 0.5)
            radius = mapValueFromRangeToRange(currentPositionOffset, 0, 0.5, spotsRadius, 2*spotsRadius/3);
        else
            radius = mapValueFromRangeToRange(currentPositionOffset, 0.5, 1, 2*spotsRadius/3, spotsRadius);
        return radius;
    }

    /**
     * used for Bump animation
     * @return
     */
    double calcBumpRadius() {
        double radius;
        if (currentPositionOffset == 0) radius = spotsRadius;
        else if (currentPositionOffset < 0.5)
            radius = mapValueFromRangeToRange(currentPositionOffset, 0, 0.5, spotsRadius, 3*spotsRadius/2);
        else
            radius = mapValueFromRangeToRange(currentPositionOffset, 0.5, 1, 3*spotsRadius/2, spotsRadius);
        return radius;
    }

    /**
     * Radius of the smaller spots for Surround animation
     * @return
     */
    double calcSurroundRadius() {
        return 0.75d * spotsRadius;
    }

    /**
     * used to change radius of the spots with Necklace animation
     * @param n
     * @return
     */
    double calcNecklaceRadius(int n) {
        double pos = n + currentPositionOffset;
        double p = (pages-1d)/2;
        if (pos < p)
            return mapValueFromRangeToRange(pos, 0, p, spotsRadius/3, spotsRadius);
        else
            return mapValueFromRangeToRange(pos, p, pages-1, spotsRadius, spotsRadius/3);
    }

    /**
     * used to change radius of the spots with Necklace2 animation
     * @param n
     * @return
     */
    double calcNecklace2Radius(int n) {
        double p = Math.abs(n - currentPosition);
        return mapValueFromRangeToRange(p, pages-1, 0, spotsRadius/4, spotsRadius);
    }

    /**
     * used for Cover && Fill animations
     */
    double calcFillRadius() {
        double radius;
        if (currentPositionOffset == 0) radius = spotsRadius;
        else if (currentPositionOffset < 0.5)
            radius = mapValueFromRangeToRange(currentPositionOffset, 0, 0.5, spotsRadius, 0);
        else
            radius = mapValueFromRangeToRange(currentPositionOffset, 0.5, 1, 0, spotsRadius);
        return radius;
    }

    /**
     * calc offset when all spots must be rotated for the animation
     * @return
     */
    float calcAllSpotsOffset() {
        if (animationType != rotate && animationType != rotate_pinch && animationType != necklace && animationType != necklace2) return 0;
        return correctedCurrentPositionOffset;
    }

    /**
     * paint all spots, considering animations and all the rest
     * @param canvas
     */
    void paintSpotsAndHand(Canvas canvas) {
        if (animationType == fill) paint.setStyle(Paint.Style.STROKE);
        else paint.setStyle(FILL);

        for (int w=0; w<pages; w++) {
            if (w != currentPosition && w != currentPosition+1)
                paintSpot(canvas, w, calcAllSpotsOffset(), spotsColor, (float)calcSpotsRadius(w), animationType == fill);
        }

        paintLeftMostSpot(canvas);
        paintTheHand(canvas);


        if (animationType == color) {
            if (currentPositionOffset < 0.5) {
                paintSpot(canvas, currentPosition + 1, calcAllSpotsOffset(), calcCurrentSpotsColor(), (float) calcSpotsRadius(currentPosition + 1), false);
                paintSpot(canvas, currentPosition, calcAllSpotsOffset(), calcSelectedSpotColor(), spotsRadius, false);  // the selected spot is painted as last one to be over the others
            } else {
                paintSpot(canvas, currentPosition, calcAllSpotsOffset(), calcSelectedSpotColor(), spotsRadius, false);  // the selected spot is painted as last one to be over the others
                paintSpot(canvas, currentPosition + 1, calcAllSpotsOffset(), calcCurrentSpotsColor(), (float) calcSpotsRadius(currentPosition), false);
            }
        }
        else if (animationType == cover) {
            paintSpot(canvas, currentPosition + 1, calcAllSpotsOffset(), calcCurrentSpotsColor(), spotsRadius, false);
            paintSpot(canvas, currentPosition, calcAllSpotsOffset(), calcCurrentSpotsColor(), spotsRadius, false);  // the selected spot is painted as last one to be over the others
        }
        else if (animationType == fill) {
            paintSpot(canvas, currentPosition + 1, calcAllSpotsOffset(), calcCurrentSpotsColor(), spotsRadius, true);
            paintSpot(canvas, currentPosition, calcAllSpotsOffset(), calcCurrentSpotsColor(), spotsRadius, true);  // the selected spot is painted as last one to be over the others
        }
        else {
            paintSpot(canvas, currentPosition + 1, calcAllSpotsOffset(), calcCurrentSpotsColor(), (float) calcSpotsRadius(currentPosition + 1), false);
            paintSpot(canvas, currentPosition, calcAllSpotsOffset(), calcSelectedSpotColor(), (float)calcSelectedSpotRadius(), false);  // the selected spot is painted as last one to be over the others
        }
        paintMovingSpot(canvas);
        paintFillSpot(canvas);
    }

    /**
     * Calculate the value that stands linearly between From and To in a proportion between 0 (From) and 1 (To)
     * @param valueFrom
     * @param valueTo
     * @param proportion01
     * @return
     */
    int proportionalScaleValue(int valueFrom, int valueTo, float proportion01) {
        return ((int)(valueFrom * (1-proportion01) + valueTo * proportion01));
    }

    /**
     * Map a value within a given range to another range.
     * @param value the value to map
     * @param fromLow the low end of the range the value is within
     * @param fromHigh the high end of the range the value is within
     * @param toLow the low end of the range to map to
     * @param toHigh the high end of the range to map to
     * @return the mapped value
     */
    public static double mapValueFromRangeToRange(
            double value,
            double fromLow,
            double fromHigh,
            double toLow,
            double toHigh) {
        double fromRangeSize = fromHigh - fromLow;
        double toRangeSize = toHigh - toLow;
        double valueScale = (value - fromLow) / fromRangeSize;
        return toLow + (valueScale * toRangeSize);
    }


    /**
     * Calculate the color that stands linearly between From and To in a proportion between 0 (From) and 1 (To)
     * @param colorFrom
     * @param colorTo
     * @param proportion01
     * @return
     */
    int proportionalScaleColor(int colorFrom, int colorTo, float proportion01) {
        return new Color().argb(
                proportionalScaleValue(Color.alpha(colorFrom), Color.alpha(colorTo), proportion01),
                proportionalScaleValue(Color.red(colorFrom), Color.red(colorTo), proportion01),
                proportionalScaleValue(Color.green(colorFrom), Color.green(colorTo), proportion01),
                proportionalScaleValue(Color.blue(colorFrom), Color.blue(colorTo), proportion01)
        );
    }


    void paintTheHand(Canvas canvas) {
        if (!handEnabled) return;
        paintHand(canvas, currentPosition, correctedCurrentPositionOffset);
    }



    // *************** ELLIPSES (and every other maths) MANAGEMENT ********************************

    int pages;
    int currentPosition;
    float currentPositionOffset = 0;
    float correctedCurrentPositionOffset = 0;
    double itemAngle;
    final static double rotationConstant = -Math.PI/2;
    double arcRate;  // 2 for half ellipse, 4 for a quarter of ellipse and so on
    double rotationConstant2;  // used to rotate an ellipse
    double arcformula_e2, arcformula_factor_1, arcformula_factor_2, arcformula_factor_3; // used in the calculations for the constant arc distribution

    /**
     * Calc all values needed to draw, runtime
     */
    void calcValues() {
//        currentPosition = viewPager.getCurrentItem();
        if (arcOrientation == toUp) { rotationConstant2 = 0; arcRate = 2; }
        else if (arcOrientation == toDown) { rotationConstant2 = Math.PI; arcRate = 2; }

        else if (arcOrientation == toRight) { rotationConstant2 = Math.PI/2; arcRate = 2; }
        else if (arcOrientation == toLeft) { rotationConstant2 = -Math.PI/2; arcRate = 2; }

        else if (arcOrientation == toUpRight) { rotationConstant2 = Math.PI/2; arcRate = 4; }
        else if (arcOrientation == toUpLeft) { rotationConstant2 = 0; arcRate = 4; }
        else if (arcOrientation == toDownRight) { rotationConstant2 = Math.PI; arcRate = 4; }
        else if (arcOrientation == toDownLeft) { rotationConstant2 = 3*Math.PI/2; arcRate = 4; }

        if (pagerAdapter != null) {
            pages = pagerAdapter.getCount();
            if (pages > 1)
                itemAngle = Math.PI * 2 / ((pages - 1) * arcRate);
            else itemAngle = 0;
        }
        else pages = 3;

    }

    /**
     * check if position must be inverted to keep the browsing direction consistent (top->down and left->right)
     * apply also the invertDirection attribute
     * @param position
     * @return inverted or normal position
     */
    int invertPosition(int position) {
        if (!currentDirection)
            return pages - 1 - position;
        else return position;
    }

    double calcAngle(int position, float positionOffset) {
        position = invertPosition(position);
        if (intervalMeasureAngle)
            return itemAngle * position + rotationConstant + rotationConstant2;    // constant angle
        else
            return calcAngleConstantArc(position, positionOffset);
    }

    /**
     *
     * @param position position number
     * @return approximate angle from the center to the point on the half-ellipses in the desired direction
     * accordingly to the formula <a href="http://math.stackexchange.com/questions/2093569/points-on-an-ellipse#2093586">here described</a>
     */
    double calcAngleConstantArc(int position, float positionOffset) {
        double t, angle;

        t = itemAngle * (position + positionOffset) + rotationConstant;    // calculates horizontals with rotationConstant and with rotationConstant2 rotates them to vertical
        angle = t
                - arcformula_factor_1 * Math.sin(t * 2)
                + arcformula_factor_2 * Math.sin(t * 4)
                + arcformula_factor_3 * Math.sin(t * 6)
                + rotationConstant2;
        ;
        return angle;
    }

    /**
     * Calc all values that are needed to draw, and that don't depend from position.
     * Recall this when layout or the number of values change
     */
    void prepareFormulaForCostantArcData() {
        if (horizontalRadius < verticalRadius)
            arcformula_e2 = 1 - (horizontalRadius * horizontalRadius) / (verticalRadius * verticalRadius);  // inverts axis for verticals
        else
            arcformula_e2 = 1 - (verticalRadius * verticalRadius) / (horizontalRadius * horizontalRadius);
        arcformula_factor_1 = arcformula_e2/8 + arcformula_e2*arcformula_e2/16 + 71*arcformula_e2*arcformula_e2*arcformula_e2/2048;
        arcformula_factor_2 = 5*arcformula_e2*arcformula_e2/256 + 5*arcformula_e2*arcformula_e2*arcformula_e2/256;
        arcformula_factor_3 = 29*arcformula_e2*arcformula_e2*arcformula_e2/6144;
    }


    // ************* Getters and setters ********************************************************


    public int getViewPagerRes() {
        return viewPagerRes;
    }

    public void setViewPagerRes(int viewPagerRes) {
        this.viewPagerRes = viewPagerRes;
        invalidate();
    }

    public void setViewPager(ViewPager viewPager) {   // useful when the view pager is created dynamically and there is no res
        configureViewPager(viewPager);
        invalidate();
    }

    public int getSpotsColor() {
        return spotsColor;
    }

    public void setSpotsColor(int spotsColor) {
        this.spotsColor = spotsColor;
        invalidate();
    }

    public int getSelectedSpotColor() {
        return selectedSpotColor;
    }

    public void setSelectedSpotColor(int selectedSpotColor) {
        this.selectedSpotColor = selectedSpotColor;
        invalidate();
    }

    public int getSpotsRadius() {
        return spotsRadius;
    }

    public void setSpotsRadius(int spotsRadius) {
        this.spotsRadius = spotsRadius;
        invalidate();
    }

    public boolean isIntervalMeasureAngle() {
        return intervalMeasureAngle;
    }

    public void setIntervalMeasureAngle(boolean intervalMeasureAngle) {
        this.intervalMeasureAngle = intervalMeasureAngle;
        invalidate();
    }

    public AnimationType getAnimationType() {
        return animationType;
    }

    public void setAnimationType(AnimationType animationType) {
        this.animationType = animationType;
        calcDirection();
        invalidate();
    }

    public ArcOrientation getArcOrientation() {
        return arcOrientation;
    }

    public void setArcOrientation(ArcOrientation arcOrientation) {
        this.arcOrientation = arcOrientation;
        invalidate();
    }

    public boolean isInvertDirection() {
        return invertDirection;
    }

    public void setInvertDirection(boolean invertDirection) {
        this.invertDirection = invertDirection;
        calcDirection();
        invalidate();
    }

    public boolean isHandEnabled() {
        return handEnabled;
    }

    public void setHandEnabled(boolean handEnabled) {
        this.handEnabled = handEnabled;
        invalidate();
    }

    public int getHandColor() {
        return handColor;
    }

    public void setHandColor(int handColor) {
        this.handColor = handColor;
        invalidate();
    }

    public int getHandWidth() {
        return handWidth;
    }

    public void setHandWidth(int handWidth) {
        this.handWidth = handWidth;
        invalidate();
    }

    public float getHandRelativeLength() {
        return handRelativeLength;
    }

    public void setHandRelativeLength(float handRelativeLength) {
        this.handRelativeLength = handRelativeLength;
        invalidate();
    }

    public SpotShape getSpotShape() {
        return spotShape;
    }

    public void setSpotShape(SpotShape spotShape) {
        this.spotShape = spotShape;
        invalidate();
    }
}
