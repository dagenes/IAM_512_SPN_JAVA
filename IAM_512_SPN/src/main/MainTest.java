/**
* COPYRIGHT : yesterday is yesterday, today is today.
*/
package main;

public class MainTest {

	public static void main(String[] args) {

		Utility utility = new Utility();
		
		//random key
		String key = utility.generateRandomKey();
		System.out.println("Random key is " + key);
		System.out.println(key.length());
		SPN spn = new SPN(false);
		
		
		LinearCryptanalysis linear = new LinearCryptanalysis(spn, 10000, true);
		linear.attack(key);
		
		
//		String pt = "1000";
//		String ct = spn.encrypt(pt, key);
//		System.out.println("\nciphertext is " + ct);
		
	}

}
