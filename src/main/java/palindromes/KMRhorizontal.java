package palindromes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
	final int palindromeMaxLength = 3;
	
	public static void main(String[] args) throws IOException, URISyntaxException {
//		new KMRhorizontal("absolute/file/path goes here").findPalindromes();	
		URL resource = KMRhorizontal.class.getResource("/kmrarray.txt");
		String absolutePath = Paths.get(resource.toURI()).toString();
		new KMRhorizontal(absolutePath).findPalindromes();	
	}
	
	public KMRhorizontal(String filePath) {
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
		palindromes = new ArrayList<String>();
		
	}
	
	//main method
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
					// 2d. Add all palindromes within this rectangle area to palindrome list
					searchRectangleForPalindrome(leftIndex, currPalindromeLength, indexOfMidpointRow);
				}
			}
		}
		System.out.println(Arrays.toString(palindromes.toArray()));
		return palindromes.toArray(new String[0]);				
	}
	
	// for now assume only mismatches of only 1 are allowed, can refactor later
	private void searchRectangleForPalindrome(int leftIndex, int currPalindromeLength,
			int indexOfMidpointRow) {
		//these need to be refactored into one method, called once for even and once for odd
		
		lookForEvenLengthPalindromes(leftIndex, currPalindromeLength, indexOfMidpointRow);

		lookForOddLengthPalindromes(leftIndex, currPalindromeLength, indexOfMidpointRow);
	}

	private void lookForEvenLengthPalindromes(int leftIndex, int currPalindromeLength, int indexOfMidpointRow) {
		List<String> aRows = text.subList(0, indexOfMidpointRow);
		System.out.println(aRows);
		List<String> listA = aRows.stream().map(s -> {
			String temp = s.substring(leftIndex, leftIndex + currPalindromeLength);
			System.out.println("looking for name for reverses of: " + temp);
			String reversedTemp = StringUtils.reverse(temp);
			String name = StringUtils.defaultIfEmpty(namings.get(reversedTemp), "");
			return name;
		}).collect(Collectors.toList());
		// b's left index must be updated if a's index is

		List<String> bRows = text.subList(indexOfMidpointRow, numRows);
		System.out.println(bRows);
		String b = bRows.stream().map(s -> {
			return namings.get(s.substring(leftIndex, leftIndex + currPalindromeLength));
		}).collect(Collectors.joining());

		// (HG) this part is for mismatches
		if (listA.isEmpty() || listA.contains("")) { // no name for reversed a
			listA = aRows.stream().map(s -> {
				String temp = s.substring(leftIndex+1, leftIndex + currPalindromeLength); // should this be leftIndex +1 on both sides?
				System.out.println("looking for name for reverses of: " + temp);
				String reversedTemp = StringUtils.reverse(temp);
				String name = StringUtils.defaultIfEmpty(namings.get(reversedTemp), "");
				return name;
			}).collect(Collectors.toList());

			b = bRows.stream().map(s -> {
				return namings.get(s.substring(leftIndex, leftIndex + currPalindromeLength - 1)); // why currPalindromeLength - 1?
			}).collect(Collectors.joining());
		}

		String a = listA.stream().collect(Collectors.joining());
		String reversedA = StringUtils.reverse(a);

		System.out.println("Comparing a and b: " + reversedA + ", " + b);
		//kmr.checkCommonPrefixAndStepOverMisMatch(kmr, palindromes, a, b, false);

		String commonPrefix = this.LCP(reversedA, b);

		if (commonPrefix.length() > 0) {
			palindromes.add(StringUtils.reverse(reversedA) + b);
			int nonZeroBasedStartPosition = leftIndex + 1;
			int endPosition = leftIndex + currPalindromeLength;
			System.out.println("**added palindrome for: " + commonPrefix + " added palindrome for " + reversedA + " and " + b
					+ " starting at column " + nonZeroBasedStartPosition + " ending at column: " + endPosition
					+ "; (0-based) index of midpoint row: " + indexOfMidpointRow);
		}
	}
	
	private void lookForOddLengthPalindromes(int leftIndex, int currPalindromeLength, int indexOfMidpointRow) {
		String commonPrefix;
		List<String> a1List = text.subList(0, indexOfMidpointRow + 1).stream().map(s -> {
			String reversedTemp = StringUtils.reverse(s.substring(leftIndex, leftIndex + currPalindromeLength));
			return namings.get(reversedTemp) == null ? "" : namings.get(reversedTemp);
		}).collect(Collectors.toList());
		
		List<String> bRows = text.subList(indexOfMidpointRow, numRows);
		System.out.println(bRows);
		String b = bRows.stream().map(s -> {
			return namings.get(s.substring(leftIndex, leftIndex + currPalindromeLength));
		}).collect(Collectors.joining());

		if (a1List.isEmpty() || a1List.contains("")) {
			a1List = text.subList(0, indexOfMidpointRow + 1).stream().map(s -> {
				String temp = s.substring(leftIndex+1, leftIndex + currPalindromeLength); // should this be leftIndex +1 on both sides
				System.out.println("looking for name for reverses of: " + temp);
				String reversedTemp = StringUtils.reverse(temp);
				String name = StringUtils.defaultIfEmpty(namings.get(reversedTemp), "");
				return name;

			}).collect(Collectors.toList());

			b = text.subList(indexOfMidpointRow, numRows).stream().map(s -> {
				return namings.get(s.substring(leftIndex, leftIndex + currPalindromeLength - 1));
			}).collect(Collectors.joining());

		}

		String a1 = a1List.stream().collect(Collectors.joining());
		a1 = StringUtils.reverse(a1);
		System.out.println("Comparing a1 and b: " + a1 + ", " + b);
		// kmr.checkCommonPrefixAndStepOverMisMatch(kmr, palindromes, a1, b, true);

		commonPrefix = this.LCP(a1, b);
		if (commonPrefix.length() > 1) {
			palindromes.add(StringUtils.reverse(a1) + b.substring(1)); // doing substring 1 to not duplicate the
																		// midpoint
			int nonZeroBasedStartPosition = leftIndex + 1;
			int endPosition = leftIndex + currPalindromeLength;
			System.out.println("**added palindrome for " + a1 + " and " + b + " starting at column "
					+ nonZeroBasedStartPosition + " ending at column: " + endPosition
					+ "; (0-based) index of midpoint row: " + indexOfMidpointRow);

		}
	}

	

