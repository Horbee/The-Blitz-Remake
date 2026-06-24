package com.honor.blitzremake.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileUtils {

	/**
	 * Loads a text resource from the classpath. Resources such as shaders
	 * live under {@code src/main/resources/} and are packaged at the jar
	 * root (e.g. {@code shaders/menu.vs}); reading them via
	 * {@code new FileReader(path)} (CWD-relative) broke when run from a fat
	 * jar or when CWD != project root. Classpath loading works in all cases.
	 *
	 * @param path classpath-relative path, e.g. {@code "shaders/menu.vs"}
	 * @return the file contents with lines joined by {@code \n}
	 */
	public static String loadAsString(String path) {
		StringBuilder result = new StringBuilder();
		try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
			if (in == null) {
				System.err.println("File couldn't be found on classpath: " + path);
				return "";
			}
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					result.append(line).append('\n');
				}
			}
		} catch (IOException e) {
			System.err.println("Could not read resource: " + path);
			e.printStackTrace();
		}
		return result.toString();
	}

}