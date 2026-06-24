package com.honor.blitzremake.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {

	public static String loadAsString(String file) {

		String result = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = reader.readLine()) != null) {
				result += line + "\n";
			}

			reader.close();

		} catch (IOException e) {
			System.err.println("File couldn't found: " + file);
			e.printStackTrace();
		}

		return result;
	}

}
