package kr.re.odp.initial.metadata.db.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * thread에 관련된 유틸
 * 
 * @author MJYoun
 * @since 2018. 11. 15.
 *
 */
public class ThreadUtil {

	/**
	 * thread를 저장하는 queue
	 * 
	 * @author MJYoun
	 * @since 2018. 11. 15.
	 *
	 */
	private static class ThreadPoolQueue {
		
		/* 실질적으로 저장되는 queue */
		private LinkedList<Object> queue = null;
		
		/* queue에 저장될 수 있는 최대 사이즈 */
		private int MAX_QUEUE_SIZE = 5;
		
		public ThreadPoolQueue() {
			this.queue = new LinkedList<Object>();
		}
		
		public ThreadPoolQueue(int queueSize) {
			this();
			this.MAX_QUEUE_SIZE = queueSize;
		}
		
		/**
		 * queue의 put동작을 하는 함수
		 * full일 경우 대기하고, empty일 경우 바로 실행한다. 
		 * 
		 * @param item
		 * @throws InterruptedException
		 */
		public synchronized void put(Object item) throws InterruptedException {
			while (queue.size() == MAX_QUEUE_SIZE) {
				wait(); // thread가 resource를 반납하고 잠든다.
			}
			
			if (queue.isEmpty()) {
				notifyAll(); // 잠들어 있던 모든 thread를 깨운다.
			}
			
			queue.add(item);
		}
		
		/**
		 * queue의 get동작을 하는 함수
		 * full일 경우 바로 실행하고, empty일 경우 대기한다.
		 * 
		 * @return
		 * @throws InterruptedException
		 */
		public synchronized Object get() throws InterruptedException {
			while (queue.isEmpty()) {
				wait();
			}
			
			if (queue.size() == MAX_QUEUE_SIZE) {
				notifyAll();
			}
			
			return queue.removeFirst();
		}
		
	}
	
	/**
	 * 실제 동작을 실행할 thread
	 * 
	 * @author MJYoun
	 * @since 2018. 11. 15.
	 *
	 */
	private static class ThreadPoolRunnable implements Runnable {

		/* 실행시 가져올 queue */
		private ThreadPoolQueue queue;
		
		/* 상태를 확인하기 위한 정보 */
		/* 변수의 원자성 보호를 위해 voldatile 선언하였다. 동시에 접근 할 수 없도록 막는다. */
		private volatile boolean running;
		
		public ThreadPoolRunnable(ThreadPoolQueue queue) {
			this.queue = queue;
			this.running = true;
		}
		
		/**
		 * thread를 실행 시키는 함수 시작하자마자 모두 동작되며, queue를 바라보고 있게 된다.
		 */
		@Override
		public void run() {
			while (running) {
				try {
					Thread.sleep(10);
					Runnable r = (Runnable) queue.get();
					r.run();
				} catch (InterruptedException ie) {
					this.stop();
				}
			}
		}
		
		/**
		 * thread의 상태를 start로 변경하고 대기 하게 만든다.
		 */
		public void start() {
			if (!this.running) {
				this.running = true;
				this.run();
			}
		}
		
		/**
		 * 동작중이던 thread를 중지시킨다.
		 */
		public void stop() {
			this.running = false;
		}
		
	}
	
	/**
	 * thread queue와 thread pool을 만들어서 관리하는 클래스
	 * 
	 * @author MJYoun
	 * @since 2018. 11. 15.
	 *
	 */
	public static class ThreadPool {
		
		/* queue */
		private ThreadPoolQueue queue;
		/* 실행하는 thread 목록 */
		private List<ThreadPoolRunnable> threadList;
		
		/* max thread size */
		private int MAX_THREAD_SIZE = 5;
		
		/* max queue size */
		private int MAX_QUEUE_SIZE = 5;
		
		/* thread pool의 상태를 확인하기 위한 변수 */
		private volatile boolean running = true;
		
		public ThreadPool() {
			init();
		}
		
		public ThreadPool(int threadSize, int queueSize) {
			this.MAX_THREAD_SIZE = threadSize;
			this.MAX_QUEUE_SIZE = queueSize;
			init();
		}
		
		/**
		 * thread pool 초기화
		 * thread를 생성하고 시작한다.
		 */
		private void init() {
			this.threadList = new ArrayList<ThreadPoolRunnable>();
			this.queue = new ThreadPoolQueue(MAX_QUEUE_SIZE);
			for (int i = 0; i < MAX_THREAD_SIZE; i++) {
				ThreadPoolRunnable thread = new ThreadPoolRunnable(queue);
				this.threadList.add(thread);
				new Thread(thread).start();
			}
		}
		
		/**
		 * thread를 실행하는 함수
		 * 실질적으로는 queue에 넣어주는 역활만 한다.
		 * 
		 * @param item
		 * @throws RuntimeException
		 * 			pool이 멈춰있을때 exception을 throw 한다.
		 * @throws InterruptedException
		 */
		public synchronized void execute(Runnable item) throws Exception {
			if (!this.running) {
				throw new Exception("Thread Pool is not running");
			}
			
			this.queue.put(item);
		}
		
		/**
		 * thread pool 실행, 내부의 thread 들도 같이 실행된다.
		 */
		public synchronized void start() {
			this.running = true;
			for (ThreadPoolRunnable r : this.threadList) {
				r.start();
			}
		}
		
		/**
		 * thread pool 정지, 내부의 thread 들도 같이 정지된다.
		 */
		public synchronized void stop() {
			this.running = false;
			for (ThreadPoolRunnable r : this.threadList) {
				r.stop();
			}
		}
		
		/**
		 * 현재 상태를 확인 할 수 있는 함수
		 * 
		 * @return
		 */
		public boolean isRunning() {
			return this.running;
		}
		
	}
	
}
