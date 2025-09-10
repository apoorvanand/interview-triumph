import threading
import queue
import time
import logging
class ThreadPool:
    def __init__(self, num_threads):
        self.tasks = queue.Queue()
        self.shutdown_flag = threading.Event()
        logging.basicConfig(level=logging.ERROR, format='%(asctime)s - %(levelname)s - %(message)s')
        self.workers = []  # List to keep track of worker threads
        self.threads = []
        self.shutdown_flag = threading.Event()
        for _ in range(num_threads):
            thread = threading.Thread(target=self.worker)
            thread.start()
            self.threads.append(thread)

    def worker(self):
        while not self.shutdown_flag.is_set():
            try:
                func, args, kwargs = self.tasks.get(timeout=1)
                try:
                    func(*args, **kwargs)
                except Exception as e:
                    logging.error(f"Error executing task: {e}")
                finally:
                    self.tasks.task_done()
            except queue.Empty:
                continue

    def add_task(self, func, *args, **kwargs):
        self.tasks.put((func, args, kwargs))

    def wait_completion(self):
        self.tasks.join()

    def shutdown(self):
        self.shutdown_flag.set()
        for thread in self.threads:
            thread.join()

if __name__ == "__main__":
    def example_task(name, duration):
        print(f"Task {name} starting")
        time.sleep(duration)
        print(f"Task {name} completed")

    pool = ThreadPool(3)
    pool.add_task(example_task, "A", 2)
    pool.add_task(example_task, "B", 1)
    pool.add_task(example_task, "C", 3)
    pool.add_task(example_task, "D", 2)
    pool.wait_completion()
    pool.shutdown()