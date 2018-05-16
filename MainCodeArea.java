import java.io.*;
import java.util.*;
import java.util.ArrayList;




public class MainCodeArea {

	private int numTruthValues =0;

	public MainCodeArea(){}

	/*
	reads input from input3.txt file. 
	reads input by line a into a line array with a delimiter
	then passes the int ArrayList 
	then sends int array list to choice
	*/
	public ArrayList<Integer> readFile(){
		ArrayList<Integer> clauses = new ArrayList<Integer>();
		String line = null;
		String[] lineArray;

		try{
			FileReader fileReader = new FileReader("input3.txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);  
			while( ( line = bufferedReader.readLine() ) != null ){
				lineArray = line.split(" ");
				clauses.add(Integer.parseInt(lineArray[0]));
				clauses.add(Integer.parseInt(lineArray[1])); 
			}
		}
		catch(FileNotFoundException exFileNotFound){
			System.out.println("input3.txt cant find");
		}
		catch(IOException exIO){
			System.out.println("cant Read File!");
		}
		return clauses;
	}

	

	public void menu(){
		ArrayList<Integer> clauses;
		ArrayList<Integer> allVariables;
		Scanner user = new Scanner(System.in);
		String output = "\n1) True Optimal Solution (slow)\n2) Pretty Good Solution (fast)\n>";
		int ui = 1;

		while(ui>0 && ui<3){
			numTruthValues = 0;
			clauses = readFile();
			allVariables = allPossibleVariables(clauses);
			System.out.print(output);
			ui = user.nextInt();
			if(ui == 1)
				bruteForcePart1(clauses,allVariables);
			
			else
				notBruteForcePart1(clauses,allVariables);
			
		}
	}

	public ArrayList<Integer> allPossibleVariables(ArrayList<Integer> clauses){
		ArrayList<Integer> allVariables = new ArrayList<Integer>();

		int temp,totalNums = 0;

		for(int i = 0; i < clauses.size(); i++){ // put this in read file
			temp = Math.abs(clauses.get(i));
			if(checkIfItExists(allVariables,temp) == false){ //checks if it does not exist
				allVariables.add(temp);
				totalNums++;
				allVariables.add((temp * -1));
			}
		}
		allVariables.add(totalNums);//stores total nums
		return allVariables;
	}
	/*
	returns true if a member of the ArrayList Integer exists 
	retruns false if it doesnt 
	*/
	public boolean checkIfItExists(ArrayList<Integer> ar,int temp){
		for(int i = 0; i < ar.size(); i++)
			if(temp ==ar.get(i))  return true; // does exist
		return false; // does not exist
	}

	/*
	creates an array of all x variables used 
	and a truth table 
	with the corresponding not values
	*/
	public void bruteForcePart1(ArrayList<Integer> clauses,ArrayList<Integer> allVariables){
		int totalNums = allVariables.remove(allVariables.size()-1); 
		int[][] truthTable = createTruthTable(allVariables,totalNums);
		bruteForceMethod(clauses,truthTable);
	}
	/*
	creates a truth table with the coresspoding not values
	*/
	public int[][] createTruthTable(ArrayList<Integer> allVariables,int totalNums){
		int[][] truthTable = new int[(int)((Math.pow(2,totalNums))+1)][totalNums*2];
		int ans = 0, column = 0;

		for(int col = 0; col < (totalNums*2) ; col++)
			truthTable[0][col] = allVariables.get(col);

		for (int row = 1; row<(int)((Math.pow(2,totalNums))+1); row++) {
			column = 0;
			for (int power=totalNums-1; power>=0; power--) {
				ans = ((row-1)/ ((int) Math.pow(2, power)))%2 ;
				if(ans == 0) ans = 1;
				else ans = 0;
				truthTable[row][column] = ans;
				column++;
				if(ans != 0) ans = 0;
				else ans = 1;	
				truthTable[row][column] = ans;
				column++;
			}
		}
		return truthTable;
	}

	
	/*
	get the corresponding variables boolean value via the truth table 
	by finding its coressponding column : 
	truthTable[row itteration][coresspponding(x1 or x2) col]
	*/
	public int getColIndex (int[][] truthTable,  int x1){
		for(int col = 0; col < truthTable[0].length ; col++)
			if( x1 == truthTable[0][col]) return col;
		return -1;// forerror
	}
	/*
	checks loops through the truth table rows, 
		within each row, we loop through all the clauses 
		and store clauses in variables x1 and x2

		then we get the corresponding variables boolean value via the truth table 
		by finding its coressponding column : 
		truthTable[row itteration][coresspponding(x1 or x2) col]
		we then check 
		if either x1Boolean and x2Boolean values are == 1
			then we increase the truth counter

	before the ending of each row itteration 
	we take the max truthCounter and the previous max 
	to find the max amount of truths between all the itterations
	*/
	public void bruteForceMethod(ArrayList<Integer> clauses, int[][] truthTable){
		int x1Bool = 1, x2Bool = 1, prevMax = 0, currMax = 0, col = 0, truthCounter = 0;
		String output = "";
		boolean test = true;

		for(int row = 1; row < truthTable.length; row++){
			truthCounter = 0;
			for (int clos = 0; clos < clauses.size() ; clos++ ) {

				col = getColIndex(truthTable, clauses.get(clos) );
				x1Bool = truthTable[row][col];

				clos++;

				col = getColIndex(truthTable, clauses.get(clos) );
				x2Bool = truthTable[row][col];

				if( ( (x1Bool == 1) || (x2Bool == 1) ) )
					truthCounter++;
			}
			prevMax = currMax;
			currMax = Math.max(truthCounter,currMax);
			if(currMax == truthCounter ){
				if(currMax > prevMax ) {
					output = output.replaceAll("T","").replaceAll("F","");
					test = true;
				}
				if(test){
					for(int c = 0; c < truthTable[0].length; c++){
						if(truthTable[0][c] > 0){
							if (truthTable[row][c] == 1) output += "T";
							else output += "F";	
						}
					}
					test = false;
				}
			}
		}
		System.out.println("Max truths " + currMax + "\n"+output);
	}

		/*
	creates the an array list with only the x variables used within the input
	*/
	public ArrayList<Integer> xVarUsed(ArrayList<Integer> clauses, ArrayList<Integer> absValueList){
		ArrayList<Integer> xVarUsed = new ArrayList<Integer>();
		int temp = 0;

		for(int i = 0; i < absValueList.size(); i++){
			temp = absValueList.get(i);
			if(checkIfItExists(clauses,temp))
				xVarUsed.add(temp);
		}
		return xVarUsed; 
	}	

	public int countPosNegs(ArrayList<Integer>clauses, int comp){
		int pos = 0, neg = 0;

		for (int i = 0 ; i < clauses.size() ; i++ ) {
			if( clauses.get(i) == comp ){
				if(clauses.get(i) > 0) pos++;
				else neg++;
			}
		}
		if(pos>neg) return pos;
		else return neg;
	}

	public ArrayList<Integer>  removeClauses(ArrayList<Integer>clauses, int remove){
		for (int i = 0; i < clauses.size(); i ++ ) {
			if(clauses.get(i) == remove || clauses.get(i+1) == remove ){
				clauses.remove(i);
				clauses.remove(i);
				i = -1;
				numTruthValues++;
			}
			else
				i++;
		}
		return clauses;
	}

	public void notBruteForcePart1(ArrayList<Integer>clauses,ArrayList<Integer>allVariables){
		int totalNums = allVariables.remove(allVariables.size()-1);
		ArrayList<Integer> xVarUsed = xVarUsed(clauses,allVariables);

		int[][] varsNumofPosNeg = new int[xVarUsed.size()][2];

		for(int i = 0; i < xVarUsed.size(); i++){
			varsNumofPosNeg[i][0] = xVarUsed.get(i);
		}
		for (int i = 0 ; i < xVarUsed.size() ; i++ ) {
			for (int j = 1; j < 2 ; j++){
				varsNumofPosNeg[i][j] = countPosNegs(clauses, allVariables.get(i));
			}
		}

		notBruteForcePart2(clauses,varsNumofPosNeg);
	}

	public void notBruteForcePart2(ArrayList<Integer>clauses,int[][] varsNumofPosNeg ){
		int x1 = 0, x2 = 0; 
		String output = "";

		for (int i = 0 ; i < varsNumofPosNeg.length ; i++ ) {
			if(i+1 < varsNumofPosNeg.length ){
				if(varsNumofPosNeg[i][0] == (varsNumofPosNeg[i+1][0])*-1){
					x1 = varsNumofPosNeg[i][1];
					x2 = varsNumofPosNeg[i+1][1];
				}
			}
			else{
				x1 = varsNumofPosNeg[i][1];
				x2 = 0;
			}
			if(x1 > x2)
				clauses = removeClauses(clauses,varsNumofPosNeg[i][0]);
			else if ( x1 < x2)
				clauses = removeClauses(clauses,varsNumofPosNeg[i+1][0]);
			i++;

			if(x1 > x2) output += "T";
			else output += "F";		
		}
		System.out.println(numTruthValues + "\n" +output);
	}
}

