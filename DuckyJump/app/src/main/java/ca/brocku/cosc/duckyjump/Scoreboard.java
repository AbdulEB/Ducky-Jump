package ca.brocku.cosc.duckyjump;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for the scoreboard screen. In order for the database to save the user's score, the
 * user must beat the #1 highest score, otherwise the score is not saved.
 */
public class Scoreboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        showTopScores();
    }

    /**
     * Load the top scores from the database.
     */
    private void showTopScores() {
        Database db = new Database(getBaseContext());
        List<ArrayList<String>> entries = db.query(Database.DB_TABLE_SCORES);
        List<String> entry = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            entry.add("Score: " + entries.get(i).get(0) + " | Date: " + entries.get(i).get(1));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.score_text_item, entry);
        ListView listView = (ListView) findViewById(R.id.score_list_view);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }
}
