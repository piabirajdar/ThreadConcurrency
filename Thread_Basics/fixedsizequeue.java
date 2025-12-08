class FixedSizeQueue {
    private final int[] arr;   // Fixed-size buffer
    private int front;         // Points to the first element
    private int rear;          // Points to the position to insert the next element
    private int size;          // Current number of elements
    private final int capacity; // Maximum size of the queue

    public FixedSizeQueue(int capacity) {
        this.capacity = capacity;
        this.arr = new int[capacity];
        this.front = 0;
        this.rear = 0;
        this.size = 0;
    }

    // Add element to queue
    public void enqueue(int x) throws Exception {
        if (isFull()) {
            throw new Exception("Queue is full");
        }
        arr[rear] = x;
        rear = (rear + 1) % capacity; // wrap around
        size++;
    }

    // Remove element from queue
    public int dequeue() throws Exception {
        if (isEmpty()) {
            throw new Exception("Queue is empty");
        }
        int val = arr[front];
        front = (front + 1) % capacity; // wrap around
        size--;
        return val;
    }

    public int peek() throws Exception {
        if (isEmpty()) {
            throw new Exception("Queue is empty");
        }
        return arr[front];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public int size() {
        return size;
    }
}


class TwoQueues {
    private final int[] arr;
    private final int capacity;
    private int front1, rear1;
    private int front2, rear2;

    public TwoQueues(int capacity) {
        this.capacity = capacity;
        this.arr = new int[capacity];
        this.front1 = this.rear1 = -1;
        this.front2 = this.rear2 = capacity;
    }

    // -------- Queue 1 Methods --------
    public void enqueue1(int x) throws Exception {
        if (rear1 + 1 == rear2) throw new Exception("Buffer full");
        if (front1 == -1) front1 = 0;
        arr[++rear1] = x;
    }

    public int dequeue1() throws Exception {
        if (front1 == -1 || front1 > rear1) throw new Exception("Queue1 empty");
        int val = arr[front1++];
        if (front1 > rear1) front1 = rear1 = -1; // reset when empty
        return val;
    }

    // -------- Queue 2 Methods --------
    public void enqueue2(int x) throws Exception {
        if (rear2 - 1 == rear1) throw new Exception("Buffer full");
        if (front2 == capacity) front2 = capacity - 1;
        arr[--rear2] = x;
    }

    public int dequeue2() throws Exception {
        if (front2 == capacity || front2 < rear2) throw new Exception("Queue2 empty");
        int val = arr[front2--];
        if (front2 < rear2) front2 = rear2 = capacity; // reset when empty
        return val;
    }

    public boolean isFull() {
        return rear1 + 1 == rear2;
    }

    public boolean isEmpty1() {
        return front1 == -1;
    }

    public boolean isEmpty2() {
        return front2 == capacity;
    }
}

