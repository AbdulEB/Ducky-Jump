package ca.brocku.cosc.duckyjump;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Responsible for starting the game.
 */
public class PlayGame extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Engine(this));
    }
}
