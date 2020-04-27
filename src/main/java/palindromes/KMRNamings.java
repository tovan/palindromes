package palindromes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KMRNamings {
	
	private Logger logger;

	public KMRNamings(Logger logger) {
		this.logger = logger;
	}
	public Map<String, String> creatingNamings(ArrayList<String> text) {
		Map<String, String> map = new HashMap<>();
	
		int numRows = text.size();
		int numCols = text.get(0).length();
		this.printSummary(text, numRows, numCols);

		char nextName = 'A'; // this is the first name

		for (int w = 2; w <= numCols; w++) {
			checkText(text, w, numRows, numCols);
			// starting at 2, add names for string lengths of every size up to the number of
			// columns in the text

			// declare the text of names as 2d array
			String[][] textNames = new String[numRows][numCols - w + 1];
			
			// add to map to hold substrings of size width and corresponding names
			nextName = addNamesForText(text, w, numRows, numCols, textNames, map, nextName);

		}
		return map;
	}

	

	private void checkText(ArrayList<String> text, int width, int numRows, int numCols) {
		if (width > numCols) {
			// cannot name this width
			System.out.println(String.format("ERROR - Cannnot name dataset with %s columns into names of length %s",
					numCols, width)); // return;
			System.exit(1);
		}
	}

	private void printSummary(ArrayList<String> text, int numRows, int numCols) {
		System.out.println("text: \n" + text);
		System.out.println("numrows is: " + numRows + " and numCols is: " + numCols);
	}

	private char addNamesForText(ArrayList<String> text, int width, int numRows, int numCols, String[][] textNames,
			Map<String, String> map, char nextName) {
        logger.log(Level.FINE, ("processing substrings of length " + width));

		for (int i = 0; i < numRows; i++)
			for (int j = 0; j < numCols - width + 1; j++) {
				String substr = text.get(i).substring(j, j + width);
				
				logger.log(Level.FINE, ("i= " + i + " j= " + j + " substr=" + substr));
				// use substr as key
				String /* char */ c;
				if (!map.containsKey(substr)) {

					map.put(substr, String.valueOf(nextName));
					c = /* (char) */ String.valueOf(nextName);
					logger.log(Level.FINE, ("map did not contain " + substr + " new name is " + c));
					nextName++; // incr for next new Name
				} else {
					String currentName = map.get(substr);
					c = /* (char) */ String.valueOf(currentName);
					// System.out.println("map contains " + substr + " with name " + c);
				}
				// set name in text of names
				textNames[i][j] = c;
			} // end for j

		// Display key and value for each entry
		map.forEach((k, v) -> 
			logger.log(Level.FINE, (String.format("%s --> %s", k, v/*.replace("LATIN CAPITAL LETTER", "")*/)))
		);
		// Display text of Names
		logger.log(Level.FINE, ("The following is the text of names: "));

		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols - width + 1; j++) {
				logger.log(Level.FINE,(textNames[i][j] + " "));
			}
			logger.log(Level.FINE, "\n");
		}

		return nextName;
	}

	
}
