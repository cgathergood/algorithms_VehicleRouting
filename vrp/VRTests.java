package vrp;

import java.util.ArrayList;
import java.io.*;

public class VRTests {

	public static void main(String[] args)throws Exception {
		String [] shouldPass = {
				"rand00010",
				"rand00020",
				"rand00030",
				"rand00040",
				"rand00050",
				"rand00060",
				"rand00070",
				"rand00080",
				"rand00090",
				"rand00100",
				"rand00200",
				"rand00300",
				"rand00400",
				"rand00500",
				"rand00600",
				"rand00700",
				"rand00800",
				"rand00900",
				"rand01000",
				"rand02000"
				};
		String [] shouldFail = {
				"fail00002",
				"fail00004"
				};
		System.out.println("\nShould Pass");
		System.out.println("Problem     \tSoln\tSize\tCost\tValid");
		for (String base:shouldPass){
			VRProblem vrp = new VRProblem(base+"prob.csv");
			VRSolution vrs = new VRSolution(vrp);

			//Create a new solution using our poor algorithm
			//vrs.oneRoutePerCustomerSolution();
			vrs.mySolver();
			

			System.out.printf("%s\t%s\t%d\t%.0f\t%s\n",base,"MyAlg",vrp.size(),vrs.solnCost(),vrs.verify());
			vrs.writeSVG(base+"prob.svg",base+"ALGsn.svg");
			if (new File(base+"cwsn.csv").exists()){
				vrs.readIn(base+"cwsn.csv");

				//Print out results of costing and verifying the solution
				System.out.printf("%s\t%s\t%d\t%.0f\t%s\n",base,"CW",vrp.size(),vrs.solnCost(),vrs.verify());
				//Write the SVG file
				vrs.writeSVG(base+"prob.svg",base+"cwsn.svg");
			}
		}
		System.out.println("\nShould Fail");
		System.out.println("Problem\tSolution\tSize\tCost\tValid");
		for (String b:shouldFail){
			VRProblem vrp = new VRProblem(b+"prob.csv");
			VRSolution vrs = new VRSolution(vrp);
			if (new File(b+"soln.csv").exists()){

				//Read an existing solution file
				vrs.readIn(b+"soln.csv");

				//Print out results of costing and verifying the solution
				System.out.printf("%s\t%s\t%d\t%.0f\t%s\n",b,b,vrp.size(),vrs.solnCost(),vrs.verify());
			
				//Write the SVG file
				vrs.writeSVG(b+"prob.svg", b+"soln.svg");
			}
		}
	}
}
