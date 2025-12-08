🛑 wait(), 🟡 notify(), and 🔁 notifyAll() Methods:

‍ These methods must be called from within a synchronized context (a synchronized block or method) on the same object whose monitor the thread is waiting on.
 They work together with a shared condition (often a flag or another shared variable lLOCK) that threads check in a loop to handle spurious wakeups. 🔄🔍


PRODUCER CONSUMER PROBELM ABOVE FILE
2. How does thread interruption work with communication methods? 🧵❗

Answer: When a thread is waiting (using wait(), join(), or blocking queue methods), it can be interrupted by another thread calling its interrupt() method. This causes an InterruptedException to be thrown, allowing the waiting thread to handle the interruption. Proper handling involves either re-interrupting the thread or propagating the exception.

‍‍