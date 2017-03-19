package com.jackie.flash_buy;

import com.jackie.flash_buy.model.Item;
import com.jackie.flash_buy.views.SplashActivity;
import com.jackie.flash_buy.views.home.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest=Config.NONE, packageName = "com.jackie.flash_buy" )
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

//    @Before
//    public void setUp() throws Exception {
//
//    }

    @Test
    public void testActivity() throws Exception {
        SplashActivity activity = Robolectric.setupActivity(SplashActivity.class);
        assertNotNull(activity);
        assertEquals(activity.getTitle(), "SplashActivity");

    }


}
