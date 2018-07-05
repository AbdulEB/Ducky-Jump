package ca.brocku.cosc.duckyjump.GameObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import ca.brocku.cosc.duckyjump.GameObjects.SkinManager.Skin;

/**
 * Contains the duck logic and animation.
 */
public class Duck {

    // Default physics values.
    private static float DEFAULT_DUCK_X             = 0f,       DEFAULT_DUCK_Y              = 0f;
    private static float DEFAULT_VELOCITY_X         = 10f,      DEFAULT_VELOCITY_Y          = 0f;
    private static float DEFAULT_JUMP_VELOCITY_X    = 0f,       DEFAULT_JUMP_VELOCITY_Y     = -20f;
    private static float DEFAULT_GRAVITY_X          = 0f,       DEFAULT_GRAVITY_Y           = 1.5f;
    private static float DEFAULT_MOVEMENT_SPEED_X   = 0.2f,     DEFAULT_MOVEMENT_SPEED_Y    = 0f;

    // The number of game ticks to keep the jump animation up for.
    private static final int JUMP_ANIMATION_COUNTDOWN = 8;
    private static final float SCALE = 0.3f; // Duck size.

    // Context is required to load the images of the duck.
    private Context context;

    private Bitmap duckLeft, duckRight, duckJumpLeft, duckJumpRight; // All the duck images.
    private Vector duckPosition, duckVelocity, jumpVelocity, gravity, movementSpeed;
    private Bubbles bubbles; // The trail that follows the duck when jumping (particle effect).
    private Utility.Direction direction; // Movement direction.
    private int width, height; // Includes the beak, hair and tail.
    private int sWidth, sHeight; // Screen dimensions.
    private boolean alive;
    private int jumpAnimationCountDown = 0;

    /**
     * Construct Duck.
     * @param context   Required to load to the images.
     * @param skin      The color of the duck.
     */
    public Duck(Context context, Skin skin) {
        this.context = context;
        bubbles = new Bubbles();
        setSkin(skin);
        reset();
        width = duckLeft.getWidth();
        height = duckLeft.getHeight();
    }

    /**
     * This needs to be set when the engine has access to the canvas.
     * @param width     Width of the canvas.
     * @param height    Height of the canvas.
     */
    public void setDimensions(int width, int height) {
        sWidth = width;
        sHeight = height;
    }

    /**
     * Reset the location & physics. To be used on a game restart.
     */
    public void reset(){
        duckPosition = new Vector(DEFAULT_DUCK_X, DEFAULT_DUCK_Y);
        gravity = new Vector(DEFAULT_GRAVITY_X, DEFAULT_GRAVITY_Y);
        jumpVelocity = new Vector(DEFAULT_JUMP_VELOCITY_X, DEFAULT_JUMP_VELOCITY_Y);
        duckPosition = new Vector(DEFAULT_DUCK_X, DEFAULT_DUCK_Y);
        duckVelocity = new Vector(DEFAULT_VELOCITY_X, DEFAULT_VELOCITY_Y);
        duckVelocity.limit(20);
        movementSpeed = new Vector(DEFAULT_MOVEMENT_SPEED_X, DEFAULT_MOVEMENT_SPEED_Y);
        direction = Utility.Direction.RIGHT;
        alive = true;
        jumpAnimationCountDown = 0;
    }

    /**
     * Set the color of the duck.
     * @param skin  The color of the duck.
     */
    public void setSkin(Skin skin) {
        int color = SkinManager.getColor(skin);
        int image1 = SkinManager.getBirdImageID(skin);
        int image2 = SkinManager.getBirdJumpImageID(skin);
        bubbles.changeColor(color);
        loadImage(image1, image2);
    }

    /**
     * Update physics calculations and duck animations.
     */
    public void update() {
        if (jumpAnimationCountDown > 0)
            jumpAnimationCountDown--;
        addForces();
        bubbles.update(getCenterX(), getCenterY());
        accountForWallHit();
    }

    /**
     * Move the duck according to physics calculations.
     */
    private void addForces() {
        duckVelocity.add(gravity);
        duckVelocity.add(movementSpeed);
        duckPosition.add(duckVelocity);
    }

    /**
     * If the duck hits a wall, flip it.
     */
    private void accountForWallHit() {
        int xPos = 0;
        if (direction == Utility.Direction.RIGHT) {
            xPos = sWidth - getWidth();
        }

        if ((xPos == 0 && duckPosition.getX() < xPos) || (xPos != 0 && duckPosition.getX() > xPos)) {
            movementSpeed.mul(-1);
            duckVelocity.setXY(duckVelocity.getX() * -1, duckVelocity.getY());
            duckPosition.setXY(xPos, duckPosition.getY());
            changeDirection();
        }
    }

