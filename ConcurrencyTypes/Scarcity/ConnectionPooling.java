import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;



// WHEN YOU NEED TO KEEP TRACK? MAINTAIN OBJECTS FOR SCARCITY USE  blocking queue.
// WHNE YOU JUST NEED PERMISSIONS(eg:10) AND HANDLE CONCRRENY MAKE USE OF SEMAPHORES.

public class ConnectionPoolWithTimeout {
    private final BlockingQueue<Connection> availableConnections;
    private final long timeoutMs;

    public ConnectionPoolWithTimeout(int poolSize, long timeoutMs) {
        this.availableConnections = new LinkedBlockingQueue<>(poolSize);
        this.timeoutMs = timeoutMs;
        for (int i = 0; i < poolSize; i++) {
            availableConnections.add(createNewConnection());
        }
    }

    public Connection acquire() throws InterruptedException {
         // dont want clinet to wait forever to get the connection...instead timeout and send back response.
        Connection conn = availableConnections.poll(timeoutMs, TimeUnit.MILLISECONDS);
        if (conn == null) {
            throw new RuntimeException("No connection available within " + timeoutMs + "ms");
        }
        return conn;
    }

    public void executeQuery(String query) throws InterruptedException {
        Connection conn = acquire();
        try {
            conn.execute(query);
        } finally {
            availableConnections.put(conn);
        }
    }
}
