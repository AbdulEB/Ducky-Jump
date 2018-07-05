package ca.brocku.cosc.duckyjump.GameObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.TreeMap;

import ca.brocku.cosc.duckyjump.R;
import ca.brocku.cosc.duckyjump.GameObjects.Utility.Direction;

/**
 * Responsible for generating and animating the spikes.
 */
public class Spikes {

    private static final float SCALE = 0.2f;

    // The spikes that come out from the sides are stored here. Only one side is stored at a time.
    private Boolean[] spikes;

    // This is required in order to load the images from resources.
    private Context context;

    private Bitmap bottomSpike, topSpike, leftSpike, rightSpike;
    private Direction direction = Direction.RIGHT; // Determines which wall to spawn spikes on.
    private int width = -1, height = -1; // This must be set when access to the canvas is possible.
    private int offset; // Spikes come out from the sides starting at a particular offset.

    // Maps the score to the number of spikes there should be.
    private static final TreeMap<Integer, Integer> difficultyMap = new TreeMap<>();
    static {
        difficultyMap.put(0, 0);
        difficultyMap.put(1, 2);
        difficultyMap.put(2, 3);
        difficultyMap.put(5, 4);
        difficultyMap.put(13, 5);
        difficultyMap.put(22, 6);
        difficultyMap.put(30, 7);
        difficultyMap.put(35, 8);
        difficultyMap.put(40, 9);
        difficultyMap.put(45, 10);
    }

    /**
     * Construct the spikes.
     * @param context   This is required to load the images from resources.
     */
    public Spikes(Context context) {
        this.context = context;
        spikes = new Boolean[0];
        loadImage();
    }

    /**
     * Remove the spikes from the sides (not from the top and bottom).
     */
    public void clear(){
        spawnSpikes(0);
    }

    /**
     * The number of spikes to spawn depends on the score. A higher score means more speaks.
     * @param score     The current score in the game.
     * @return          The number of spikes that should be spawn.
     */
    private int getNumberOfSpikesToSpawn(int score){
        if (difficultyMap.containsKey(score))
            return difficultyMap.get(score);
        else
            return difficultyMap.get(difficultyMap.floorKey(score));
    }

    /**
     * Spawn spikes at the left or right wall. The number of spikes depends on the score.
     * @param direction     The wall to spawn on.
     * @param score         The current score in the game.
     */
    public void spawn(Direction direction, int score) {
        this.direction = direction;
        int numOfSpikes = getNumberOfSpikesToSpawn(score);
        spawnSpikes(numOfSpikes);
        resetOffset();
    }

    /**
     * Spawn the left or right spikes.
     * @param numOfSpikes   The number of spikes to spawn.
     */
    private void spawnSpikes(int numOfSpikes) {
        if (spikes.length == 0) {
            spikes = new Boolean[getMaxPossibleSpikes(height)];
        }

        // Don't create more spikes than can fit & have at least space for one empty spot.
        if (numOfSpikes >= spikes.length)
            numOfSpikes = spikes.length - 1;

        for (int i = 0; i < numOfSpikes; i++) {
            spikes[i] = true;
        }
        for (int i = numOfSpikes; i < spikes.length; i++){
            spikes[i] = false;
        }

        Utility.Shuffle(spikes);
    }

    /**
     * When new spikes are spawned the offset needs to be reset. This is so that the spikes are
     * placed outside the boundaries of the screen and then move into the screen to perform the
     * spawn animation.
     */
    private void resetOffset() {
        offset = -leftSpike.getWidth() / 2;
    }

    /**
     * Update the spawn animation.
     */
    public void update() {
        if (offset < 0)
            offset += 5;
    }

    /**
     * Draw the spikes to the screen.
     * @param canvas        The screen to draw on.
     * @param paint         The tool to paint with.
     * @param direction     The wall that the spikes are on.
     */
    public void draw(Canvas canvas, Paint paint, Direction direction) {
        if (direction == Direction.RIGHT)
            drawRightSpikes(canvas, paint);
        else
            drawLeftSpikes(canvas, paint);

        drawTopSpikes(canvas, paint);
        drawBottomSpikes(canvas, paint);
    }

    /**
     * Set the dimensions of the canvas.
     * @param width     The width of the canvas.
     * @param height    The height of the canvas.
     */
    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Get the width of the screen. This is used by the collision detection algorithm.
     * @return  The width of the screen.
     */
    int getScreenWidth() {
        if (width == -1)
            throw new IllegalStateException("Cannot get the screen width before spawning.");
        return width;
    }

