import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Lab4 {
	final static int INFINITY = 100000000;
	
	public static void main(String args[]) throws IOException {
		ArrayList<ArrayList<ArrayList<String>>> dicHash = new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<ArrayList<String>> wordList = new ArrayList<ArrayList<String>>();
		ArrayList<String> noSpace = new ArrayList<String>();
		String dictionary = args[0];
		String input = args[1];
		String str;
		int index, size, length; 
		int maxLength[] = new int[26];
		int M = Integer.parseInt(args[2]);
		BufferedReader br;

		for (int i=0; i<26; i++)	// Create empty lists for 26 English characters
			dicHash.add(new ArrayList<ArrayList<String>>());
		
		br = new BufferedReader(new FileReader(dictionary));
		while ((str = br.readLine()) != null) {	// Store words from dictionary to hash table
			str = str.toLowerCase();			
			index = str.charAt(0) - 'a';
			size = dicHash.get(index).size();
			length = str.length();
			if (dicHash.get(index).isEmpty())
				dicHash.get(index).add(new ArrayList<String>());
			if (length > (size -1))
				for (int i=0; i<=(length-size); i++)
					dicHash.get(index).add(new ArrayList<String>());
			dicHash.get(index).get(length).add(str);
		}
		br.close();
		
		for (int i=0; i<dicHash.size(); i++)
			maxLength[i] = dicHash.get(i).size() - 1;
		
		br = new BufferedReader(new FileReader(input));
		while ((str = br.readLine()) != null) {
			noSpace.add(str);
			wordList.add(new ArrayList<String>());
		}			
		br.close();
		/*
		for (int i=0; i<dicHash.size(); i++) {
			System.out.println("letter: "+i);
			for (int j=0; j<dicHash.get(i).size(); j++) {
				System.out.println("length: "+j);
				for (int k=0; k<dicHash.get(i).get(j).size(); k++) {
					System.out.print(dicHash.get(i).get(j).get(k)+" ");
				}
				System.out.println();
			}
			System.out.println();
		}
		*/		
		// Part I: Break input paragraphs into words
		for (int i=0; i<noSpace.size(); i++) {
			//System.out.println(noSpace.get(i));	// Print input sentences without space
			breakWords(dicHash, noSpace.get(i), wordList.get(i), maxLength);
			/*
			for (int j=0; j<wordList.get(i).size(); j++)
				System.out.print(wordList.get(i).get(j)+" ");
			System.out.println();
			*/
		}
		
		// Part II: Output the text document
		for (int i=0; i<noSpace.size(); i++)			
			wrapWords(M, wordList.get(i).size(),wordList.get(i));
		
	}
	public static void breakWords(ArrayList<ArrayList<ArrayList<String>>> dicHash, String noSpace, ArrayList<String> wordList,
			 					  int[] maxLength) {
		String word, refWord;
		int indexSize, listSize, index;
		int cursor = 0;
		int incLen= 1;
		int wordIndex = -1;
		int wordLength = 0;
		boolean find;

		while (cursor >= 0 && cursor < noSpace.length()) {
			find = false;	// Initialize find tag
			index = noSpace.toLowerCase().charAt(cursor) - 'a';				// Index letter of dictionary
			Search: {
				if (index >= 0 && index <= 25) {							// Index must be a lower-case letter
					for (int len=incLen; len<=maxLength[index]; len++) {	// Increasing the length of word capture
						if ((cursor + len) <= noSpace.length()) {			// Out of bounce condition
							word = noSpace.substring(cursor, cursor+len);
							indexSize = dicHash.get(index).size();
							for (int i=0; i<indexSize; i++) {
								listSize = dicHash.get(index).get(i).size();
								for (int j=0; j<listSize; j++) {
									refWord = dicHash.get(index).get(i).get(j);
									//System.out.println(word.toLowerCase() + "	:	" + refWord);
									if (refWord.equals(word.toLowerCase())) {
										//System.out.println("Matched!");											
										find = true;
										wordList.add(word);
										//System.out.println("Word List Size: " + wordList.size());
										wordIndex = wordList.lastIndexOf(word);
										wordLength = word.length();
										cursor += wordLength;				// Cursor moves to next word
										incLen = 1;
										break Search;
									}
								}
							}
						}
					}
				} 
			}
			if (!find) {
				wordLength = wordList.get(wordIndex).length();
				cursor -= wordLength;								// Cursor moves to previous word
				wordList.remove(wordIndex);							// Remove last word from list
				wordIndex--;										// Index moves to previous word
				index = noSpace.toLowerCase().charAt(cursor) - 'a';	// Update index with previous word
				//System.out.println("Max Length of " + index +": " + maxLength[index]);
				if ((wordLength + 1) <= maxLength[index])			// Check if increment is longer than max length of dictionary
					incLen = wordLength + 1;
			}
		}
	}

	public static void wrapWords(int M, int size, ArrayList<String> wordList) {
		int[][] spaceLeft = new int[size+1][size+1];
		int[][] lineCost = new int[size+1][size+1];
		int[] totalCost = new int[size+1];
		int[] wrap = new int[size+1];
		
		for (int i=1; i<=size; i++) {
			spaceLeft[i][i] = M - wordList.get(i-1).length();
			for (int j=i+1; j<=size; j++) {
				spaceLeft[i][j] = spaceLeft[i][j-1] - wordList.get(j-1).length() -1;
			}
		}
		
		for (int i=1; i<=size; i++) {
			for (int j=i; j<=size; j++) {
				if (spaceLeft[i][j] < 0)	// No more space left in line
					lineCost[i][j] = INFINITY;
				else if (j == size && spaceLeft[i][j] >= 0) // The end of paragraph
					lineCost[i][j] = 0;
				else
					lineCost[i][j] = spaceLeft[i][j]*spaceLeft[i][j]*spaceLeft[i][j];	//LineCost = SpaceLeft^3
			}
		}
		
		totalCost[0] = 0;
		for (int j=1; j<=size; j++) {
			totalCost[j] = INFINITY;
			for (int i=1; i<=j; i++) {
				if (totalCost[i-1] != INFINITY && lineCost[i][j] != INFINITY &&
						totalCost[i-1] + lineCost[i][j] < totalCost[j]) {
					totalCost[j] = totalCost[i-1] + lineCost[i][j];
					wrap[j] = i;
				}					
			}
		}
		/*
		for (int i=0; i<spaceLeft.length; i++) {
			System.out.println();
			for (int j=0; j<spaceLeft.length; j++) {
				System.out.print(spaceLeft[i][j] + " ");
			}
		}
		for (int i=0; i<lineCost.length; i++) {
			System.out.println();
			for (int j=0; j<lineCost.length; j++) {
				System.out.print(lineCost[i][j] + " ");
			}
		}
		for (int i=0; i<totalCost.length; i++)
			System.out.print(totalCost[i] + " ");
			
		for (int i=0; i<wrap.length; i++)
			System.out.print(wrap[i] + " ");			
		*/
		printLine(wrap, size, wordList, totalCost[size]);			
	}
	
	public static void printLine(int[] wrap, int size, ArrayList<String> wordList, int totalCost) {
		if (wrap[size] > 1)
			printLine(wrap, wrap[size]-1, wordList, totalCost);

		for (int i=wrap[size]-1; i<size; i++) {
			System.out.print(wordList.get(i));
			if (i < size-1)
				System.out.print(" ");				
		}
		System.out.println();
		if (size == wordList.size())
			System.out.println(totalCost);
	}
}
