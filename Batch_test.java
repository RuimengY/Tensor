package Lab6;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Batch_test {
    private Tensor kernel = new Tensor();
    private Tensor photoes = new Tensor();

    public void getKernelFromFile() {

        try {
            BufferedReader reader = new BufferedReader(new FileReader("./conv_kernel.txt"));
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
    }

    public void readImages() {
        try {
            // Iterate over the subdirectories in the ./figs/ directory
            File figsDir = new File("./figs/");
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

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 实现kernel和photoes的卷积运算
    public Tensor conv(int padding, int stride) {
        // 将kernel进行padding处理
        photoes.pad(padding);
        // 计算卷积后的result的各个维数的大小
        int Hout = (photoes.getShape(2) - kernel.getShape(2)) / stride + 1;
        int Wout = (photoes.getShape(3) - kernel.getShape(3)) / stride + 1;
        int num = photoes.getShape(0);
        int channel = kernel.getShape(0);
        Tensor result = new Tensor(num, channel, Hout, Wout);
        for (int i = 0; i < num; i++) { // 遍历每张图片
            for (int j = 0; j < channel; j++) { // 遍历每个输出通道
                for (int m = 0; m < Hout; m++) {
                    for (int n = 0; n < Wout; n++) {
                        int sum = 0;
                        for (int k = 0; k < 3; k++) { // 遍历每个输入通道(反正要红绿蓝三种颜色都加起来)
                            // 将二维数组中的每个点都算出来
                            for (int p = 0; p < kernel.getShape(2); p++) { // 遍历kernel的高
                                for (int q = 0; q < kernel.getShape(3); q++) { // 遍历kernel的宽
                                    int imgRow = m * stride + p;// 此时在photoes中的行数
                                    int imgCol = n * stride + q;// 此时在photoes中的列数
                                    if (imgRow < photoes.getShape(2) && imgCol < photoes.getShape(3)) {
                                        // 由于此时已经确定了是四维数组，循环方便
                                        sum += (int) photoes.get(i, k, imgRow, imgCol) * (int) kernel.get(j, k, p, q);
                                    }
                                }
                            }
                        }
                        // 将三种颜色在该点的和作为result该点的值
                        result.set(sum, i, j, m, n);
                    }
                }
            }
        }
        return result;
    }

    // 将result存储在.txt文件中
    public void saveResult(Tensor result) {
        // Output result to result.txt
        try {
            File resultsDir = new File("./results");
            if (!resultsDir.exists()) {
                resultsDir.mkdir();
            }
            for (int i = 0; i < result.getShape(0); i++) {
                String fileName = String.format("./results/%03d_result.txt", i + 1);
                FileWriter writer = new FileWriter(fileName);
                // 在第一行写入num，channel，Hout，Wout
                writer.write(result.getShape(0) + " " + result.getShape(1) + " " + result.getShape(2) + " "
                        + result.getShape(3) + "\n");
                for (int j = 0; j < result.getShape(1); j++) {
                    for (int k = 0; k < result.getShape(2); k++) {
                        for (int l = 0; l < result.getShape(3); l++) {
                            writer.write(result.get(i, j, k, l) + " ");
                        }
                        writer.write("\n");
                    }
                }
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Batch_test test = new Batch_test();
        test.getKernelFromFile();
        test.readImages();
        Tensor result = test.conv(2, 1);
        test.saveResult(result);

    }
}
