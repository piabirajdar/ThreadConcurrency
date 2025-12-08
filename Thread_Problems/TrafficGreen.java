import java.util.concurrent.locks.ReentrantLock;

class TrafficLight {
    private int currentGreen = 1; // Initially road 1 is green
    private final ReentrantLock lock = new ReentrantLock();

    public void carArrived(
        int carId,            // car’s ID
        int roadId,           // road number
        int direction,        // direction (1=right, 2=straight, 3=left)
        Runnable turnGreen,   // turns light green for this road
        Runnable crossCar     // lets car cross
    ) {
        lock.lock();  // only one car (or road switch) at a time
        try {
            // If the light is not green for this road → switch
            if (roadId != currentGreen) {
                turnGreen.run();      // Switch the light
                currentGreen = roadId;
            }
            // Now safe to cross
            crossCar.run();
        } finally {
            lock.unlock();
        }
    }
}
