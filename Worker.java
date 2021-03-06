package fish_and_sharks;

import java.util.Random;

import cl.niclabs.skandium.muscles.Execute;


public class Worker implements Execute<Range, Range>{

	private Random random;			//random class	

	public Worker() {		
		random = new Random();		
	}

	//receive a range, apply the fish and sharks algorithm on it, return the range
	public Range getNeighbors(Range range){	

		for (int i = range.getStart(); i <= range.getEnd(); i++) {
			for (int j = 1; j < Matrix.getMatrix().length-1; j++) {	

				manageMyNeighbors(i, j);						

				//Update the ghost row on the bottom 
				if(i == 1) 
					Matrix.newMatrix[Matrix.newMatrix.length- 1][j] = Matrix.newMatrix[i][j];	

				//Update the ghost row on the top
				if(i == (Matrix.newMatrix.length-2)) 
					Matrix.newMatrix[0][j] = Matrix.newMatrix[i][j];				
			}		

		//Update ghost columns on the right 
		Matrix.newMatrix[i][Matrix.newMatrix.length-1] = Matrix.newMatrix[i][1]; 		

		//Update ghost columns on the left 			
		Matrix.newMatrix[i][0] = Matrix.newMatrix[i][Matrix.newMatrix.length-2];
		}		
		return range;
	}


	/* Receive my position, count the numbers of my neighbors who are fish and sharks
	 * count how many of my neighbors are in breeding age */
	public void manageMyNeighbors(int x, int y){
		int countFish = 0;
		int countFishBreed = 0;
		int countShark = 0;
		int countSharkBreed = 0;

		int row, col;
		int posX = x;
		int posY = y;

		/* scan my neighbors on the row above me
		 * scan my neighbors on my row
		 * scan  my neighbors on the row bellow me */
		for (row = posX-1; row <= posX+1; row++) {	
			for (col = posY-1; col <= posY+1; col++) {
				if ((posX != row || posY != col)) {				//exclude my position
					
					if (Matrix.getMatrix()[row][col].startsWith("F")) {	//its a fish
						countFish++;					//count the number  of neighbor fishes

						if (returnAge(row, col) > 2) 			//Fish breeding age starts at 2 
							countFishBreed++;			//number  of neighbor fishes in breeding age
					}

					if (Matrix.getMatrix()[row][col].startsWith("S")) {	//its a shark
						countShark++;					//count the number  of neighbor sharks

						if (returnAge(row, col) > 3) 			//shark breeding age starts at 3
							countSharkBreed++;			//number  of neighbor sharks in breeding age
					}					
				} 		
			} 
		}

		//Manage myself according with my neighbors information
		if (Matrix.getMatrix()[x][y].startsWith("F")) 
			manageFish(x, y, countFish, countShark);		//I am fish		

		else {
			if(Matrix.getMatrix()[x][y].startsWith("E"))		//I am empty
				manageEmptyCells(x, y, countFish, countFishBreed, countShark, countSharkBreed);

			else manageShark(x, y, countFish, countShark);		//I am shark		
		}
	}

	/* An empty cell with >= 4 fish (shark) neighbors and >= 3 of them in breeding age... 
	 * and < 4 shark (fish) neighbors is filled by a new fish (shark) individual with age 1 */
	public void manageEmptyCells(int x, int y, int countFish, int countFishBreed, int countShark, int countSharkBreed){
	
		if (countFish >= 4 && countShark < 4  && countFishBreed > 2 )   		
			Matrix.newMatrix[x][y] = "F_" + 1;  		//generateFish					

		else{
			if (countShark >= 4 && countFish < 4  && countSharkBreed > 3 ) 	
				Matrix.newMatrix[x][y] = "S_" + 1;	//generateShark		
			else 
				Matrix.newMatrix[x][y] = "Empty";	//remain empty
		}
	}

	public void manageFish(int x, int y, int countFish, int countShark){
		if(returnAge(x, y) >= 10)		Matrix.newMatrix[x][y] = "Empty";	//fish live up to 10, then they die	
		else {	
			if (countFish >= 8)   		Matrix.newMatrix[x][y] = "Empty";  	//fish Dies, over population
			else {
				if (countShark >= 5)   	Matrix.newMatrix[x][y] = "Empty";  	//fish Dies, shark food
				else Matrix.newMatrix[x][y] = "F_" + (returnAge(x, y) + 1);	//otherwise its age increases
			}
		}
	}			

	public void manageShark(int x, int y, int countFish, int countShark){
		int number = random.nextInt(3);
		double randomCauses = number / 100.0;

		if(returnAge(x, y) >= 20)	Matrix.newMatrix[x][y] = "Empty";		//shark live up to 20, then they die	
		else {
			//sharks Dies by starvation or random causes 
			if ((countShark >= 6 && countFish == 0) || randomCauses== 0.03)   Matrix.newMatrix[x][y] = "Empty";  
			else Matrix.newMatrix[x][y] = "S_" + (returnAge(x, y) + 1);		//otherwise its age increases
		}
	}			

	//return age of fish or shark
	public int returnAge(int x, int y){												
		String age= Matrix.getMatrix()[x][y].substring(2);
		int ageInt=Integer.parseInt(age);			
		return ageInt;
	} 


	@Override
	public Range execute(Range range) throws Exception {
		return getNeighbors(range);
	}	
}
