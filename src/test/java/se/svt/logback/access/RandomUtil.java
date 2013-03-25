package se.svt.logback.access;

import java.util.Random;

public class RandomUtil {
	private static Random random = new Random();

	private RandomUtil() {}

	public static int getRandomServerPort() {
		int r = random.nextInt(20000);
		// the first 1024 ports are usually reserved for the OS
		return r + 1024;
	}

	public static int getPositiveInt() {
		int r = random.nextInt();
		if (r < 0) {
			r = -r;
		}
		return r;
	}
}
