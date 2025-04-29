import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MySetPixels {
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

        int block = size / numThreads;
        int from = 0;
        int to = 0;

        long start = System.currentTimeMillis();

        int redShift = 100;
        int greenShift = 100;
        int blueShift = 100;

        MySetPixelsGroupThread threads[] = new MySetPixelsGroupThread[numThreads];

        // thread execution
        for (int i = 0; i < numThreads; i++) {
            from = i * block;
            to = i * block + block;
            if (i == (numThreads - 1))
                to = size;
            threads[i] = new MySetPixelsGroupThread(from, to, img, redShift, greenShift, blueShift);
            threads[i].start();
        }

        // wait for threads to terminate
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
            }
        }

        long elapsedTimeMillis = System.currentTimeMillis() - start;

        try {
            File file = new File(fileNameW);
            ImageIO.write(img, "jpg", file);
        } catch (IOException e) {
        }

        System.out.println("Done...");
        System.out.println("time in ms = " + elapsedTimeMillis);
    }
}

class MySetPixelsGroupThread extends Thread {
    int myfrom;
    int myto;
    BufferedImage img;
    int redShift, greenShift, blueShift;

    // constructor
    public MySetPixelsGroupThread(int from, int to, BufferedImage img, int redShift, int greenShift, int blueShift) {
        myfrom = from;
        myto = to;
        this.img = img;
        this.redShift = redShift;
        this.greenShift = greenShift;
        this.blueShift = blueShift;
    }

    // thread code
    public void run() {
        for (int y = myfrom; y < myto; y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                // Retrieving contents of a pixel
                int pixel = img.getRGB(x, y);
                // Creating a Color object from pixel value
                Color color = new Color(pixel, true);
                // Retrieving the R G B values, 8 bits per r,g,b
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                // Modifying the RGB values
                red = (red + redShift) % 256;
                green = (green + greenShift) % 256;
                blue = (blue + blueShift) % 256;
                // Creating new Color object
                color = new Color(red, green, blue);
                // Setting new Color object to the image
                img.setRGB(x, y, color.getRGB());
            }
        }

    }
}