package sUtils;

/**
 * Created by Mac on 2015/11/01.
 */
public class RandomStringGenerator {

    public static enum Mode {
        ALPHA, ALPHANUMERIC, NUMERIC
    }

    public static String generateRandomString() throws Exception {

        // Mode mode = AL

        StringBuffer buffer = new StringBuffer();
        String characters = "";

//		switch(mode){
//
//		case ALPHA:
//			characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
//			break;
//
//		case ALPHANUMERIC:
//			characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
//			break;
//
//		case NUMERIC:
//			characters = "1234567890";
//		    break;
//		}

        int length = 10;

        characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

        int charactersLength = characters.length();

        for (int i = 0; i < length; i++) {
            double index = Math.random() * charactersLength;
            buffer.append(characters.charAt((int) index));
        }
        return buffer.toString();
    }
}
