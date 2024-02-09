import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Arrays;

public class growregion {
    private static int targetValue;
    private static int imageWidth;
    private static int imageHeight;
    private static int[][] imageMatrix;
    private static boolean[][] isVisited;
    private static LinkedList<Integer> regionSizes;

    // Constructor: Initializes the processing of the image.
    public growregion(String filePath, int val) {
        targetValue = val;
        System.out.println("Visho Malla Oli");
        System.out.println(filePath);
        readImage(filePath); // Read and process the PGM image file.
        regionSizes = new LinkedList<>();
    }

    // Reads the PGM image file, validates format, and loads the image data into memory.
    private void readImage(String filePath) {
        try {
            Scanner scanner = new Scanner(new File(filePath));
            
            // Validate the PGM file format and read image dimensions and max value.
            String headerLine = scanner.nextLine().trim();
            String[] headerParts = headerLine.split("\\s+");
            if (!headerParts[0].equals("P2")) {
                throw new IllegalArgumentException("Unsupported file format: " + headerParts[0]);
            }
            
            // Determine if the file follows the expected format and read accordingly.
            if (headerParts.length == 4) { // File includes dimensions and max value on the first line.
                imageWidth = Integer.parseInt(headerParts[1]);
                imageHeight = Integer.parseInt(headerParts[2]);
            } else { // Standard PGM format with dimensions and max value on separate lines.
                imageWidth = scanner.nextInt();
                imageHeight = scanner.nextInt();
                scanner.nextInt(); // Skip max value line.
            }
    
            // Initialize matrices for image data and visited flags.
            imageMatrix = new int[imageHeight][imageWidth];
            isVisited = new boolean[imageHeight][imageWidth];
    
            // Load the image data into the matrix.
            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    imageMatrix[y][x] = scanner.nextInt();
                    isVisited[y][x] = false;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    // Processes the image to find and grow regions based on the target value.
    public void processImage() {
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                if (imageMatrix[y][x] == targetValue && !isVisited[y][x]) {
                    exploreRegion(y, x);
                }
            }
        }
        printResults();
    }

    // Explores a region starting from a seed point, growing it based on the 8-connected neighborhood.
    private void exploreRegion(int startY, int startX) {
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startY, startX});
        isVisited[startY][startX] = true;
        int regionSize = 0;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currentY = current[0];
            int currentX = current[1];
            regionSize++;

            for (int offsetY = -1; offsetY <= 1; offsetY++) {
                for (int offsetX = -1; offsetX <= 1; offsetX++) {
                    if (offsetY == 0 && offsetX == 0) continue; // Skip the current pixel itself.
                    int newY = currentY + offsetY;
                    int newX = currentX + offsetX;
                    if (isValid(newY, newX) && imageMatrix[newY][newX] == targetValue && !isVisited[newY][newX]) {
                        isVisited[newY][newX] = true;
                        queue.add(new int[]{newY, newX});
                    }
                }
            }
        }
        regionSizes.add(regionSize);
    }

    // Validates if a pixel coordinate is within the image bounds.
    private boolean isValid(int y, int x) {
        return y >= 0 && y < imageHeight && x >= 0 && x < imageWidth;
    }

    // Prints the results after processing the image.
    private void printResults() {
        Integer[] sortedRegionSizes = regionSizes.toArray(new Integer[0]);
        Arrays.sort(sortedRegionSizes);
        System.out.printf("%d", regionSizes.size());
        for (int size : sortedRegionSizes) {
            System.out.printf(", %d", size);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            String filePath = args[0];
            int val = Integer.parseInt(args[1]);
            growregion regionGrower = new growregion(filePath, val);
            regionGrower.processImage();
        } else {
            System.out.println("Incorrect number of command line arguments.");
        }
    }
}
