#!/usr/bin/python
from Queue import Queue
from threading import Thread
import urllib2, time, sys

#url_base = "http://localhost:8080/sparql"
url_base = "http://sparql-playground.nextprot.org/sparql"
total_calls = 30
batch_size = 1
number_of_threads = 1
queryCount = "?query=SELECT%20(COUNT(*)%20AS%20%3Fno)%20%0Awhere%20%7B%20%3Fs%20%3Fp%20%3Fo%20%20%7D"
type = "xml"


class Worker(Thread):
    """Thread executing tasks from a given tasks queue"""
    def __init__(self, tasks):
        Thread.__init__(self)
        self.tasks = tasks
        self.daemon = True
        self.start()
    
    def run(self):
        while True:
            func, args, kargs = self.tasks.get()
            try: func(*args, **kargs)
            except Exception, e: print e
            self.tasks.task_done()

class ThreadPool:
    """Pool of threads consuming tasks from a queue"""
    def __init__(self, num_threads):
        self.tasks = Queue(num_threads)
        for _ in range(num_threads): Worker(self.tasks)
    def add_task(self, func, *args, **kargs):
        """Add a task to the queue"""
        self.tasks.put((func, args, kargs))
    def wait_completion(self):
        """Wait for completion of all the tasks in the queue"""
        self.tasks.join()

class Timer(object):
    def __enter__(self):
        self.__start = time.time()
    def __exit__(self, type, value, traceback):
        self.__finish = time.time()
    def duration_in_seconds(self):
        return self.__finish - self.__start

if __name__ == '__main__':

    def export(url):
        tim = Timer()
        with tim:
            urllib2.urlopen(url).read()
        print url + " fetch in " + str(tim.duration_in_seconds()) + "\n"
 
    pool = ThreadPool(number_of_threads)
    
    globalTimer = Timer()
    with globalTimer:
        for n in range (0, total_calls, batch_size):
            pool.add_task(export, url_base + queryCount)
        pool.wait_completion()
    print "Process finished in " + str(globalTimer.duration_in_seconds()) + "\n"

