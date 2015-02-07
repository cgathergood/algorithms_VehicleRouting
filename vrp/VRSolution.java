package vrp;

import java.util.*;
import java.io.*;

public class VRSolution {
	public VRProblem prob;
	public List<List<Customer>> soln;
	ArrayList<Customer> route;
	ArrayList<Customer> newRoute;
	int count = 0;

	public VRSolution(VRProblem problem) {
		this.prob = problem;
	}

	// The dumb solver adds one route per customer
	public void oneRoutePerCustomerSolution() {
		this.soln = new ArrayList<List<Customer>>();
		for (Customer c : prob.customers) {
			ArrayList<Customer> route = new ArrayList<Customer>();
			route.add(c);
			soln.add(route);
		}
	}

	// Author's Solution - 40056161
	public void mySolver() {

		this.route = new ArrayList<Customer>(); // Master route
		this.newRoute = new ArrayList<Customer>();
		this.soln = new ArrayList<List<Customer>>();

		ArrayList<Customer> temp_customers = new ArrayList<Customer>(prob.customers);
		ArrayList<CustomerDistance> distances = new ArrayList<CustomerDistance>(); 

		// Sort Customers by distance from depot, smallest distance first. 
		// Used to establish the first customer to visit
		for (Customer c : prob.customers) {
			CustomerDistance cd = new CustomerDistance(c,c.distance(prob.depot));
			distances.add(cd);
		}
		//Sorts customers by smallest distance first
		Collections.sort(distances);

		// Adds the customer to the route
		route.add(distances.get(0).customer);
		// findRoute() - recursive function to establish the 'master route'
		findRoute(distances.get(0).customer);

		//Iterates through the master route accounting for capacity
		for (Customer c : route) {
			if (c.c + count <= prob.depot.c) {
				newRoute.add(c);
				count += c.c;
			} else if (c.c + count > prob.depot.c) {
				// Finish existing route
				soln.add(newRoute);
				// Reset the capacity count and route
				count = 0;
				count += c.c;
				newRoute = new ArrayList<Customer>();
			}
			if (!(newRoute.contains(c))) {
				newRoute.add(c);
			}
		}
		soln.add(newRoute);

		// Reset list of customers
		prob.customers = temp_customers;

	}

	public void findRoute(Customer source) {
		// Used to create the inital 'master' route
		// Finds the nearest customer to the source customer
		ArrayList<CustomerDistance> distances = new ArrayList<CustomerDistance>();
		for (Customer c : prob.customers) {
			CustomerDistance cd = new CustomerDistance(c, c.distance(source));
			distances.add(cd);
		}
		Collections.sort(distances);
		// Adds nearest customer to the route then removes the source customer 
		//(temp_customers is used to reset the customer list after problem is solved)
		route.add(distances.get(1).customer);
		prob.customers.remove(distances.get(0).customer);
		if (prob.customers.size() > 1) {
			findRoute(distances.get(1).customer);
		}
	}

	// Calculate the total journey
	public double solnCost() {
		double cost = 0;
		for (List<Customer> route : soln) {
			Customer prev = this.prob.depot;
			for (Customer c : route) {
				cost += prev.distance(c);
				prev = c;
			}
			// Add the cost of returning to the depot
			cost += prev.distance(this.prob.depot);
		}
		return cost;
	}

