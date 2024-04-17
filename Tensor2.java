
public class Tensor2 {
    private int[] shape;
    private Object[] data;

    public Tensor2(int... shape) {
        this.shape = shape;
        this.data = new Object[calculateSize(shape)];
    }

    private int calculateSize(int[] shape) {
        int size = 1;
        for (int dim : shape) {
            size *= dim;
        }
        return size;
    }

    public Object get(int... indices) {
        int index = calculateIndex(indices, shape, 0);
        return data[index];
    }

    public void set(Object value, int... indices) {
        int index = calculateIndex(indices, shape, 0);
        data[index] = value;
    }

    private int calculateIndex(int[] indices, int[] shape, int depth) {
        if (depth == shape.length - 1) {
            return indices[depth];
        }
        int stride = calculateStride(shape, depth);
        return indices[depth] * stride + calculateIndex(indices, shape, depth + 1);
    }

    private int calculateStride(int[] shape, int depth) {
        int stride = 1;
        for (int i = depth + 1; i < shape.length; i++) {
            stride *= shape[i];
        }
        return stride;
    }

    // 传入另一个tensor类，计算两个tensor类的和
    public Tensor2 add(Tensor2 other) {
        Tensor2 result = new Tensor2(shape);
        result = multiDimensionalLoop(shape, other);
        return result;
    }

    public Tensor2 multiDimensionalLoop(int[] loopCounts, Tensor2 other) {
        int dimensions = loopCounts.length;
        // 初始化，indices中的每个元素都是0
        int[] indices = new int[dimensions];
        // temp是保存每一次循环的所有索引，用来使用函数get()
        int[] temp = new int[dimensions];
        Tensor2 result = new Tensor2(shape);
        while (true) {
            // 执行循环体操作,每次循环结束都是一个多维数组的索引
            for (int i = 0; i < dimensions; i++) {
                temp[i] = indices[i];
            }
            // 此时的temp中保存的是每一次循环的所有索引，使用get方法进行比较
            Object value = (Integer) get(temp) + (Integer) other.get(temp);
            result.set(value, temp);
            // 更新索引
            // 确保从最后一维度开始改变索引
            int j = dimensions - 1;
            while (j >= 0 && indices[j] == loopCounts[j] - 1) {
                indices[j] = 0;
                j--;
            }
            if (j < 0) {
                break;
            }
            // 一开始都是0，将当前索引加1，直至值为loopCounts-1
            indices[j]++;
        }
        return result;
    }

    // 传入另一个tensor类，计算两个tensor类的差
    public Tensor2 sub(Tensor2 other) {
        Tensor2 result = new Tensor2(shape);
        result = multiDimensionalLoop2(shape, other);
        return result;
    }

    public Tensor2 multiDimensionalLoop2(int[] loopCounts, Tensor2 other) {
        int dimensions = loopCounts.length;
        // 初始化，indices中的每个元素都是0
        int[] indices = new int[dimensions];
        // temp是保存每一次循环的所有索引，用来使用函数get()
        int[] temp = new int[dimensions];
        Tensor2 result = new Tensor2(shape);
        while (true) {
            // 执行循环体操作,每次循环结束都是一个多维数组的索引
            for (int i = 0; i < dimensions; i++) {
                temp[i] = indices[i];
            }
            // 此时的temp中保存的是每一次循环的所有索引，使用get方法进行比较
            Object value = (Integer) get(temp) - (Integer) other.get(temp);
            result.set(value, temp);
            // 更新索引
            // 确保从最后一维度开始改变索引
            int j = dimensions - 1;
            while (j >= 0 && indices[j] == loopCounts[j] - 1) {
                indices[j] = 0;
                j--;
            }
            if (j < 0) {
                break;
            }
            // 一开始都是0，将当前索引加1，直至值为loopCounts-1
            indices[j]++;
        }
        return result;
    }
    // Implement other operations like addition, subtraction, padding, and
    // stretching here

    public static void main(String[] args) {
        Tensor2 tensor = new Tensor2(2, 3);
        tensor.set(1, 0, 0);
        tensor.set(2, 0, 1);
        tensor.set(3, 0, 2);
        tensor.set(4, 1, 0);
        tensor.set(5, 1, 1);
        tensor.set(6, 1, 2);
        Tensor2 other = new Tensor2(2, 3);
        other.set(1, 0, 0);
        other.set(2, 0, 1);
        other.set(3, 0, 2);
        other.set(4, 1, 0);
        other.set(5, 1, 1);
        other.set(6, 1, 2);
        Tensor2 result = tensor.add(other);

        System.out.println(result.get(0, 0)); // Output: 1
        System.out.println(result.get(1, 2)); // Output: 6

        Tensor2 result2 = tensor.sub(other);
        System.out.println(result2.get(0, 0)); // Output: 1
        System.out.println(result2.get(1, 2)); // Output: 6
    }

}