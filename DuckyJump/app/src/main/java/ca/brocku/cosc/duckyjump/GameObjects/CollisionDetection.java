package ca.brocku.cosc.duckyjump.GameObjects;

/**
 * Detect if the duck has collided with a spike.
 */
public class CollisionDetection {

    /**
     * Static classes should not be instantiated.
     */
    private CollisionDetection(){}

    /**
     * Check if the duck has collided with a spike.
     * @param duck      The specified duck.
     * @param spikes    The specified spikes.
     * @return          True if a collision occurred.
     */
    public static boolean detected(Duck duck, Spikes spikes) {
        Boolean[] locations = spikes.getSpawnLocations();
        Utility.Direction direction = spikes.getDirection();

        for (int i = 0; i < locations.length; i++){

            // Skip if there is no spike in this location.
            if (!locations[i])
                continue;

            if (direction == Utility.Direction.RIGHT) {
                if (rightSpikeCollision(duck, spikes, i))
                    return true;
            } else {
                if (leftSpikeCollision(duck, spikes, i))
                    return true;
            }
        }

        return topSpikeCollision(duck, spikes) || bottomSpikeCollision(duck, spikes);
    }

    /**
     * Check if the duck has collided with a spike on the right side.
     * @param duck      The specified duck.
     * @param spikes    The specified spikes.
     * @param i         The index of the spike.
     * @return          True if a collision occurred.
     */
    private static boolean rightSpikeCollision(Duck duck, Spikes spikes, int i){
        // Triangle properties.
        int tBase = spikes.getSpikeBase();
        int tHeight = spikes.getSpikeHeight();

        // Screen properties.
        int sWidth = spikes.getScreenWidth();

        // Location of the spike.
        int x = sWidth - tHeight;
        int y = tBase + tBase * i;

        float rightSideOfDuck = duck.getX() + duck.getWidth();
        float leftSideOfSpike = x + spikes.getSpikeHeight();
        boolean sameLevel = duck.getCenterY() > y && duck.getCenterY() < y + spikes.getSpikeBase();
        return rightSideOfDuck >= leftSideOfSpike && sameLevel;
    }

    /**
     * Check if the duck has collided with a spike on the left side.
     * @param duck      The specified duck.
     * @param spikes    The specified spikes.
     * @param i         The index of the spike.
     * @return          True if a collision occurred.
     */
    private static boolean leftSpikeCollision(Duck duck, Spikes spikes, int i){
        int tBase = spikes.getSpikeBase();
        int y = tBase + tBase * i;
        boolean sameLevel = duck.getCenterY() > y && duck.getCenterY() < y + spikes.getSpikeHeight();
        return duck.getX() <= 0 && sameLevel;
    }

    /**
     * Check if the duck has collided with a spike on the top side.
     * @param duck      The specified duck.
     * @param spikes    The specified spikes.
     * @return          True if a collision occurred.
     */
    private static boolean topSpikeCollision(Duck duck, Spikes spikes){
        // The duck's hair touching the spike shouldn't kill it, hence the division by 3.
        return duck.getY() < spikes.getSpikeHeight() / 3;
    }

    /**
     * Check if the duck has collided with a spike on the bottom side.
     * @param duck      The specified duck.
     * @param spikes    The specified spikes.
     * @return          True if a collision occurred.
     */
    private static boolean bottomSpikeCollision(Duck duck, Spikes spikes){
        int height = spikes.getScreenHeight();
        return duck.getY() + duck.getHeight() > height - spikes.getSpikeHeight();
    }

}
