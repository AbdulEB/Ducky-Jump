package ca.brocku.cosc.duckyjump.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;

/**
 * Create a trail of particle effects when the duck has jumped. It makes things pretty.
 */
public class Bubbles {

    private static final int MAX_BUBBLES = 3;
    private static final int MAX_BUBBLE_SIZE = 30;
    private static final int INCREMENT = 1; // The amount that the bubbles shrink by.
    private static final int BUBBLE_DISTANCE = 5; // The spread of the bubbles.
    private static final int DEFAULT_COLOR = Color.rgb(253, 211, 1);

    ArrayList<Point> bubbles; // The locations of the bubbles.
    ArrayList<Integer> bubblesSize; // The diameter for each bubble in the bubble list.
    private int color;

    private int updateCounter = 0; // Used to increase distance between bubbles.
    private int queue = 0; // The number of bubbles left to create.

    /**
     * Construct bubbles.
     */
    public Bubbles() {
        bubbles = new ArrayList<>();
        bubblesSize = new ArrayList<>();
        changeColor(DEFAULT_COLOR);
    }

    /**
     * Clear bubbles.
     */
    public void clear() {
        bubbles = new ArrayList<>();
        bubblesSize = new ArrayList<>();
    }

    /**
     * Change the color of the bubbles.
     * @param color     The specified color to change it to.
     */
    public void changeColor(int color) {
        this.color = color;
    }

    /**
     * Begin the trail of bubbles. To be used when a jump is performed.
     * @param x     The x coordinate of the duck.
     * @param y     The y coordinate of the duck.
     */
    public void start(int x, int y) {
        if (bubbles.size() == 0)
            createBubble(x, y); // Create the first bubble, if first bubble does not exist.

        // Don't queue up too many bubbles if there is already a lot.
        if (queue >= MAX_BUBBLES)
            queue++;
        else
            queue += MAX_BUBBLES;
    }

    /**
     * Shrink the bubbles and create more of necessary (depending on the queue) to create a trail.
     * @param x     The x coordinate of the duck.
     * @param y     The y coordinate of the duck.
     */
    public void update(int x, int y) {
        shrinkBubbles();

        updateCounter++;
        if (updateCounter != BUBBLE_DISTANCE){
            return;
        }
        updateCounter = 0;

        if (queue != 0){
            bubbles.add(new Point(x, y));
            bubblesSize.add(MAX_BUBBLE_SIZE);
            queue--;
        }

    }

    /**
     * Draw the bubbles to the screen.
     * @param canvas    The screen to draw to.
     * @param paint     The tool to draw with.
     */
    public void draw(Canvas canvas, Paint paint) {
        final int MAX_VAL = 255;

        for (int i = 0; i < bubbles.size(); i++) {
            int x = bubbles.get(i).x + (bubblesSize.get(i) / 2);
            int y = bubbles.get(i).y + (bubblesSize.get(i)); // For aesthetic reasons I didn't '/2'.
            int size = bubblesSize.get(i);

            float alphaPercent = (float)size / (float)MAX_BUBBLE_SIZE;
            int alpha = (int)(alphaPercent * MAX_VAL);

            paint.setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
            canvas.drawCircle(x, y, size, paint);
        }

        paint.setColor(Color.argb(MAX_VAL, MAX_VAL, MAX_VAL, MAX_VAL)); // Reset alpha to 255.
    }

    /**
     * Create a bubble at the specified location. Usually this location is where the duck was last.
     * @param x     The x coordinate of the duck.
     * @param y     The y coordinate of the duck.
     */
    private void createBubble(int x, int y) {
        bubbles.add(new Point(x, y));
        bubblesSize.add(MAX_BUBBLE_SIZE);
    }

    /**
     * Go through each bubble and shrink it according to the increment.
     */
    private void shrinkBubbles() {
        for (int i = 0 ; i < bubbles.size(); i++){
            bubblesSize.set(i, bubblesSize.get(i)- INCREMENT);
            if(bubblesSize.get(i)<=0){
                removeBubbleAtIndex(i);
                i--;
            }
        }
    }

    /**
     * Remove bubbles that have shrunken to oblivion.
     * @param index     The index of the bubble in the list.
     */
    private void removeBubbleAtIndex(int index) {
        bubbles.remove(index);
        bubblesSize.remove(index);
    }

}
