package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class KMRHorizontal3 {

	public static void main(String[] args) throws IOException {
		KMRHorizontal3 kmr = new KMRHorizontal3();
		ArrayList<String> text = kmr.getTextAsList("FILE LOCATION GOES HERE");

		// 1. step 1 create map of names
		Map<String, String> namings = kmr.creatingNamings(text);

		// 2. step 2 search input array for all maximal palindromes
		ArrayList<String> palindromes = new ArrayList<String>();
		// int numCols = text.get(0).length();
		for (int i = 3; i <= 6; i++) {
			// loop to go over all possible name lengths
			for (int leftIndex = 0; leftIndex <= (text.get(0).length() - i); leftIndex++) {
				// loop to go over columns, w width at a time, look at rectangles
				for (int indexOfMidpointRow = 1; indexOfMidpointRow <= text.size() - 1; /*Math.ceil( new Double(text.size()) / 2);*/ indexOfMidpointRow++) { 
					// loop to go over rows 2 and up and treat each as a midpoint

					final int currNameLength = i;
					final int finalLeftIndex = leftIndex;
				
					//for now assume only mismatches of only 1 are allowed, can refactor later
					List<String> listA = text.subList(0, indexOfMidpointRow).stream().map(s -> {
						
						System.out.println("looking for name for reverses of: " + s.substring(finalLeftIndex, finalLeftIndex + currNameLength));

						
						String reversedTemp = StringUtils
								.reverse(s.substring(finalLeftIndex, finalLeftIndex + currNameLength));
						String name = namings.get(reversedTemp) == null ? "" : namings.get(reversedTemp);
						return name;
					}).collect(Collectors.toList());
					//b's left index must be updated if a's index is
					String b = text.subList(indexOfMidpointRow, text.size()).stream().map(s -> {
						return namings.get(s.substring(finalLeftIndex, finalLeftIndex + currNameLength));
					}).collect(Collectors.joining());


					if(listA.isEmpty() || listA.contains("")) { // no name for reversed a
						listA = text.subList(0, indexOfMidpointRow).stream().map(s -> {
							
							System.out.println("looking for name for reverses of: " + s.substring(finalLeftIndex + 1, finalLeftIndex + currNameLength));
							String reversedTemp = StringUtils
									.reverse(s.substring(finalLeftIndex + 1, finalLeftIndex + currNameLength));
							String name = namings.get(reversedTemp) == null ? "" : namings.get(reversedTemp);
							
							return name;
						}).collect(Collectors.toList());
						
						b = text.subList(indexOfMidpointRow, text.size()).stream().map(s -> {
							return namings.get(s.substring(finalLeftIndex, finalLeftIndex + currNameLength - 1));
						}).collect(Collectors.joining());

					}
					
					String a = StringUtils.reverse(listA.stream().collect(Collectors.joining()));
					
					System.out.println("Comparing a and b: " + a + ", " + b);
					//kmr.checkCommonPrefixAndStepOverMisMatch(kmr, palindromes, a, b, false);

					String commonPrefix = kmr.LCP(a, b);

					if (commonPrefix.length() > 0) {
						palindromes.add(StringUtils.reverse(a) + b);
						int nonZeroBasedStartPosition = leftIndex+1;
						int endPosition = leftIndex + currNameLength;
						System.out.println("**added palindrome for: "+ commonPrefix +" added palindrome for " + a + " and " + b + " starting at column " + nonZeroBasedStartPosition + " ending at column: " + endPosition + "; (0-based) index of midpoint row: " + indexOfMidpointRow);
					}

					List<String> a1List = text.subList(0, indexOfMidpointRow + 1).stream().map(s -> {
						String reversedTemp = StringUtils.reverse(s.substring(finalLeftIndex, finalLeftIndex + currNameLength));
						return namings.get(reversedTemp) == null ? "" : namings.get(reversedTemp);
					}).collect(Collectors.toList());
					
					if(a1List.isEmpty() || a1List.contains("")) { 	
						a1List = text.subList(0, indexOfMidpointRow + 1).stream().map(s -> {
							
							System.out.println("looking for name for reverses of: " + s.substring(finalLeftIndex + 1, finalLeftIndex + currNameLength));
							String reversedTemp = StringUtils
									.reverse(s.substring(finalLeftIndex + 1, finalLeftIndex + currNameLength));
							String name = namings.get(reversedTemp) == null ? "" : namings.get(reversedTemp);
							
							return name;
						}).collect(Collectors.toList());
						
						b = text.subList(indexOfMidpointRow, text.size()).stream().map(s -> {
							return namings.get(s.substring(finalLeftIndex, finalLeftIndex + currNameLength - 1));
						}).collect(Collectors.joining());

					}
					
					String a1 = a1List.stream().collect(Collectors.joining()); 
					a1 = StringUtils.reverse(a1);
					System.out.println("Comparing a1 and b: " + a1 + ", " + b);
					//kmr.checkCommonPrefixAndStepOverMisMatch(kmr, palindromes, a1, b, true);

					commonPrefix = kmr.LCP(a1, b);	
					if (commonPrefix.length() > 1) {
						palindromes.add(StringUtils.reverse(a1)  + b.substring(1)); // doing substring 1 to not duplicate the midpoint
						int nonZeroBasedStartPosition = leftIndex+1;
						int endPosition = leftIndex + currNameLength;
						System.out.println("**added palindrome for " + a1 + " and " + b + " starting at column " + nonZeroBasedStartPosition + " ending at column: " + endPosition +"; (0-based) index of midpoint row: " + indexOfMidpointRow);

					}
				}
			}
		}
		System.out.println(Arrays.toString(palindromes.toArray()));
	}

	public void checkCommonPrefixAndStepOverMisMatch(KMRHorizontal3 kmr, ArrayList<String> palindromes, String a, String b, boolean doNotDuplicateMidPoint) {
		String commonPrefix = kmr.LCP(a, b);

		int commonPrefixLength = commonPrefix.length();
		if (commonPrefixLength > 0) {
			int aLength = a.length();
			if (aLength != b.length()) {
				System.out.println("!!!!!!!!!!");
			}
			if (commonPrefixLength < aLength) {
				System.out.println("!!!!!!!!!! commonPrefix.length() < a.length()");
				if (aLength == commonPrefixLength+1) {
					// 1 off
					palindromes.add(StringUtils.reverse(a) + (doNotDuplicateMidPoint? b.substring(1) : b));
					System.out.println(
							"***commonPrefix: " + commonPrefix + " added palindrome for " + a + " and " + b);
				}
				String a1 = a.substring(commonPrefixLength+1); // 1=skip over the 1char difference
				String b1 = b.substring(commonPrefixLength+1);
				System.out.println("Comparing a1 and b1: " + a1 + ", " + b1 + " from  " + a + ", " + b);

				String commonPrefix1 = kmr.LCP(a1, b1);
				if (commonPrefix1.length() > 0) {
					palindromes.add(StringUtils.reverse(a1) + (doNotDuplicateMidPoint? b.substring(1) : b));
					System.out.println(
							"***commonPrefix: " + commonPrefix1 + " added palindrome for " + a1 + " and " + b1);
				}

			}
			palindromes.add(StringUtils.reverse(a) + (doNotDuplicateMidPoint? b.substring(1) : b));
			System.out.println(
					"***commonPrefix: " + commonPrefix + " added palindrome for " + a + " and " + b);
		}
	}
	
	public String LCP(String a, String b) {
		String commonPrefix = StringUtils.getCommonPrefix(a, b);
		return commonPrefix;
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

	private ArrayList<String> getTextAsList(String filename) {
		ArrayList<String> text = new ArrayList<>();
		try {
			// File(args[0]);
			text = readText(new File(filename));
		} catch (Exception e) {
			System.out.println(
					"ERROR - Command line args are problematic. Please provide a filename and width as integer for naming.");
			System.exit(1);
		}
		return text;
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
		System.out.println("processing substrings of length " + width);

		for (int i = 0; i < numRows; i++)
			for (int j = 0; j < numCols - width + 1; j++) {
				String substr = text.get(i).substring(j, j + width);

				System.out.println("i= " + i + " j= " + j + " substr=" + substr);
				// use substr as key
				String /* char */ c;
				if (!map.containsKey(substr)) {

					map.put(substr, String.valueOf(nextName));
					c = /* (char) */ String.valueOf(nextName);
					System.out.println("map did not contain " + substr + " new name is " + c);
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
		map.forEach((k, v) -> System.out.println(String.format("%s --> %s", k, v/*.replace("LATIN CAPITAL LETTER", "")*/)));
		// Display text of Names
		System.out.println("The following is the text of names: ");
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols - width + 1; j++) {
				System.out.print(textNames[i][j] + " ");
			}
			System.out.println();
		}

		return nextName;
	}

	private ArrayList<String> readText(File infile) throws FileNotFoundException {
		ArrayList<String> text = new ArrayList<>();
		Scanner sc = new Scanner(infile);

		while (sc.hasNext()) {
			String nextLine = sc.nextLine();
			// remove all whitespace from input line
			nextLine = nextLine.replaceAll("\\s", "");
			text.add(nextLine);
		}
		sc.close();
		return text;
	}

}


