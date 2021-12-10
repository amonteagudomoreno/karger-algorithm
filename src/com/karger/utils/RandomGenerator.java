package src.com.karger.utils;

import java.util.Random;

public class RandomGenerator {

    private static Random rnd;
    private static final char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    public RandomGenerator() {
        rnd = new Random();
    }

    public Random getRnd() {
        return rnd;
    }

    /*
     * Return a random string
	 */
    public String stringRandom() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars[rnd.nextInt(chars.length)]);
        }
        return sb.toString();
    }
}
