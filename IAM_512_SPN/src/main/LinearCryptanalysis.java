/**
* COPYRIGHT : yesterday is yesterday, today is today.
*/
package main;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author HC
 * @date 19 Nis 2020
 * @project_name IAM_512_SPN
 */
public class LinearCryptanalysis {

	private SPN spn;

	private int iterNumber;

	private boolean verbose;

	public LinearCryptanalysis(SPN spn, int iterNumber, boolean verbose) {
		super();
		this.spn = spn;
		this.iterNumber = iterNumber;
		this.verbose = verbose;
	}

	public void attack(String key) {

		Utility utility = new Utility();
		Map<Integer, Integer> sBox = spn.getsBox();
		Map<Integer, Integer> sBoxInverse = spn.getsBoxInverse();

		// TODO iyileþtirilebilir
		String key_5 = key.substring(key.length() - 4);
		int key_5_5_8 = key_5.charAt(1);
		int key_5_13_16 = key_5.charAt(3);

		if (verbose)
			System.out.println("key is " + key);
		if (verbose)
			System.out.println("key_5 is " + key_5);
		if (verbose)
			System.out.println("key_5_5_8 is " + key_5_5_8);
		if (verbose)
			System.out.println("key_5_13_16 is " + key_5_13_16);

		// init prob bias matrix via sbox
		int[][] probBias = initProbBias(sBox);

		// initialize with all zeros
		int[] countTargetBias = new int[256];
		for (int i = 0; i <= 0 + iterNumber; i++) {

			String plain = utility.intToHex(i);
			String cipher = spn.encrypt(i, key);
			int cipher_5_8 = Integer.parseInt(cipher.substring(1, 2), 16);
			int cipher_13_16 = Integer.parseInt(cipher.substring(3, 4), 16);// utilityintToHex(cipher.charAt(3));

//			if (verbose)
//				System.out.println(
//						"###### " + i + " " + utility.hexToInt(cipher) + " " + cipher_5_8 + " " + cipher_13_16);

			for (int j = 0; j < 256; j++) {

				String target = String.format("%02X", j);
				int target_5_8 = Integer.parseInt(target.substring(0, 1), 16);
				int target_13_16 = Integer.parseInt(target.substring(1, 2), 16);

				int v_5_8 = cipher_5_8 ^ target_5_8;
				int v_13_16 = cipher_13_16 ^ target_13_16;

				int u_5_8 = sBoxInverse.get(v_5_8);
				int u_13_16 = sBoxInverse.get(v_13_16);

				int lApprox = ((u_5_8 >> 2) & 0b1) ^ (u_5_8 & 0b1) ^ ((u_13_16 >> 2) & 0b1) ^ (u_13_16 & 0b1)
						^ ((i >> 11) & 0b1) ^ ((i>>9) & 0b1) ^ ((i >> 8) & 0b1);
				if (lApprox == 0)
					countTargetBias[j] += 1;
//				if (verbose)
//					System.out.println("       ******* " + j + " " + target_5_8 + " " + target_13_16 + " " + v_5_8 + " "
//							+ v_13_16 + " " + u_5_8 + " " + u_13_16 + " " + lApprox);
			}
		}
		
		double[] bias = new double[countTargetBias.length];
		for (int i = 0; i < countTargetBias.length; i++) {
			int lAprx = countTargetBias[i];
			bias [i] = Math.abs((lAprx-iterNumber/2.0)/iterNumber);
		}
		
		double maxResult = 0;
		int maxIndex = 0;
		for (int i = 0; i < bias.length; i++) {
			if(bias[i]>maxResult) {
				maxResult = bias[i];
				maxIndex = i;
			}
		}
		
		if (verbose) 
			System.out.println("Highest bias is " + maxResult + ", subkey is " + utility.intToHex(maxIndex));
		
		if ((((maxIndex>>4)&0b1111) == key_5_5_8) && ((maxIndex&0b1111) == key_5_13_16))
			System.out.println("Success!");
		else
			System.out.println("Failure");
		
	}

	/**
	 * @param sBox
	 * @return
	 */
	private int[][] initProbBias(Map<Integer, Integer> sBox) {
		Utility utility = new Utility();
		int[][] probBias = new int[16][16];

		for (Entry<Integer, Integer> entry : sBox.entrySet()) {

			int[] key = utility.intToBit(entry.getKey());
			int x1 = key[3];
			int x2 = key[2];
			int x3 = key[1];
			int x4 = key[0];
			int[] value = utility.intToBit(entry.getValue());
			int y1 = value[3];
			int y2 = value[2];
			int y3 = value[1];
			int y4 = value[0];

			int[] eq_in = new int[] { 0, x4, x3, x3 ^ x4, x2, x2 ^ x4, x2 ^ x3, x2 ^ x3 ^ x4, x1, x1 ^ x4, x1 ^ x3,
					x1 ^ x3 ^ x4, x1 ^ x2, x1 ^ x2 ^ x4, x1 ^ x2 ^ x3, x1 ^ x2 ^ x3 ^ x4 };

			int[] eq_out = new int[] { 0, y4, y3, y3 ^ y4, y2, y2 ^ y4, y2 ^ y3, y2 ^ y3 ^ y4, y1, y1 ^ y4, y1 ^ y3,
					y1 ^ y3 ^ y4, y1 ^ y2, y1 ^ y2 ^ y4, y1 ^ y2 ^ y3, y1 ^ y2 ^ y3 ^ y4 };

			if (verbose) {
				System.out.println(x1 + " " + x2 + " " + x3 + " " + x4);
				System.out.println(y1 + " " + y2 + " " + y3 + " " + y4);
				System.out.println(Arrays.toString(eq_in));
				System.out.println(Arrays.toString(eq_out));
				System.out.println();
			}
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					if (eq_in[i] == eq_out[j])
						probBias[i][j]++;
				}
			}
		}

		if (verbose) {
			// clone this array for print.
			int[][] clone = probBias.clone();
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					clone[i][j] -= 8;
				}
			}
			for (int[] is : clone) {
				System.out.println(Arrays.toString(is));
			}
		}

		return probBias;
	}

}
