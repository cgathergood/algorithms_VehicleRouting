package vrp;

import java.util.*;
public class Experiment {

	public static void main(String[] args)throws Exception{
		long total = 0;
		System.out.printf("Problem  \tSize  \tCost  \tTime\n");
		String [] probs = {
//				"rand00100",
//				"rand00200","rand00300",
//				"rand00400","rand00500",
//				"rand00600","rand00700",
//				"rand00800","rand00900",
//				"rand01000", "rand02000"
				"rand02000"
				};
		for (String f:probs){
			ArrayList<Long> timing = new ArrayList<Long>();
			VRProblem vrp = new VRProblem(f+"prob.csv");
			VRSolution vrs = new VRSolution(vrp);
			for(int i=0;i<50;i++){
				long start = System.nanoTime();
				//vrs.oneRoutePerCustomerSolution();
				vrs.mySolver();
				timing.add(System.nanoTime()-start);
			}
			for(long i : timing){
				total =+ i;
			}
			
			vrs.writeOut(f+"dmsn.csv");
			System.out.printf("%s  \t%d  \t%f  \t%s\n",
					f,vrp.size(),
					vrs.solnCost(),total/50);
	
			//System.out.println("Average Time: "+total/50);
		}
		
	}
}
