package innovate.ebad.com.instantmessenger.Application;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import innovate.ebad.com.instantmessenger.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}