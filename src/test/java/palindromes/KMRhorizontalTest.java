package palindromes;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.Test;

public class KMRhorizontalTest {
	@Test
	public void testPalindromes() throws URISyntaxException {
		URL resource = KMRhorizontal.class.getResource("/kmrarray1.txt");
		String absolutePath = Paths.get(resource.toURI()).toString();
		KMRhorizontal kmr = new KMRhorizontal(absolutePath);
		String[] expectedPalindromes = new String[] {"JHIIHJ"};
		String[] actualPalindromes = kmr.findPalindromes();
		assertArrayEquals(expectedPalindromes, actualPalindromes);
	}
}
