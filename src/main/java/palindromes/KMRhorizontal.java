package palindromes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class KMRhorizontal {
	static Logger logger = Logger.getLogger(KMRhorizontal.class.getName());
	static ConsoleHandler handler = new ConsoleHandler();
	final ArrayList<String> text;
	final int numRows;
	final int numCols;
	final Map<String, String> namings;
	final ArrayList<String> palindromes;

	// these could be passed in to the constructor
	final int palindromeMinLength = 3;
	final int palindromeMaxLength = 4;
	private Map<String, String> nameToTextMap;

	public static void main(String[] args) throws IOException, URISyntaxException {
//		new KMRhorizontal("absolute/file/path goes here").findPalindromes();	
		URL resource = KMRhorizontal.class.getResource("/kmrarray.txt");
		String absolutePath = Paths.get(resource.toURI()).toString();
		new KMRHorizontalFromGitHub(absolutePath).findPalindromes();
	}

	public KMRHorizontalFromGitHub(String filePath) {
		logger.setLevel(Level.FINE);
		handler.setLevel(Level.INFO); // change to FINE to see output from the creatingNamings method
		logger.addHandler(handler);

		text = getTextAsList(filePath);
		numRows = text.size();
		numCols = text.get(0).length();
		// 1. step 1 create map of names
		namings = new KMRNamings(logger).creatingNamings(text);
		System.out.println("These are all the namings:");
		System.out.println(namings);
		nameToTextMap = new HashMap<>();
		namings.forEach((k, v) -> nameToTextMap.put(v, k));

		palindromes = new ArrayList<String>();

	}

	// main method
	public String[] findPalindromes() {
		// search input array for all maximal palindromes

		// 2a. For each possible length of palindrome
		for (int currPalindromeLength = palindromeMinLength; currPalindromeLength <= palindromeMaxLength; currPalindromeLength++) {
			// 2b. For each column width - oop to go over columns, w width at a time, look
			// at rectangles
			for (int leftIndex = 0; leftIndex <= (numCols - currPalindromeLength); leftIndex++) {
				// 2c. For each row - loop to go over rows 2 and up and treat each as a midpoint
				for (int indexOfMidpointRow = 1; indexOfMidpointRow <= numRows
						- 1; /* Math.ceil( new Double(numRows) / 2); */ indexOfMidpointRow++) {
					for(int rowIndex = indexOfMidpointRow; rowIndex > 1; rowIndex --) {
						
						// 2d. Add all palindromes within this rectangle area to palindrome list
						searchRectangleForPalindrome(leftIndex, currPalindromeLength, indexOfMidpointRow, rowIndex);
					}
				}
			}
		}
		System.out.println(Arrays.toString(palindromes.toArray()));
		return palindromes.toArray(new String[0]);
	}

	// for now assume only mismatches of only 1 are allowed, can refactor later
	private void searchRectangleForPalindrome(int leftIndex, int currPalindromeLength, int indexOfMidpointRow, int rowIndex) {

		lookForPalindromes(leftIndex, currPalindromeLength, indexOfMidpointRow, indexOfMidpointRow, 0, rowIndex, "even");

		lookForPalindromes(leftIndex, currPalindromeLength, indexOfMidpointRow + 1, indexOfMidpointRow, 1, rowIndex, "odd");
	}

	private void lookForPalindromes(int leftIndex, int currPalindromeLength, int aindexOfMidpointRow,
			int bindexOfMidpointRow, int substringIndex, int rowIndex, String descr) {
		List<String> aRows = text.subList(aindexOfMidpointRow - rowIndex/*0*/, aindexOfMidpointRow);
		
		System.out.println(descr + " length, aRows: " + aRows.toString());
		List<String> listA = aRows.stream().map(s -> {
			String temp = s.substring(leftIndex, leftIndex + currPalindromeLength);
			System.out.println("looking for name for reverses of: " + temp);
			String reversedTemp = StringUtils.reverse(temp);
			String name = StringUtils.defaultIfEmpty(namings.get(reversedTemp), "");
			if (name != "") {
				System.out.println("found name: " + name);
			}
			return name;
		}).collect(Collectors.toList());

//		int rightIndex;
//		rightIndex=numRows;
//		System.out.println(
//				"aIndexOfMidpointRow: " + aindexOfMidpointRow + ",  bindexOfMidpointRow: " + bindexOfMidpointRow);

		int rightIndex = Math.min(aindexOfMidpointRow + rowIndex/*2 * aindexOfMidpointRow*/, numRows); // should this be aindexOfMidpointRow
		List<String> bRows = text.subList(bindexOfMidpointRow, rightIndex);
		System.out.println(descr + " length, bRows: " + bRows);
		String b = bRows.stream().map(s -> {
			String temp = s.substring(leftIndex, leftIndex + currPalindromeLength);
			System.out.println("looking for name for  " + temp);
//			String reversedTemp = StringUtils.reverse(temp);
			String name = StringUtils.defaultIfEmpty(namings.get(temp), "");
			if (name != "") {
				System.out.println("found name: " + name);
			}
			return name;

		}).collect(Collectors.joining());

		String a = listA.stream().collect(Collectors.joining());
		String reversedA = StringUtils.reverse(a);
//		String reversedB = StringUtils.reverse(b);

		String commonPrefix = this.LCP(reversedA, b);

		if (/* commonPrefix.length() + commonPrefix2.length() >= a.length() -1 */commonPrefix.length() == a.length()) {
			addPalindrome(leftIndex, aindexOfMidpointRow, currPalindromeLength, b, a, commonPrefix);
		} else {
			// check for one-off
			// 1. get first index of diff between 2 prefixes
			int totalDiffs = getTotalDiffsBetweenStrings(reversedA, b);
			if (totalDiffs == 1) {
				// 2. inspect differences at that location
				int indexOfFirstDiff = StringUtils.indexOfDifference(reversedA, b);
				if (getNumDifferencesOfDivedIntoNames(reversedA.charAt(indexOfFirstDiff), b.charAt(indexOfFirstDiff)) == 1) {
					addPalindrome(leftIndex, aindexOfMidpointRow, currPalindromeLength, b, reversedA, commonPrefix);
				}
			}
		}
	}

	private void addPalindrome(int leftIndex, int indexOfMidpointRow, final int currNameLength, String b, String a,
			String commonPrefix) {
		String originalText = b + " " + a + ":";
		for (int n = 0; n < commonPrefix.length(); n++) {
			originalText += nameToTextMap.get(String.valueOf(commonPrefix.charAt(n)));
		}
		int nonZeroBasedStartPosition = leftIndex + 1;
		int endPosition = leftIndex + currNameLength;
		System.out.println("**added palindrome for: " + commonPrefix + " added palindrome for " + a + " and " + b
				+ " starting at column " + nonZeroBasedStartPosition + " ending at column: " + endPosition
				+ "; (0-based) index of midpoint row: " + indexOfMidpointRow);

		palindromes.add(StringUtils.reverse(originalText) + originalText);
	}

	private int getNumDifferencesOfDivedIntoNames(char a, char b) {
		String aName = nameToTextMap.get(String.valueOf(a));
		String bName = nameToTextMap.get(String.valueOf(b));

		return getTotalDiffsBetweenStrings(aName, bName);

	}

	private int getTotalDiffsBetweenStrings(String a, String b) {

		int differences = 0;
		char[] charDifferences = new char[a.length()];
		for (int i = 0; i < a.length() && i < b.length(); i++) {
			if (a.charAt(i) != b.charAt(i)) {
				differences++;
				charDifferences[i] = a.charAt(i);
			}
		}
		return differences;
	}

	private int getTotalDiffsBetweenStrings(String a, String b) {

		int differences = 0;
		char[] charDifferences = new char[a.length()];
		for (int i = 0; i < a.length() && i < b.length(); i++) {
			if (a.charAt(i) != b.charAt(i)) {
				differences++;
				charDifferences[i] = a.charAt(i);
			}
		}
//		System.out.println("getTotalDiffsBetweenStrings a: " + a + " b: " + b + " differences: " + Arrays.toString(charDifferences));
		return differences;
	}

	public String LCP(String a, String b) {
		String commonPrefix = StringUtils.getCommonPrefix(a, b);
		return commonPrefix;
	}

	private static ArrayList<String> getTextAsList(String filename) {
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

	private static ArrayList<String> readText(File infile) throws FileNotFoundException {
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
