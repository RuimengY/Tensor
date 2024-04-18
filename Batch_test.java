package Lab6;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Batch_test {
    private Tensor kernel = new Tensor();
    private Tensor photoes = new Tensor();

    public void getKernal() {

        try {
            BufferedReader reader = new BufferedReader(new FileReader("sample_conv_kernel.txt"));
            String line = reader.readLine();
            String[] numbers = line.split(" ");
            int num1 = Integer.parseInt(numbers[0]);
            int num2 = Integer.parseInt(numbers[1]);
            int num3 = Integer.parseInt(numbers[2]);
            int num4 = Integer.parseInt(numbers[3]);
            kernel = new Tensor(num1, num2, num3, num4);
            // 从第二行开始读取num1*num2*num3*num4个数据作为tensor的data值，一行有num4个数据，数据之间用空格隔开
            for (int i = 0; i < kernel.getShape(0); i++) {
                for (int j = 0; j < kernel.getShape(1); j++) {
                    for (int k = 0; k < kernel.getShape(2); k++) {
                        line = reader.readLine();
                        String[] color = line.split(" ");
                        for (int l = 0; l < kernel.getShape(3); l++) {

                            kernel.set(Integer.parseInt(color[l]), i, j, k, l);
                        }
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(kernel.get(0, 1, 1, 1));
    }

    public void readImages() {
        try {
            // Iterate over the subdirectories in the ./figs/ directory
            File figsDir = new File("sample");
            File[] subdirectories = figsDir.listFiles(File::isDirectory);
            for (File subdirectory : subdirectories) {
                // Get the images in the subdirectory
                File[] images = subdirectory.listFiles();
                // 确定图片的高度和宽度
                BufferedImage im = ImageIO.read(images[0]);
                int height = im.getHeight();
                int width = im.getWidth();
                // 对photoes进行初始化，其shape为[images.length, 3, height, width]
                photoes = new Tensor(images.length, 3, height, width);
                // 将每一个image的数据读取到photoes中(注意有三个通道，每个通道都有数组)
                for (int i = 0; i < images.length; i++) {
                    BufferedImage image = ImageIO.read(images[i]);
                    // Store the pixel values of the image in the tensor
                    for (int row = 0; row < height; row++) {
                        for (int col = 0; col < width; col++) {
                            // 分别获得红绿蓝三种颜色的rgb值并存储于photoes中
                            int rgb = image.getRGB(col, row);
                            int red = (rgb >> 16) & 0xFF;
                            int green = (rgb >> 8) & 0xFF;
                            int blue = rgb & 0xFF;

                            // Set the pixel values in the tensor
                            photoes.set(red, i, 0, row, col);
                            photoes.set(green, i, 1, row, col);
                            photoes.set(blue, i, 2, row, col);
                        }
                    }
                }
                System.out.println(photoes.get(0, 0, 0, 0));
                System.out.println(photoes.get(0, 1, 0, 0));
                System.out.println(photoes.get(0, 2, 0, 0));
                // Process the imagesTensor as needed

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Batch_test test = new Batch_test();
        test.getKernal();
        test.readImages();
    }

}
