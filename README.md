# ArcPageIndicator
A fully customizable super-easy Page Indicator, with stunning animations and original graphics, for Android. Needs a very small screen, perfect when many pages need to be shown and reached in a small time.

<img src="gifs/c01.gif" height="100"><img src="gifs/c02.gif"><img src="gifs/c03.gif"><img src="gifs/c04.gif"><br>
<img src="gifs/c05.gif"><img src="gifs/c06.gif"><img src="gifs/c07.gif"><img src="gifs/c08.gif"><br>
<img src="gifs/c09.gif"><img src="gifs/c10.gif"><img src="gifs/c11.gif"><img src="gifs/c12.gif"><br>
<img src="gifs/c13.gif"><img src="gifs/c14.gif"><br>



* Out-of-the-box working indicator
* Fully customizable and styleable
* Can be programmatically controlled

### Setup (Gradle)
In your project's build.gradle file:

    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
            ...
        }
    }
    
In your Application's or Module's build.gradle file:

    dependencies {
        ...
        compile 'com.github.BeppiMenozzi:ArcPageIndicator:1.0.0'
        ...
    }
    
### Minimal usage
Layout for bottom 180° ellipse:

    ...
    xmlns:app="http://schemas.android.com/apk/res-auto"
    ...
    <it.beppi.arcpageindicator.ArcPageIndicator
        android:id="@+id/arc_pi"
        android:layout_width="400dp"
        android:layout_height="120dp"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        app:apiArcOrientation="toUp"
        app:apiViewPager="@id/view_pager"
        />

Layout for upper-left 90° ellipse:

    ...
    xmlns:app="http://schemas.android.com/apk/res-auto"
    ...
    <it.beppi.arcpageindicator.ArcPageIndicator
        android:id="@+id/arc_pi"
        android:layout_width="120dp"
        android:layout_height="120dp"
        android:layout_alignParentTop="true"
        android:layout_alignParentLeft="true"
        app:apiArcOrientation="toDownRight"
        app:apiViewPager="@id/view_pager"
        />

### Attributes description
List of attributes with description:
<table>
<tr><th colspan="2">General</th></tr>
<tr><td><b>apiViewPager</b></td><td>The ViewPager associated to the Indicator</td></tr>
<tr><td><b>kDefaultState</b></td><td>The starting state of the knob.</td></tr>
<tr><td><b>kAnimation</b></td><td>Enable / disable indicator's animation.</td></tr>
<tr><td><b>kAnimationSpeed</b></td><td>Parameter "speed" applied to the spring physical model for the indicator's animation.</td></tr>
<tr><td><b>kAnimationBounciness</b></td><td>Parameter "bounciness" applied to the spring physical model for the indicator's animation.</td></tr>
<tr><td><b>kEnabled</b></td><td>Enable / disable knob.</td></tr>
<tr><th colspan="2">Knob appearance</th></tr>
</table>
