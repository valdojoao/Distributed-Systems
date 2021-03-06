package fish_and_sharks;


import java.text.DecimalFormat;
import java.util.concurrent.Future;

import cl.niclabs.skandium.Skandium;
import cl.niclabs.skandium.Stream;
import cl.niclabs.skandium.skeletons.Map;
import cl.niclabs.skandium.skeletons.Skeleton;
import cl.niclabs.skandium.skeletons.While;


public class Run {

	public static int matrixSize;
	public static int generations;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {   
	
		int threads = Integer.parseInt(args[0]);	//number of threads		
		matrixSize = Integer.parseInt(args[1]); 	//get size of matrix
		generations = Integer.parseInt(args[2]);	//get the number of generations
		
		if (threads > matrixSize) {
			System.out.println("The number of threads cant be greater than the matrix size");
			System.exit(0);
		}				

		System.out.println("Original Matrix of "+matrixSize +" x "+matrixSize + " with threads = "+threads);

		long initTime = System.nanoTime() ;
		Matrix matrix = new Matrix(matrixSize);	
		double generateTime = (double)(System.nanoTime()-initTime)/ 1000000; //time to generate and print initial matrix

		System.out.println();
		System.out.println("computation after " + generations + " generations");	

		Skandium skandium = new Skandium(threads);

		Skeleton<Range, Range> map = new Map(
				new Splitter(threads),
				new Worker(),
				new Merger()
				);

		Skeleton<Range, Range> whileSkeleton = new While<Range>(map, new Cond());
		Stream<Range, Range> stream = skandium.newStream(whileSkeleton);

		long init = System.nanoTime() ;		
		Future<Range> future = stream.input(new Range(1,matrixSize));	
		Range result =  future.get();
		double totalTime = (double)(System.nanoTime()-init)/ 1000000;		//time to compute the algorithm
	
		long initPTime = System.nanoTime() ;
		//matrix.printMatrix(); 
		double printTime = (double)(System.nanoTime()-initPTime)/ 1000000;	//time to print the final result
		
		DecimalFormat df = new DecimalFormat("###,##0.000");			//format double  to alow only .000
		
		//print the outputs
		System.out.println();
		System.out.println("Time to generate and print initial matrix: "+df.format(generateTime)+"[ms]");
		System.out.println("Completion time "+df.format(totalTime)+"[ms]");
		System.out.println("Splitter time "+df.format((Splitter.splitTime/1000000))+"[ms]");
		System.out.println("Workers time "+df.format((totalTime-(Splitter.splitTime/1000000 + Merger.mergeTime/1000000)))+"[ms]");
		System.out.println("Merger time "+df.format((Merger.mergeTime/1000000))+"[ms]");
		System.out.println("Time to print last matrix: "+df.format(printTime)+"[ms]");
		System.out.println("Processors "+Runtime.getRuntime().availableProcessors());
		
		skandium.shutdown();
		System.exit(0);
	}
}
