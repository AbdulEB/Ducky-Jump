package ca.brocku.cosc.duckyjump.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.util.Random;

/**
 * Helper class for bitmap manipulation, scaling and other common functions.
 */
public class Utility {

    /**
     * Static classes should not be instantiated.
     */
    private Utility(){}

    /**
     * Used for determining which direction the duck is facing and on which wall to create the
     * spikes on.
     */
    public enum Direction {
        LEFT,
        RIGHT,
    }

    /**
     * For scaling the duck and spike images.
     * @param scale     The scaling factor.
     * @param value     The number to scale.
     * @return          The scaled number.
     */
    public static int toScale(float scale, int value) {
        return Math.round(scale * value);
    }

    /**
     * Rotate the specified bitmap. This is to be used by spikes in order to generate four images
     * from the single image in the drawables folder.
     * @param source    The bitmap to rotate.
     * @param angle     The angle in which to rotate the bitmap.
     * @return          The rotated bitmap.
     */
    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Flip the specified bitmap horizontally. This is to be used by duck in order to generate
     * two more images from the two that are in the drawables folder (the drawable folders has two
     * images where the duck is facing right, but the duck needs to be able to face the other
     * direction too).
     * @param source    The bitmap to flip.
     * @return          The flipped bitmap.
     */
    public static Bitmap FlipBitmapHorizontally(Bitmap source) {
        Matrix matrix = new Matrix();
        int cx = source.getWidth() / 2;
        int cy = source.getHeight() / 2;
        matrix.postScale(-1, 1, cx, cy);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Flip the specified bitmap vertically. This is used by the spikes in order to create spikes
     * in all four orientations from the single image in the drawables folder.
     * @param source    The bitmap to flip.
     * @return          The flipped bitmap.
     */
    public static Bitmap FlipBitmapVertically(Bitmap source) {
        Matrix matrix = new Matrix();
        int cx = source.getWidth() / 2;
        int cy = source.getHeight() / 2;
        matrix.postScale(1, -1, cx, cy);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Shuffle an array. This is for spawning spikes in random locations.
     * @param array     The array of spikes.
     * @param <T>       The type of element, but should be spikes in this case.
     */
    public static <T> void Shuffle(T[] array) {
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            Swap(array, i, random.nextInt(array.length));
        }
    }

    /**
     * Helper method for the shuffle method to swap to elements.
     * @param array     The array to perform the swap on.
     * @param i         The first element.
     * @param j         The second element.
     * @param <T>       The type of element, but should be spikes in this case.
     */
    public static <T> void Swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
