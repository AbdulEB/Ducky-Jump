package ca.brocku.cosc.duckyjump;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

import ca.brocku.cosc.duckyjump.GameObjects.SkinManager.Skin;

/**
 * Responsible for the screen where the user can pick which duck they want to play with.
 */
public class SkinPicker extends AppCompatActivity {

    private Database db;

    private Skin circleMenuArray[] = {
            Skin.YELLOW,
            Skin.BLUE,
            Skin.ORANGE,
            Skin.PURPLE,
            Skin.RED,
            Skin.GREEN
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_picker);
        db = new Database(getBaseContext());

        final Handler setDelay = new Handler();
        CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circle_skin_menu);
        circleMenu.setMainMenu(Color.parseColor("#222222"), R.drawable.icon_settings, R.drawable.icon_settings)
                .addSubMenu(Color.parseColor("#222222"),  R.drawable.duck_yellow)
                .addSubMenu(Color.parseColor("#222222"),R.drawable.duck_blue)
                .addSubMenu(Color.parseColor("#222222"), R.drawable.duck_orange)
                .addSubMenu(Color.parseColor("#222222"), R.drawable.duck_purple)
                .addSubMenu(Color.parseColor("#222222"), R.drawable.duck_red)
                .addSubMenu(Color.parseColor("#222222"), R.drawable.duck_green)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    Runnable startDelay;
                    @Override
                    public void onMenuSelected(int index) {
                        final int i = index;
                        startDelay = new Runnable() {
                            @Override
                            public void run() {
                                setSkin(circleMenuArray[i].toString());
                            }
                        };
                        setDelay.postDelayed(startDelay, 700);
                    }

                });

    }

    /**
     * Save the skin to the database.
     * @param duck  The chosen skin.
     */
    public void setSkin(String duck) {
        db.updateSkin(duck);
        this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}
