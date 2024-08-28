package com.example;

import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BajajHealthApplication {

	// Default JSON file path
	private static final String DEFAULT_JSON_FILE_PATH = "C:\\Users\\videh\\Downloads\\BajajHealthCheck.json";

	public static void main(String[] args) {
		// Check if at least 1 argument is provided
		if (args.length < 1) {
			System.out.println("Usage: java -jar <JAR_FILE> <240347320064> [path to JSON file]");
			return;
		}

		String prnNumber = args[0].trim().toLowerCase();
		String jsonFilePath = (args.length > 1) ? args[1].trim() : DEFAULT_JSON_FILE_PATH;

		// Check if PRN Number is not empty
		if (prnNumber.isEmpty()) {
			System.out.println("PRN Number cannot be empty.");
			return;
		}

		// Find the value associated with the key "destination"
		String destinationValue = findDestinationValue(jsonFilePath);
		if (destinationValue == null) {
			System.out.println("Key 'destination' not found in the JSON file.");
			return;
		}

		// Generate a random 8-character alphanumeric string
		String randomString = generateRandomString(8);

		// Concatenate PRN Number, destination value, and random string
		String concatenatedString = prnNumber + destinationValue + randomString;

		try {
			// Compute MD5 hash
			String md5Hash = computeMD5Hash(concatenatedString);
			// Print result in the format: MD5_HASH;Random_String
			System.out.println(md5Hash + ";" + randomString);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error computing MD5 hash: " + e.getMessage());
		}
	}

	// Function to find the value associated with the key "destination"
	private static String findDestinationValue(String jsonFilePath) {
		try (FileReader reader = new FileReader(jsonFilePath)) {
			JsonElement rootElement = JsonParser.parseReader(reader);
			return findDestinationValueRecursive(rootElement);
		} catch (IOException e) {
			System.out.println("Error reading the JSON file: " + e.getMessage());
			return null;
		}
	}

	// Recursive function to traverse JSON and find the destination value
	private static String findDestinationValueRecursive(JsonElement element) {
		if (element.isJsonObject()) {
			JsonObject jsonObject = element.getAsJsonObject();
			// Check if the current JsonObject has the key "destination"
			if (jsonObject.has("destination")) {
				return jsonObject.get("destination").getAsString();
			}
			// Recursively search through all entries in the JsonObject
			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				String result = findDestinationValueRecursive(entry.getValue());
				if (result != null) {
					return result;
				}
			}
		} else if (element.isJsonArray()) {
			// Recursively search through all elements in the JsonArray
			for (JsonElement child : element.getAsJsonArray()) {
				String result = findDestinationValueRecursive(child);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	// Function to generate a random alphanumeric string of the specified length
	private static String generateRandomString(int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int index = random.nextInt(chars.length());
			sb.append(chars.charAt(index));
		}
		return sb.toString();
	}

	// Function to compute the MD5 hash of the given string
	private static String computeMD5Hash(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] hashBytes = md.digest(input.getBytes());
		StringBuilder sb = new StringBuilder();
		for (byte b : hashBytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}