package vrp;

public class CustomerDistance implements Comparable {
	// Each customer has a list of the distances between itself
	// and every other customer
	public Customer customer;
	public double distance;

	public CustomerDistance(Customer c, double d) {
		customer = c;
		distance = d;
	}

	@Override
	// Comparator used to return the smallest distance
	public int compareTo(Object arg0) {
		if (distance > ((CustomerDistance) arg0).distance) {
			return 1;
		} else if (((CustomerDistance) arg0).distance > distance) {
			return -1;
		}
		return 0;
	}
}
