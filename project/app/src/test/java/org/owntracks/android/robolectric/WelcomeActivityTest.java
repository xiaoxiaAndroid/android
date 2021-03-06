package org.owntracks.android.robolectric;

import android.Manifest;
import android.content.Intent;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.owntracks.android.R;
import org.owntracks.android.ui.map.MapActivity;
import org.owntracks.android.ui.welcome.WelcomeActivity;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.gms.common.ShadowGoogleApiAvailability;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;
import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.O_MR1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.shadows.ShadowView.clickOn;


@RunWith(RobolectricTestRunner.class)
@Config(minSdk = LOLLIPOP, maxSdk = O_MR1, shadows = {ShadowViewPager.class, ShadowGoogleApiAvailability.class})
public class WelcomeActivityTest {
    @Spy
    private WelcomeActivity welcomeActivity;

    private ShadowApplication application;

    @Before
    public void setup() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
                = Shadow.extract(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);

        ActivityController<WelcomeActivity> welcomeActivityActivityController = Robolectric.buildActivity(WelcomeActivity.class);


        welcomeActivity = welcomeActivityActivityController.get();
        application = Shadows.shadowOf(welcomeActivity.getApplication());
        application.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION);
        assertNotNull(welcomeActivity);
        welcomeActivityActivityController.setup();

    }

    @Test
    @Config(minSdk = M)
    public void DoneButtonShouldStartMapActivityOnVersionWithBackgroundRestriction() {
        assertEquals(View.VISIBLE, welcomeActivity.findViewById(R.id.btn_next).getVisibility());
        clickOn(welcomeActivity.findViewById(R.id.btn_next));

        assertEquals(View.VISIBLE, welcomeActivity.findViewById(R.id.btn_next).getVisibility());
        clickOn(welcomeActivity.findViewById(R.id.btn_next));

        assertEquals(View.VISIBLE, welcomeActivity.findViewById(R.id.done).getVisibility());
        assertTrue(welcomeActivity.findViewById(R.id.done).isEnabled());
        clickOn(welcomeActivity.findViewById(R.id.done));
        Intent expectedIntent = new Intent(welcomeActivity, MapActivity.class);
        Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }

    @Test
    @Config(maxSdk = LOLLIPOP_MR1)
    public void DoneButtonShouldStartMapActivityOnVersionWithoutBackgroundRestriction() {
        assertEquals(View.VISIBLE, welcomeActivity.findViewById(R.id.btn_next).getVisibility());
        clickOn(welcomeActivity.findViewById(R.id.btn_next));

        assertEquals(View.VISIBLE, welcomeActivity.findViewById(R.id.done).getVisibility());
        assertTrue(welcomeActivity.findViewById(R.id.done).isEnabled());
        clickOn(welcomeActivity.findViewById(R.id.done));
        Intent expectedIntent = new Intent(welcomeActivity, MapActivity.class);
        Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }
}