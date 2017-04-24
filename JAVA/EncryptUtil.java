package iot.smartshoes.lbs.service.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author MJYoun
 * @since 2017. 04. 24.
 *
 */
public class EncryptUtil {

	public static String encrypt512(String target) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(target.getBytes());
			byte byteData[] = md.digest();

			// convert the byte to hex format method 1
			StringBuffer hashCodeBuffer = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				hashCodeBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			return hashCodeBuffer.toString();
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		}

		return "";
	}
}
