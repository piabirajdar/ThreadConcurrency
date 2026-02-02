public class MedianFinder {
    private PriorityQueue<Integer> maxHeap; 
    private PriorityQueue<Integer> minHeap;

    public MedianFinder() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        minHeap = new PriorityQueue<>();
    }

    public void addNum(int num) {
        if (maxHeap.isEmpty() || num < maxHeap.peek()) {
            maxHeap.add(num);
        } else {
            minHeap.add(num);
        }

        
        if (Math.abs(maxHeap.size() - minHeap.size()) > 1) {
            minHeap.add(maxHeap.poll());
        } else if (maxHeap.size() < minHeap.size()) {
            maxHeap.add(minHeap.poll());
        }
    }

    public double findMedian() {
        if (maxHeap.size() == minHeap.size()) {
            
            return (double) (maxHeap.peek() + minHeap.peek()) / 2;
        }

        
        return maxHeap.peek();
    }
}