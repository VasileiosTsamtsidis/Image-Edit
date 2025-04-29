import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MyRGBtoGrayScale {
	public static void main(String args[]) {

		String fileNameR = null;
		String fileNameW = null;

		/*
		 * You have to change the fileNameR and fileNameW in order for this to work.
		 * Type the pathway where you
		 * have put the project and the image into and then give it a try
		 */
		// (btw George is my fathers Name XD)
		fileNameR = "C:\\Users\\George\\Desktop\\ImageEdit\\original.jpg";
		fileNameW = "C:\\Users\\George\\Desktop\\ImageEdit\\new.jpg";

		// Reading Input file to an image
		BufferedImage img = null;
		try {
			File inputFile = new File(fileNameR);
			if (!inputFile.exists()) {
				System.out.println("File not found: " + fileNameR);
				System.exit(1);
			}
			img = ImageIO.read(inputFile);
			if (img == null) {
				System.out.println("Failed to decode image. Is it a valid .jpg file?");
				System.exit(1);
			}
		} catch (IOException e) {
			System.out.println("IOException while reading file: " + e.getMessage());
			System.exit(1);
		}

		int size = img.getHeight();
		int numThreads = Runtime.getRuntime().availableProcessors();

		// Start timing
		long start = System.currentTimeMillis();

		// Coefficinets of R G B to GrayScale
		double redCoefficient = 0.299;
		double greenCoefficient = 0.587;
		double blueCoefficient = 0.114;

		MyRGBtoGrayScaleGroupThread threads[] = new MyRGBtoGrayScaleGroupThread[numThreads];

		// thread execution
		for (int i = 0; i < numThreads; i++) {
			threads[i] = new MyRGBtoGrayScaleGroupThread(i, numThreads, img, size, redCoefficient, greenCoefficient,
					blueCoefficient);
			threads[i].start();
		}

		// wait for threads to terminate
		for (int i = 0; i < numThreads; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
			}
		}
		// Stop timing
		long elapsedTimeMillis = System.currentTimeMillis() - start;

		// Saving the modified image to Output file
		try {
			File file = new File(fileNameW);
			ImageIO.write(img, "jpg", file);
		} catch (IOException e) {
		}

		System.out.println("Done...");
		System.out.println("time in ms = " + elapsedTimeMillis);
	}
}

class MyRGBtoGrayScaleGroupThread extends Thread {
	private int myStart;
	private int myStop;
	private BufferedImage img;
	private double redCoefficient;
	private double greenCoefficient;
	private double blueCoefficient;

	// constructor
	public MyRGBtoGrayScaleGroupThread(int myId, int numThreads, BufferedImage img, int size, double redCoefficient,
			double greenCoefficient, double blueCoefficient) {
		myStart = myId * (size / numThreads);
		myStop = myStart + (size / numThreads);
		if (myId == (numThreads - 1))
			myStop = size;
		this.img = img;
		this.redCoefficient = redCoefficient;
		this.greenCoefficient = greenCoefficient;
		this.blueCoefficient = blueCoefficient;
	}

	// thread code
	public void run() {
		for (int y = myStart; y < myStop; y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				// Retrieving contents of a pixel
				int pixel = img.getRGB(x, y);
				// Creating a Color object from pixel value
				Color color = new Color(pixel, true);
				// Retrieving the R G B values, 8 bits per r,g,b
				// Calculating GrayScale
				int red = (int) (color.getRed() * redCoefficient);
				int green = (int) (color.getGreen() * greenCoefficient);
				int blue = (int) (color.getBlue() * blueCoefficient);
				// Creating new Color object
				color = new Color(red + green + blue,
						red + green + blue,
						red + green + blue);
				// Setting new Color object to the image
				img.setRGB(x, y, color.getRGB());
			}
		}
	}
}