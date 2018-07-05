package ca.brocku.cosc.duckyjump.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Responsible for drawing the score to the screen and all animations associated with the score
 * (such as the ring that lights up in the background when the user gets a point).
 */
public class ScoreDrawer {

    private static final int DEFAULT_BRIGHTNESS = 150; // Of the ring.
    private static final int BRIGHTNESS_DECREMENT = 5; // Of the ring.

    // These are all relative to the width so that they appear the same on all deviced.
    private static final float SCORE_FONT_SIZE_RATIO = 4.778f;
    private static final float DEAD_FONT_SIZE_RATIO = 8.0f;
    private static final float FINAL_SCORE_FONT_SIZE_RATIO = 3.6f;

    // Message to the user.
    private static final String line1 = "TO RETRY";
    private static final String line2 = "TAP UP HERE";
    private static final String line3 = "NEW";
    private static final String line4 = "HIGH SCORE";

    private static float scoreFontSize, deadFontSize, finalScoreFont;

    private static final float CIRCLE_SIZE_RATIO = 0.2f; // Relative to screen width.
    private static final float INNER_RING_RATIO = 0.90f; // Relative to outer ring.

    private int brightness = DEFAULT_BRIGHTNESS;
    private int currentScore = 0;
    private boolean newHighScore = false;

    /**
     * Construct score drawer.
     * @param paint     The tool to paint with.
     * @param width     The width of the screen.
     * @param height    The height of the screen.
     */
    public ScoreDrawer(Paint paint, int width, int height) {
        paint.setTextSize(scoreFontSize);
        paint.setTextAlign(Paint.Align.CENTER);

        scoreFontSize = (int)(width / SCORE_FONT_SIZE_RATIO);
        deadFontSize = (int)(width / DEAD_FONT_SIZE_RATIO);
        finalScoreFont = (int)(width / FINAL_SCORE_FONT_SIZE_RATIO);
    }

    /**
     * Update the animation of the ring.
     */
    public void update(){
        if (brightness >= DEFAULT_BRIGHTNESS)
            brightness -= BRIGHTNESS_DECREMENT;
    }

    /**
     * Reset the animation of the ring.
     */
    public void reset(){
        currentScore = 0;
        brightness = DEFAULT_BRIGHTNESS;
        newHighScore = false;
    }

    /**
     * Draw the score to the screen.
     * @param canvas    The screen to draw on.
     * @param paint     The tool to paint with.
     * @param score     The score of the latest game.
     */
    public void drawScore(Canvas canvas, Paint paint, int score){
        paint.setTextSize(scoreFontSize);
        final int MAX_COLOR = 255;

        if (score != currentScore) {
            brightness = MAX_COLOR;
            currentScore = score;
        }

        int x = canvas.getWidth() / 2;
        int y = canvas.getHeight() / 2;

        drawRing(canvas, paint, x, y);

        if (score == 0)
            return;

        // the y offset for the font.
        int ty = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;
        canvas.drawText(Integer.toString(currentScore), x, ty, paint);
    }

    /**
     * Draw the ring to the screen (the one that circles the score during the game and lights up).
     * @param canvas    The canvas to draw to.
     * @param paint     The tool to paint with.
     * @param x         The x coordinate of the ring.
     * @param y         The y coordinate of the ring.
     */
    private void drawRing(Canvas canvas, Paint paint, int x, int y){
        // Outer Ring.
        paint.setColor(Color.rgb(brightness, brightness, brightness));
        int outerCircle = (int)(canvas.getWidth() * CIRCLE_SIZE_RATIO);
        canvas.drawCircle((float)x, (float)y, outerCircle, paint);

        // Inner Ring.
        int innerCircle = (int)(outerCircle * INNER_RING_RATIO);
        paint.setColor(Color.DKGRAY);
        canvas.drawCircle((float)x, (float)y, innerCircle, paint);
        paint.setColor(Color.rgb(DEFAULT_BRIGHTNESS, DEFAULT_BRIGHTNESS, DEFAULT_BRIGHTNESS));
    }

    /**
     * If a new high score is set, turn the flag on so that the user can be notified of it.
     */
    public void newHighScore() {
        newHighScore = true;
    }

    /**
     * Draw the screen at the end of the game. This displays the score in a larger font and
     * notifies the user if they set a new high score.
     * @param canvas    The screen to draw to.
     * @param paint     The tool to paint with.
     * @param score     The score of the latest game.
     */
    public void drawGameOverScreen(Canvas canvas, Paint paint, int score) {
        drawGameOverScore(canvas, paint);
        drawGameOverHighScore(canvas, paint, score);
    }

    /**
     * Draw the score of the latest game in a larger font.
     * @param canvas    The screen to draw to.
     * @param paint     The tool to paint with.
     */
    private void drawGameOverScore(Canvas canvas, Paint paint) {
        int x = canvas.getWidth() / 2;
        int y = canvas.getHeight() / 2;

        paint.setColor(Color.LTGRAY);
        paint.setTextSize(deadFontSize);
        int ty1 = (int) (y - y / 2 - ((paint.descent() + paint.ascent()) / 2));
        canvas.drawText(line2, x, ty1, paint);
        canvas.drawText(line1, x, ty1 + paint.ascent(), paint);
    }

    /**
     * Notify the user if they set a new high score.
     * @param canvas    The screen to draw to.
     * @param paint     The tool to paint with.
     * @param score     The score of the latest game.
     */
    private void drawGameOverHighScore(Canvas canvas, Paint paint, int score) {
        int x = canvas.getWidth() / 2;
        int y = canvas.getHeight() / 2;

        paint.setTextSize(finalScoreFont);
        int ty2 = (int) (y - ((paint.descent() + paint.ascent()) / 2));
        canvas.drawText(Integer.toString(score), x, ty2, paint);
        int ty3 = (int) (y + (y/2) - ((paint.descent() + paint.ascent()) / 2));
        paint.setTextSize(deadFontSize);

        if (newHighScore) {
            canvas.drawText(line3, x, ty3 + paint.ascent(), paint);
            canvas.drawText(line4, x, ty3, paint);
        }
    }

}