    /**
     * Get the height of the screen. This is used by the collision detection algorithm.
     * @return
     */
    int getScreenHeight() {
        if (height == - 1)
            throw new IllegalStateException("Cannot get the screen height before spawning.");
        return height;
    }

    /**
     * A spike is an isosceles triangle. The base is the measurement of the longest side.
     * @return  The base measurement in pixels.
     */
    public int getSpikeBase() {
        return bottomSpike.getWidth();
    }

    /**
     * A spike is an isosceles triangle. The height is the measurement between the center of the
     * two acute angles and the obtuse angle.
     * @return  The height measurement in pixels.
     */
    public int getSpikeHeight() {
        return bottomSpike.getHeight() / 2;
    }

    /**
     * Get the array containing where the spikes are.
     * @return  The spikes.
     */
    Boolean[] getSpawnLocations() {
        return spikes;
    }

    /**
     * Get the wall that the spikes are created on (left or right).
     * @return  The wall that the spikes are created on.
     */
    Direction getDirection() {
        return direction;
    }

    /**
     * Draw spikes on the right wall.
     * @param canvas        The screen to draw on.
     * @param paint         The tool to paint with.
     */
    private void drawRightSpikes(Canvas canvas, Paint paint) {
        for (int i = 0; i < spikes.length; i++) {
            if (!spikes[i])
                continue;
            int x = canvas.getWidth() - rightSpike.getWidth() - offset;
            int y = rightSpike.getHeight() + rightSpike.getHeight() * i;
            canvas.drawBitmap(rightSpike, x, y, paint);
        }
    }

    /**
     * Draw spikes on the left wall.
     * @param canvas        The screen to draw on.
     * @param paint         The tool to paint with.
     */
    private void drawLeftSpikes(Canvas canvas, Paint paint) {
        for (int i = 0; i < spikes.length; i++) {
            if (!spikes[i])
                continue;

            int x = 0 + offset;
            int y = leftSpike.getHeight() + leftSpike.getHeight() * i;
            canvas.drawBitmap(leftSpike, x, y, paint);
        }
    }

    /**
     * Draw spikes on the top.
     * @param canvas        The screen to draw on.
     * @param paint         The tool to paint with.
     */
    private void drawTopSpikes(Canvas canvas, Paint paint) {
        int start = bottomSpike.getWidth();
        int end = canvas.getWidth() + bottomSpike.getWidth();
        int increment = bottomSpike.getWidth();
        for (int i = start; i < end; i += increment) {
            int x = canvas.getWidth() - i;
            int y = canvas.getHeight() - bottomSpike.getHeight();
            canvas.drawBitmap(bottomSpike, x, y, paint);
        }
    }

    /**
     * Draw spikes on the bottom.
     * @param canvas        The screen to draw on.
     * @param paint         The tool to paint with.
     */
    private void drawBottomSpikes(Canvas canvas, Paint paint) {
        int start = topSpike.getWidth();
        int end = canvas.getWidth() + topSpike.getWidth();
        int increment = topSpike.getWidth();
        for (int i = start; i < end; i += increment) {
            int x = canvas.getWidth() - i;
            int y = 0;
            canvas.drawBitmap(topSpike, x, y, paint);
        }
    }

    /**
     * The maximum possible spikes that can be spawned on one side of the screen.
     * @param height    The height of the screen.
     * @return          The maximum number of spikes that can be spawned.
     */
    public int getMaxPossibleSpikes(int height) {
        Bitmap spike = rightSpike;
        // Do not draw a spike right at the top or right at the bottom.
        int trimmedHeight = height - spike.getHeight() * 2;
        return trimmedHeight / spike.getHeight();
    }

    /**
     * Load all the images of the spikes (4 in total: top, left, bottom, and right). These are all
     * the same image but rotated.
     */
    private void loadImage() {
        Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.spike);
        int x = Utility.toScale(SCALE, image.getWidth());
        int y = Utility.toScale(SCALE, image.getHeight());
        bottomSpike = Bitmap.createScaledBitmap(image, x, y, false);
        image = Utility.FlipBitmapVertically(image);
        topSpike = Bitmap.createScaledBitmap(image, x, y, false);
        image = Utility.RotateBitmap(image, -90);
        leftSpike = Bitmap.createScaledBitmap(image, x, y, false);
        image = Utility.FlipBitmapHorizontally(image);
        rightSpike = Bitmap.createScaledBitmap(image, x, y, false);
    }

}