//	public void checkCommonPrefixAndStepOverMisMatch(KMRHorizontal3 kmr, ArrayList<String> palindromes, String a, String b, boolean doNotDuplicateMidPoint) {
//		String commonPrefix = kmr.LCP(a, b);
//
//		int commonPrefixLength = commonPrefix.length();
//		if (commonPrefixLength > 0) {
//			int aLength = a.length();
//			if (aLength != b.length()) {
//				System.out.println("!!!!!!!!!!");
//			}
//			if (commonPrefixLength < aLength) {
//				System.out.println("!!!!!!!!!! commonPrefix.length() < a.length()");
//				if (aLength == commonPrefixLength+1) {
//					// 1 off
//					palindromes.add(StringUtils.reverse(a) + (doNotDuplicateMidPoint? b.substring(1) : b));
//					System.out.println(
//							"***commonPrefix: " + commonPrefix + " added palindrome for " + a + " and " + b);
//				}
//				String a1 = a.substring(commonPrefixLength+1); // 1=skip over the 1char difference
//				String b1 = b.substring(commonPrefixLength+1);
//				System.out.println("Comparing a1 and b1: " + a1 + ", " + b1 + " from  " + a + ", " + b);
//
//				String commonPrefix1 = kmr.LCP(a1, b1);
//				if (commonPrefix1.length() > 0) {
//					palindromes.add(StringUtils.reverse(a1) + (doNotDuplicateMidPoint? b.substring(1) : b));
//					System.out.println(
//							"***commonPrefix: " + commonPrefix1 + " added palindrome for " + a1 + " and " + b1);
//				}
//
//			}
//			palindromes.add(StringUtils.reverse(a) + (doNotDuplicateMidPoint? b.substring(1) : b));
//			System.out.println(
//					"***commonPrefix: " + commonPrefix + " added palindrome for " + a + " and " + b);
//		}
//	}

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