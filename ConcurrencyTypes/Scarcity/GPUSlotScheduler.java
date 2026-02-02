

// GPUs are scarce

// Each job needs GPU memory

// Higher priority runs first

// If GPUs aren’t available → queue or reject

// Must be thread-safe

// 🧠 Design (say this first)

// “I model GPUs as slots with fixed memory.
// Jobs go into a priority queue.
// A scheduler thread assigns jobs only if memory fits.”

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

class GPU {
    int memory;
    int id;
    public GPU(int memory, int id){
        this.memory = memory;
        this.id = id;
    }
}
class Job {
    int seq;
    int memory;
    int priority;

}
class GPUScheduler {
    PriorityBlockingQueue<Job> queue  = new PriorityBlockingQueue<>(10, (a, b) -> {
        if(a.priority != b.priority) {
            return Integer.compare(b.priority, a.priority);
        }
        return Long.compare(a.seq, b.seq);
    });
    ExecutorService workers;

    AtomicInteger seq = new AtomicInteger();

    List<GPU> gpus = new ArrayList<>();
    public GPUScheduler(int numberOfGPUs, int memory) {
        for(int i=0; i < numberOfGPUs; i++) {
            gpus.add(new GPU(i, memory));
        }
        this.workers = = Executors.newFixedThreadPool(5);
        runScheduler();
    }

    public synchronized void submitJob(int memoryRequired, int priority) {
        queue.offer(new Job(seq.incrementAndGet(), memoryRequired, priority));
    }

    public void runScheduler() {
        while (true) {
            try {
                Job job = queue.take(); // blocks until a job arrives

                Gpu gpu = acquireGpu(job.memoryRequired);
                if (gpu == null) {
                    queue.offer(job);        // no GPU fits right now
                    Thread.sleep(10);        // avoid tight loop
                    continue;
                }

                workers.submit(() -> {
                    try {
                        job.task.run();
                    } finally {
                        releaseGpu(gpu, job.memoryRequired);
                    }
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break; // exit scheduler loop cleanly
            }
        }
    }

    public synchronized GPU acquireGpu(int memoryRequired) {
        for(GPU gpu : gpus){
            if(gpu.memory >= memoryRequired){
                gpu.memory -= memoryRequired;
                return gpu;
            }
        }
        return null;
    }


    public synchronized void releaseGPU(int memoryRelease) {
        gpu.memory += memoryRequired;
    }
}