    /**
     * Draw the duck & related effects to the screen.
     * @param canvas    The screen to draw to.
     * @param paint     The tool to paint with.
     */
    public void draw(Canvas canvas, Paint paint) {
        bubbles.draw(canvas, paint);
        Bitmap duck = currentImage();
        canvas.drawBitmap(duck, duckPosition.getX(), duckPosition.getY(), paint);
    }

    /**
     * Get the current image of the duck that should be drawn to the screen. This will account for
     * the direction that the duck is facing, as well as if it is in the middle of a jump.
     * @return  The current image of the duck.
     */
    private Bitmap currentImage() {
        if (direction == Utility.Direction.LEFT && jumpAnimationCountDown > 0)
            return duckJumpLeft;
        else if (direction == Utility.Direction.LEFT)
            return duckLeft;
        else if (direction == Utility.Direction.RIGHT && jumpAnimationCountDown > 0)
            return duckJumpRight;
        else
            return duckRight;
    }

    /**
     * Load all four possible images of duck into local variables. Since there are only two
     * images in the drawables folder, they need to be flipped to create the other two that will
     * face the left direction.
     * @param duckImageID       The ID of the duck image in resources.
     * @param duckJumpImageID   The ID of the duck jump image in resources.
     */
    private void loadImage(int duckImageID, int duckJumpImageID) {
        Bitmap image = BitmapFactory.decodeResource(context.getResources(), duckImageID);
        int x = Utility.toScale(SCALE, image.getWidth());
        int y = Utility.toScale(SCALE, image.getHeight());
        duckRight = Bitmap.createScaledBitmap(image, x, y, false);
        image = Utility.FlipBitmapHorizontally(image);
        duckLeft = Bitmap.createScaledBitmap(image, x, y, false);
        image = BitmapFactory.decodeResource(context.getResources(), duckJumpImageID);
        duckJumpRight = Bitmap.createScaledBitmap(image, x, y, false);
        image = Utility.FlipBitmapHorizontally(image);
        duckJumpLeft = Bitmap.createScaledBitmap(image, x, y, false);
    }

    /**
     * Perform a jump. This does the physics calculations and creates a trail of bubbles.
     */
    public void jump() {
        jumpAnimationCountDown = JUMP_ANIMATION_COUNTDOWN;
        duckVelocity.setXY(duckVelocity.getX(), 0);
        duckVelocity.add(jumpVelocity);
        bubbles.start(getCenterX(), getCenterY());
    }

    /**
     * Get the direction that the duck is facing and moving towards.
     * @return  The direction that the duck is facing.
     */
    public Utility.Direction getDirection() {
        return direction;
    }

    /**
     * Check if the duck is alive.
     * @return  True if the duck is alive.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Kill the duck. To be used if the duck collides with a spike.
     */
    public void kill() {
        alive = false;
        bubbles.clear();
    }

    /**
     * Set a new position for the duck. To be used on a game restart.
     * @param x     The new x coordinate of the duck.
     * @param y     The new y coordinate of the duck.
     */
    public void setDuckPosition(float x, float y) {
        duckPosition.setXY(x, y);
    }

    /**
     * Get the width of the duck. This includes for the beak and the tail.
     * @return  The width of the duck.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of the duck. This includes the hair.
     * @return  The height of the duck.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the x coordinate of the top left location of the duck.
     * @return  The x coordinate of the duck.
     */
    public float getX() {
        return duckPosition.getX();
    }

    /**
     * Get the y coordinate of the top left location of the duck.
     * @return  The y coordinate of the duck.
     */
    public float getY() {
        return duckPosition.getY();
    }

    /**
     * Get the x coordinate of the center of the duck.
     * @return  The x coordinate of the center of the duck.
     */
    public int getCenterX() {
        return (int) duckPosition.getX() + (width / 2);
    }

    /**
     * Get the y coordinate of the center of the duck.
     * @return  The y coordinate of the center of the duck.
     */
    public int getCenterY() {
        return (int) duckPosition.getY() + (height / 2);
    }

    /**
     * Change the direction of the duck. To be used when the duck hits a wall.
     */
    private void changeDirection() {
        if (direction == Utility.Direction.LEFT)
            direction = Utility.Direction.RIGHT;
        else
            direction = Utility.Direction.LEFT;
    }

}