	public Boolean verify() {
		// Check that no route exceeds capacity
		Boolean okSoFar = true;
		for (List<Customer> route : soln) {
			// Start the spare capacity at
			int total = 0;
			for (Customer c : route)
				total += c.c;
			if (total > prob.depot.c) {
				System.out.printf(
						"********FAIL Route starting %s is over capacity %d\n",
						route.get(0), total);
				okSoFar = false;
			}
		}
		// Check that we keep the customer satisfied
		// Check that every customer is visited and the correct amount is picked
		// up
		Map<String, Integer> reqd = new HashMap<String, Integer>();
		for (Customer c : this.prob.customers) {
			String address = String.format("%fx%f", c.x, c.y);
			reqd.put(address, c.c);
		}
		for (List<Customer> route : this.soln) {
			for (Customer c : route) {
				String address = String.format("%fx%f", c.x, c.y);
				if (reqd.containsKey(address))
					reqd.put(address, reqd.get(address) - c.c);
				else
					System.out.printf("********FAIL no customer at %s\n",
							address);
			}
		}
		for (String address : reqd.keySet())
			if (reqd.get(address) != 0) {
				System.out.printf(
						"********FAIL Customer at %s has %d left over\n",
						address, reqd.get(address));
				okSoFar = false;
			}
		return okSoFar;
	}

	public void readIn(String filename) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String s;
		this.soln = new ArrayList<List<Customer>>();
		while ((s = br.readLine()) != null) {
			ArrayList<Customer> route = new ArrayList<Customer>();
			String[] xycTriple = s.split(",");
			for (int i = 0; i < xycTriple.length; i += 3)
				route.add(new Customer((int) Double.parseDouble(xycTriple[i]),
						(int) Double.parseDouble(xycTriple[i + 1]),
						(int) Double.parseDouble(xycTriple[i + 2])));
			soln.add(route);
		}
		br.close();
	}

	public void writeSVG(String probFilename, String solnFilename)
			throws Exception {
		String[] colors = "chocolate cornflowerblue crimson cyan darkblue darkcyan darkgoldenrod"
				.split(" ");
		int colIndex = 0;
		String hdr = "<?xml version='1.0'?>\n"
				+ "<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.1//EN' '../../svg11-flat.dtd'>\n"
				+ "<svg width='8cm' height='8cm' viewBox='0 0 500 500' xmlns='http://www.w3.org/2000/svg' version='1.1'>\n";
		String ftr = "</svg>";
		StringBuffer psb = new StringBuffer();
		StringBuffer ssb = new StringBuffer();
		psb.append(hdr);
		ssb.append(hdr);
		for (List<Customer> route : this.soln) {
			ssb.append(String.format("<path d='M%s %s ", this.prob.depot.x,
					this.prob.depot.y));
			for (Customer c : route)
				ssb.append(String.format("L%s %s", c.x, c.y));
			ssb.append(String.format(
					"z' stroke='%s' fill='none' stroke-width='2'/>\n",
					colors[colIndex++ % colors.length]));
		}
		for (Customer c : this.prob.customers) {
			String disk = String
					.format("<g transform='translate(%.0f,%.0f)'>"
							+ "<circle cx='0' cy='0' r='%d' fill='pink' stroke='black' stroke-width='1'/>"
							+ "<text text-anchor='middle' y='5'>%d</text>"
							+ "</g>\n", c.x, c.y, 10, c.c);
			psb.append(disk);
			ssb.append(disk);
		}
		String disk = String
				.format("<g transform='translate(%.0f,%.0f)'>"
						+ "<circle cx='0' cy='0' r='%d' fill='pink' stroke='black' stroke-width='1'/>"
						+ "<text text-anchor='middle' y='5'>%s</text>"
						+ "</g>\n", this.prob.depot.x, this.prob.depot.y, 20,
						"D");
		psb.append(disk);
		ssb.append(disk);
		psb.append(ftr);
		ssb.append(ftr);
		PrintStream ppw = new PrintStream(new FileOutputStream(probFilename));
		PrintStream spw = new PrintStream(new FileOutputStream(solnFilename));
		ppw.append(psb);
		spw.append(ssb);
		ppw.close();
		spw.close();
	}

	public void writeOut(String filename) throws Exception {
		PrintStream ps = new PrintStream(filename);
		for (List<Customer> route : this.soln) {
			boolean firstOne = true;
			for (Customer c : route) {
				if (!firstOne)
					ps.print(",");
				firstOne = false;
				ps.printf("%f,%f,%d", c.x, c.y, c.c);
			}
			ps.println();
		}
		ps.close();
	}
}
