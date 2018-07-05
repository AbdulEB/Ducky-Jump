package ca.brocku.cosc.duckyjump;;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

import ca.brocku.cosc.duckyjump.GameObjects.SkinManager;

import static ca.brocku.cosc.duckyjump.GameObjects.SkinManager.Skin;

/**
 * The first activity the pops up in the app. This contains the buttons for the other menus.
 */
public class MainActivity extends AppCompatActivity {

    private String circleMenuArray[] = {"Play", "Scoreboard", "Options"};
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new Database(getBaseContext());

        final Handler setDelay = new Handler();

        db = new Database(getBaseContext());
        Skin skin = db.getSkin();
        int id1 = SkinManager.getBirdImageID(skin);
        int id2 = SkinManager.getBirdJumpImageID(skin);
        int c = SkinManager.getColor(skin);
        String color = "#" + Integer.toHexString(c);

        final CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circle_main_menu);
        circleMenu.setMainMenu(Color.parseColor("#222222"), id1, id2)
                .addSubMenu(Color.parseColor(color), R.drawable.icon_play)
                .addSubMenu(Color.parseColor(color), R.drawable.icon_hiscores)
                .addSubMenu(Color.parseColor(color), R.drawable.icon_settings)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    Runnable startDelay;

                    @Override
                    public void onMenuSelected(int index) {
                        final int i = index;
                        startDelay = new Runnable() {
                            @Override
                            public void run() {
                                onClick(i);
                            }
                        };
                        setDelay.postDelayed(startDelay, 1100);
                    }
                });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    /**
     * Load another menu (such as settings, scoreboard or play the actual game).
     * @param index     The index of which button was pressed.
     */
    public void onClick(int index) {
        Intent i;
        switch (circleMenuArray[index]) {
            case "Play":
                i = new Intent(this, PlayGame.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case "Options":
                i = new Intent(this, SkinPicker.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case "Scoreboard":
                i = new Intent(this, Scoreboard.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }

}