import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * TinyPicMiner
 * 
 * @author Jayden Weaver
 *
 */
public class TinyPicMiner {

	public static void main(String[] args) {

		final String urlString = "http://tinypic.com/images.php";
		try {
			URL url = new URL(urlString);
			InputStream inStream = url.openStream();
			BufferedReader bufRead = new BufferedReader(new InputStreamReader(inStream));
			ArrayList<String> urlStringList = new ArrayList<String>();
			String currentURL = "";

			// Collect All Image Page URLs.
			while ((currentURL = bufRead.readLine()) != null) {
				if (currentURL.contains("<li><a href=\"http://tinypic.com/view.php?")) {
					urlStringList.add(currentURL);
				}
			}
			inStream.close();
			bufRead.close();

			// Parse All Image Page URLs.
			int imgURLStartingIndex, imgURLEndingIndex;
			ArrayList<String> parsedList = new ArrayList<String>();
			for (String s : urlStringList) {
				// Get Indexes, Dynamically.
				imgURLStartingIndex = s.indexOf("http://tinypic.com/view.php?");
				imgURLEndingIndex = s.indexOf("\">");
				s = s.substring(imgURLStartingIndex, imgURLEndingIndex);
				s = s.replace("#38;", "");
				parsedList.add(s);
			}

			String line = "";
			int directURLStartingIndex, directURLEndingIndex;
			ArrayList<String> directIMGList = new ArrayList<String>();
			for (String s : parsedList) {
				url = new URL(s);
				inStream = url.openStream();
				bufRead = new BufferedReader(new InputStreamReader(inStream));
				while ((line = bufRead.readLine()) != null) {
					if (line.contains("class=\"thickbox\" >")) {
						break;
					}
				}
				// Calculate Index Dynamically.
				directURLStartingIndex = line.indexOf("http");
				directURLEndingIndex = line.indexOf("\" class");
				line = line.substring(directURLStartingIndex, directURLEndingIndex);
				directIMGList.add(line);
			}

			saveImages(directIMGList);

		} catch (Exception e) {
			System.err.println(e);
		}

	}

	/**
	 * Saves the photos from an arraylist.
	 * 
	 * @param imageList
	 */
	public static void saveImages(ArrayList<String> imageList) {
		if (imageList.isEmpty()) {
			System.err.println("No images to save!");
			return;
		}
		// Create the necessary directories.
		File outputFile, directory;
		directory = new File("Saved_Images");
		directory.mkdir();
		String timeFolder = "" + System.currentTimeMillis();
		directory = new File("Saved_Images\\" + timeFolder);
		directory.mkdir();

		for (int k = 0; k < imageList.size(); k++) {
			try {
				URL imageURL = new URL(imageList.get(k));
				BufferedImage photo = ImageIO.read(imageURL);
				if (imageURL.toString().substring(imageURL.toString().length() - 3, imageURL.toString().length())
						.equals("png")) {
					outputFile = new File("Saved_Images\\" + timeFolder + "\\" + k + ".png");
					ImageIO.write(photo, "png", outputFile);
				} else if (imageURL.toString().substring(imageURL.toString().length() - 3, imageURL.toString().length())
						.equals("gif")) {
					outputFile = new File("Saved_Images\\" + timeFolder + "\\" + k + ".gif");
					ImageIO.write(photo, "gif", outputFile);
				} else if (imageURL.toString().substring(imageURL.toString().length() - 3, imageURL.toString().length())
						.equals("jpg")) {
					outputFile = new File("Saved_Images\\" + timeFolder + "\\" + k + ".jpg");
					ImageIO.write(photo, "jpg", outputFile);
				}
				System.out.println("SAVED IMAGE: " + imageURL.toString());
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}

}
