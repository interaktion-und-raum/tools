package de.dennisppaul.ollama;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

import processing.core.PImage;
import org.json.JSONObject;

import javax.imageio.ImageIO;

public class Ollama {
    public static final String MODEL_LLAMA32        = "llama3.2";
    public static final String MODEL_LLAMA32_VISION = "x/llama3.2-vision";
    public static final String IMAGE_FORMAT_PNG     = "png";
    public static final String IMAGE_FORMAT_JPG     = "jpg";

    public static        String model_name       = MODEL_LLAMA32;
    public static        String api_URL          = "http://localhost:11434/api/";
    private static final String api_URL_generate = api_URL + "generate";
    private static final String api_URL_pull     = api_URL + "pull";
//    public static String stream     = "false";

    public static String pull(String model) {
        try {
            // Create the JSON payload
            String jsonPayload = """
                                 {
                                     "model": "%s",
                                     "action": "pull"
                                 }
                                 """.formatted(model);

            // Create a URL object
            URL               url        = new URL(api_URL_pull); // Replace with the actual API URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set up the HTTP connection properties
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Write the JSON payload to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner       scanner  = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                // Print the response
                return response.toString(); // Adjust this if you want to process the response further
            } else {
                System.out.println("HTTP error code: " + responseCode);
            }

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String encodePImageToBase64(PImage image, String format) {
        try {
            // Save PImage to a BufferedImage
            java.awt.image.BufferedImage mBufferedImage = (java.awt.image.BufferedImage) image.getNative();

            // Write to ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(mBufferedImage, format, baos);
            baos.flush();

            // Encode the byte array to Base64
            byte[] imageBytes = baos.toByteArray();
            baos.close();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String encodeImageToBase64(String imagePath) {
        try {
            File            file            = new File(imagePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[]          imageBytes      = fileInputStream.readAllBytes();
            fileInputStream.close();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String extractResponses(String apiResponse) {
        StringBuilder cleanedResponse = new StringBuilder();

        // Split the response into individual JSON objects
        String[] jsonObjects = apiResponse.split("(?<=})\\s*(?=\\{)");
        for (String jsonObject : jsonObjects) {
            try {
                // Parse each JSON object
                JSONObject json = new JSONObject(jsonObject);
                // Append the response field
                if (json.has("response")) {
                    cleanedResponse.append(json.getString("response"));
                }
            } catch (Exception e) {
                System.err.println("Error parsing JSON: " + e.getMessage());
            }
        }
        return cleanedResponse.toString().trim();
    }

    public static String text(String prompt) {
        try {
            // Create the JSON payload
            String jsonPayload = """
                                 {
                                     "model": "%s",
                                     "prompt": "%s"
                                 }
                                 """.formatted(model_name, prompt);

            // Create a URL object
            URL               url        = new URL(api_URL_generate);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set up the HTTP connection properties
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Write the JSON payload to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner       scanner  = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                // Print the response
                return extractResponses(response.toString());
            } else {
                System.out.println("HTTP error code: " + responseCode);
            }

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String image(PImage image) {
        String base64Image = encodePImageToBase64(image, IMAGE_FORMAT_PNG);
        return _image(base64Image);
    }

    public static String image(String image_path) {
        String base64Image = encodeImageToBase64(image_path);
        return _image(base64Image);
    }

    private static String _image(String base64Image) {
        try {
            // Create the JSON payload
            String jsonPayload = """
                                 {
                                     "model": "%s",
                                     "prompt": "Analyze this image:",
                                     "images": ["%s"]
                                 }
                                 """.formatted(model_name, base64Image);

            // Create a URL object
            URL               url        = new URL(api_URL_generate);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set up the HTTP connection properties
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Write the JSON payload to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner       scanner  = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                // Print the response
                return extractResponses(response.toString());
            } else {
                System.out.println("HTTP error code: " + responseCode);
            }

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}