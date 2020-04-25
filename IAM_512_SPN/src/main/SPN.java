/**
* COPYRIGHT : yesterday is yesterday, today is today.
*/
package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A class containing simple substituiton-permutation network cipher.
 * 
 * @author HC
 * @date 18 Nis 2020
 * @project_name IAM_512_SPN
 */
public class SPN {

	private Map<Integer, Integer> sBox;
	private Map<Integer, Integer> sBoxInverse;
	private Map<Integer, Integer> pBox;
	private boolean verbose;

	public SPN(boolean verbose) {
		initsBox();
		initpBox();
		this.verbose = verbose;
	}

	private void initpBox() {
		sBox = new HashMap<Integer, Integer>();
		sBoxInverse = new HashMap<Integer, Integer>();

		sBox.put(0, 0xE);
		sBox.put(1, 0x4);
		sBox.put(2, 0xD);
		sBox.put(3, 0x1);
		sBox.put(4, 0x2);
		sBox.put(5, 0xF);
		sBox.put(6, 0xB);
		sBox.put(7, 0x8);
		sBox.put(8, 0x3);
		sBox.put(9, 0xA);
		sBox.put(0xA, 0x6);
		sBox.put(0xB, 0xC);
		sBox.put(0xC, 0x5);
		sBox.put(0xD, 0x9);
		sBox.put(0xE, 0x0);
		sBox.put(0xF, 0x7);
		for (Entry<Integer, Integer> entry : sBox.entrySet()) {
			sBoxInverse.put(entry.getValue(), entry.getKey());
		}
		
		if (verbose) {
			System.out.println("\n-------------------PBOX-------------");
			for (Entry<Integer, Integer> entry : pBox.entrySet()) {
				System.out.println(entry.getKey() + " " + entry.getValue());
			}
			System.out.println("--------------------------------");
		}
	}

	private void initsBox() {
		pBox = new HashMap<Integer, Integer>();
		pBox.put(0, 0);
		pBox.put(1, 4);
		pBox.put(2, 8);
		pBox.put(3, 12);
		pBox.put(4, 1);
		pBox.put(5, 5);
		pBox.put(6, 9);
		pBox.put(7, 13);
		pBox.put(8, 2);
		pBox.put(9, 6);
		pBox.put(0xA, 10);
		pBox.put(0xB, 14);
		pBox.put(0xC, 3);
		pBox.put(0xD, 7);
		pBox.put(0xE, 11);
		pBox.put(0xF, 15);
	}

	/**
	 * @param plainText
	 * @param key
	 * @return
	 */
	public String encrypt(int plainText, String key) {
		int cipherText = plainText;

		if (verbose)
			System.out.println(
					"plaint text is " + plainText );
//		System.out.println(
//				"plaint text is " + plainText + ", byte form is " + Arrays.toString(plainText.getBytes()));

		List<String> subKeys = subKeys(key);
		Utility utility = new Utility();
		// first 3 key-mixing s-box and p-box
		for (int i = 0; i < 3; i++) {

			if (verbose)
				System.out.println("Round " + (i + 1) + " and subkey is " + utility.hexToInt(subKeys.get(i)));
			cipherText = utility.XOR(cipherText, utility.hexToInt(subKeys.get(i)));
			if (verbose)
				System.out.println("XOR " + cipherText + " " + utility.intToHex(cipherText));
			cipherText = performSbox(cipherText, false);
			if (verbose)
				System.out.println("SBOX " + cipherText + " " + utility.intToHex(cipherText));
			cipherText = performPbox(cipherText);
			if (verbose)
				System.out.println("PBOX " + cipherText + " " + utility.intToHex(cipherText));
			if (verbose)
				System.out.println();
		}

		// fourth key-mixing and last s-box
		cipherText = utility.XOR(cipherText, utility.hexToInt(subKeys.get(3)));
		if (verbose)
			System.out.println("4th XOR " + cipherText + " " + utility.intToHex(cipherText));
		cipherText = performSbox(cipherText, false);
		if (verbose)
			System.out.println("Last SBOX " + cipherText + " " + utility.intToHex(cipherText));
		// last key mixing
		cipherText = utility.XOR(cipherText, utility.hexToInt(subKeys.get(4)));
		if (verbose)
			System.out.println("Last XOR " + cipherText + " " + utility.intToHex(cipherText));

		return utility.intToHex(cipherText);
	}

	public void decrypt() {
		// TODO implement if needed
	}

	private List<String> subKeys(String key) {

		List<String> subKeys = new ArrayList<String>();
		for (int i = 0; i < key.length(); i += 4) {
			subKeys.add(key.substring(i, i + 4));
		}

		return subKeys;
	}

	/**
	 * @param plainText
	 * @param isInverse
	 * @return
	 */
	private int performSbox(Integer pt, boolean isInverse) {

		List<Integer> subPlaintTxt = Arrays.asList(pt & 0x000f, (pt & 0x00f0) >> 4, (pt & 0x0f00) >> 8,
				(pt & 0xf000) >> 12);
		for (int i = 0; i < subPlaintTxt.size(); i++) {
			subPlaintTxt.set(i, sBox.get(subPlaintTxt.get(i)));
		}

		return subPlaintTxt.get(0) | subPlaintTxt.get(1) << 4 | subPlaintTxt.get(2) << 8 | subPlaintTxt.get(3) << 12;
	}

	/**
	 * @param pt
	 * @return
	 */
	private int performPbox(Integer pt) {

		Utility utility = new Utility();

		int tmpCipher = 0;
		for (int j = 0; j < 16; j++) {
			if ((pt & (1 << j)) > 0) {
				tmpCipher = utility.OR(tmpCipher, 1 << pBox.get(j));
			}
		}

		return tmpCipher;
	}

	public Map<Integer, Integer> getsBox() {
		return sBox;
	}

	public Map<Integer, Integer> getsBoxInverse() {
		return sBoxInverse;
	}

	public Map<Integer, Integer> getpBox() {
		return pBox;
	}

}
