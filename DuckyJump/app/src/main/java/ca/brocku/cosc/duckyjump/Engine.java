package ca.brocku.cosc.duckyjump;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

import ca.brocku.cosc.duckyjump.GameObjects.CollisionDetection;
import ca.brocku.cosc.duckyjump.GameObjects.Duck;
import ca.brocku.cosc.duckyjump.GameObjects.ScoreDrawer;
import ca.brocku.cosc.duckyjump.GameObjects.Spikes;
import ca.brocku.cosc.duckyjump.GameObjects.Utility;

/**
 * Brings all the components of the game together and is responsible for the main logic.
 *
 * There is one "catch". Some of the game objects (such as the duck and the spikes) need access
 * to the canvas size in order to function properly. Unfortunately, the canvas object is limited
 * to the onDraw(...) method and thus cannot be accessed outside of it. To solve this, a hacky
 * solution was done. These objects store the dimensions of the screen is a placeholder, until
 * the canvas is accessible in onDraw(...), then the dimensions are updated to the canvas. The
 * first block in the onDraw(...) method is responsible for this.
 */
public class Engine extends AppCompatImageView {

    // Drawing & interaction.
    private GestureDetectorCompat mDetector;
    private Paint paint;
    private int width, height;
    private Database db;

    // Game logic & objects.
    private boolean running, started; // The game.
    private Spikes spikes;
    private Duck duck;
    private Utility.Direction direction; // Of the duck.
    private int score;
    private ScoreDrawer scoreDrawer;

    /**
     * Instantiate the game objects and drawing tools.
     * @param context   The context.
     */
    public Engine(Context context) {
        super(context);

        // Drawing & interaction.
        mDetector = new GestureDetectorCompat(getContext(), new MyGestureListener());
        paint = new Paint();
        paint.setAntiAlias(true);
        setBackgroundColor(Color.DKGRAY);
        loadWindowDimensions(context);
        db = new Database(context);

        // Game logic & objects.
        running = false;
        started = false;
        duck = new Duck(context, db.getSkin());
        direction = duck.getDirection();
        score = 0;
        spikes = new Spikes(context);
        scoreDrawer = new ScoreDrawer(paint, width, height);
    }

    /**
     * Since the canvas cannot be accessed outside of onDraw(), this method is the temporary
     * placeholder for the width and height.
     * @param context   The context.
     */
    private void loadWindowDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }

    /**
     * Place duck in the center of the canvas.
     */
    private void spawnDuck() {
        duck.setDuckPosition(width/2-duck.getWidth()/2, height/2-duck.getHeight()/2);
    }

    /**
     * Check if the duck has bounced off a wall.
     * @param duck  The specified duck.
     * @return      True if the duck has bounces off a wall.
     */
    private boolean duckChangedDirection(Duck duck) {
        return this.direction != duck.getDirection();
    }

    /**
     * Updates the game objects and then draws them to the screen.
     * @param canvas    The canvas to draw to.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // Updating the temporary screen dimensions placeholder to the canvas dimensions.
        if (!started) {
            width = canvas.getWidth();
            height = canvas.getHeight();
            spikes.setDimensions(width, height);
            duck.setDimensions(width, height);
            started = true;
        }

        if (!running) {
            spawnDuck();
            spikes.spawn(direction, score);
        } else {
            update();
        }

        if (duck.isAlive())
            drawGame(canvas);
        else
            drawGameOverScreen(canvas);

        postInvalidateOnAnimation(); // REQUIRED
        super.onDraw(canvas);        // REQUIRED
    }

    /**
     * Update the locations and animations of the game objects.
     */
    private void update() {
        duck.update();
        spikes.update();
        scoreDrawer.update();

        if (CollisionDetection.detected(duck, spikes)) {
            duck.kill();
            spikes.clear();
            running = false;

            if (isNewHighScore()) {
                db.saveScore(Integer.toString(score));
                scoreDrawer.newHighScore();
            }

            return;
        }

        if (duckChangedDirection(duck)) {
            direction = duck.getDirection();
            score++;
            spikes.spawn(direction, score);
        }

    }

    private boolean isNewHighScore() {
        return score > db.getHighScore();
    }

    /**
     * Draw the game to the screen.
     * @param canvas    The canvas to draw on.
     */
    private void drawGame(Canvas canvas) {
        scoreDrawer.drawScore(canvas, paint, score);
        duck.draw(canvas, paint);
        spikes.draw(canvas, paint, direction);
    }

    /**
     * Draw the game over screen.
     * @param canvas    The canvas to draw on.
     */
    private void drawGameOverScreen(Canvas canvas) {
        scoreDrawer.drawGameOverScreen(canvas, paint, score);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * Pressing down on the screen cases the duck to jump and it also restarts the game if the
     * duck is dead.
     */
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            if (duck.isAlive()) {
                if (!running) {
                    running = true;
                }
                duck.jump();
            } else {
                // To restart, the user must click the top of the screen. This prevents users from
                // accidentally restarting the game (especially if they are spam clicking).
                if (event.getY() < height / 2) {
                    scoreDrawer.reset();
                    spikes.clear();
                    direction = Utility.Direction.RIGHT;
                    duck.reset();
                    score = 0;
                    scoreDrawer.reset();
                    spawnDuck();
                }
            }
            return true;
        }

    }

}
