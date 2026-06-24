package com.honor.blitzremake.util;

import java.util.Random;

public class Rnd {

	public static final Random myRandom = new Random();
	
	public static float nextFloat() {
		return myRandom.nextFloat();
	}
	
}
