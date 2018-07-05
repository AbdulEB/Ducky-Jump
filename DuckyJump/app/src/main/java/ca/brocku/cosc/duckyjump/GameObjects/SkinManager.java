package ca.brocku.cosc.duckyjump.GameObjects;

import android.graphics.Color;

import ca.brocku.cosc.duckyjump.R;

/**
 * Manages the images of the different skins for the duck. Also contains the colors corresponding
 * to each skin. This color is to be used for the bubbles and the menu.
 */
public class SkinManager {

    /**
     * Static classes should not be instantiated.
     */
    private SkinManager(){}

    /**
     * Get the image ID of the duck where the duck is not jumping.
     * @param skin  The color of the duck.
     * @return      The ID of the image in resources.
     */
    public static int getBirdImageID(Skin skin) {
        switch (skin) {
            case YELLOW:
                return R.drawable.duck_yellow;
            case ORANGE:
                return R.drawable.duck_orange;
            case RED:
                return R.drawable.duck_red;
            case BLUE:
                return R.drawable.duck_blue;
            case GREEN:
                return R.drawable.duck_green;
            case PURPLE:
                return R.drawable.duck_purple;
        }
        throw new AssertionError("Skin not recognized.");
    }

    /**
     * Get the image ID of the duck where the duck is jumping.
     * @param skin  The color of the duck.
     * @return      The ID of the image in resources.
     */
    public static int getBirdJumpImageID(Skin skin) {
        switch (skin) {
            case YELLOW:
                return R.drawable.duck_yellow_jump;
            case ORANGE:
                return R.drawable.duck_orange_jump;
            case RED:
                return R.drawable.duck_red_jump;
            case BLUE:
                return R.drawable.duck_blue_jump;
            case GREEN:
                return R.drawable.duck_green_jump;
            case PURPLE:
                return R.drawable.duck_purple_jump;
        }
        throw new AssertionError("Skin not recognized.");
    }

    /**
     * Get the corresponding color of the specified duck skin.
     * @param skin  The color of the duck.
     * @return      The color associated with the specified skin.
     */
    public static int getColor(Skin skin) {
        switch (skin) {
            case YELLOW:
                return Color.rgb(253, 211, 1);
            case ORANGE:
                return Color.rgb(255, 173, 42);
            case RED:
                return Color.rgb(229, 92, 92);
            case BLUE:
                return Color.rgb(40, 189, 189);
            case GREEN:
                return Color.rgb(94, 209, 107);
            case PURPLE:
                return Color.rgb(180, 102, 169);
        }
        throw new AssertionError("Skin not recognized.");
    }

    /**
     * The duck comes in multiple colors.
     */
    public enum Skin {
        YELLOW,
        ORANGE,
        PURPLE,
        RED,
        GREEN,
        BLUE
    }

}
