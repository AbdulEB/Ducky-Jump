package ca.brocku.cosc.duckyjump.GameObjects;

/**
 * Used to represent locations and forces that are acted out on the duck.
 */
class Vector {

    private float x, y;
    private float limit = Float.MAX_VALUE;

    /**
     * Construct a vector.
     * @param x     The x coordinate.
     * @param y     The y coordinate.
     */
    Vector (float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x coordinate.
     * @return  The x coordinate.
     */
    float getX () {
        return x;
    }

    /**
     * Get the y coordinate.
     * @return  The y coordinate.
     */
    float getY () {
        return y;
    }

    /**
     * Set the x and y coordinates to a new location.
     * @param x     The new x coordinate.
     * @param y     The new y coordinate.
     */
    void setXY (float x, float y) {
        setX(x);
        setY(y);
    }

    /**
     * Set only the x coordinate.
     * @param x     The new x coordinate.
     */
    private void setX (float x) {
        this.x = x;
    }

    /**
     * Set onl the y coordinate.
     * @param y     The y coordinate.
     */
    private void setY (float y) {
        this.y = y;
    }

    /**
     * Add a vector to the current one.
     * @param v     The vector to add to this one.
     */
    void add (Vector v) {
        x += v.x;
        y += v.y;
        limit();
    }

    /**
     * Subtract a vector from the current one.
     * @param v     The vector to subtract from this one.
     */
    void sub (Vector v) {
        x -= v.x;
        y -= v.y;
        limit();
    }

    /**
     * Multiple the current vector by a particular value.
     * @param s     The value to multiply the current vector by.
     */
    void mul (float s) {
        x *= s;
        y *= s;
        limit();
    }

    /**
     * Divide the current vector by a particular value.
     * @param s     The value to divide the current vectory by.
     */
    void div (float s) {
        x /= s;
        x /= s;
        limit();
    }

    /**
     * Change the x and y coordinate so that their magnitude is equal to one.
     */
    void normalize () {
        float m = mag();
        if (m > 0) {
            x /= m;
            y /= m;
        }
    }

    /**
     * Get the magnitude of the vector.
     * @return      The magnitute of the vector.
     */
    private float mag () {
        return (float)Math.sqrt(x*x + y*y);
    }

    /**
     * Limit the magnitude of the vector. This is so the duck doesn't go too fast.
     * @param l     The length of the maximum magnitude.
     */
    void limit (float l) {
        limit = l;
        limit();
    }

    /**
     * Perform the limit.
     */
    private void limit () {
        float m = mag();
        if (m > limit) {
            float ratio = m / limit;
            x /= ratio;
            y /= ratio;
        }
    }

    /**
     * Create a deep copy of the vector.
     * @return  A deep copy of the vector.
     */
    public Vector clone () {
        return new Vector(x, y);
    }

    /**
     * Convert the vector to a string.
     * @return  The vector as represented by a string.
     */
    public String toString () {
        return "(" + x + ", " + y + ")";
    }

}