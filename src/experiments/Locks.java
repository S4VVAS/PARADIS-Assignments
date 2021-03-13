package experiments;

public class Locks implements Runnable{
	
	private Linteger ei = new Linteger();

	@Override
	public void run() {
		for(int i = 0; i < 100000; i++) {
			synchronized (ei) {
				add();
			}
			
		}
	}
	
	public void add() {
		synchronized (ei) {
			ei.add();
		}
	}
	

	public static void main(String[] args) {
		Locks l = new Locks();
		
		Thread one = new Thread(l);
		Thread two = new Thread(l);
		Thread three = new Thread(l);
		
		one.start();
		two.start();
		three.start();
		
		try {
			one.join();
			two.join();
			three.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println(l.ei.get());
		
	}

}

class Linteger{
	private int i = 0;
	
	public void add() {
		i++;
	}
	
	public int get() {
		return i;
	}
	
	
}
