import java.util.Arrays;

public class Tensor {
    private int[] shape;
    private Object[] data;

    public Tensor(int... shape) {
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
    public Tensor add(Tensor other) {
        Tensor result = new Tensor(shape);
        result = multiDimensionalLoop(shape, other);
        return result;
    }

    public Tensor multiDimensionalLoop(int[] loopCounts, Tensor other) {
        int dimensions = loopCounts.length;
        // 初始化，indices中的每个元素都是0
        int[] indices = new int[dimensions];
        // temp是保存每一次循环的所有索引，用来使用函数get()
        int[] temp = new int[dimensions];
        Tensor result = new Tensor(shape);
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
    public Tensor sub(Tensor other) {
        Tensor result = new Tensor(shape);
        result = multiDimensionalLoop2(shape, other);
        return result;
    }

    public Tensor multiDimensionalLoop2(int[] loopCounts, Tensor other) {
        int dimensions = loopCounts.length;
        // 初始化，indices中的每个元素都是0
        int[] indices = new int[dimensions];
        // temp是保存每一次循环的所有索引，用来使用函数get()
        int[] temp = new int[dimensions];
        Tensor result = new Tensor(shape);
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

    // 能够对张量进行索引操作，以获取特定的子张量。
    public Tensor2 getSubTensorBySubDimensions(int... subDimensions) {
        int[] subShape = new int[subDimensions.length];
        for (int i = 0; i < subDimensions.length; i++) {
            subShape[i] = shape[shape.length - subDimensions.length + i];
        }
        Tensor2 subTensor = new Tensor2(subShape);
        int[] subIndices = new int[subShape.length];
        for (int i = 0; i < data.length; i++) {
            calculateSubIndicesBySubDimensions(i, subDimensions, subIndices);
            Object value = get(subIndices);
            subTensor.set(value, subIndices);
        }
        return subTensor;
    }

    private void calculateSubIndicesBySubDimensions(int linearIndex, int[] subDimensions, int[] subIndices) {
        int remainingIndex = linearIndex;
        for (int i = subDimensions.length - 1; i >= 0; i--) {
            int dimSize = shape[shape.length - subDimensions.length + i];
            subIndices[i] = remainingIndex % dimSize;
            remainingIndex /= dimSize;
        }
    }

    public static void main(String[] args) {
        Tensor tensor = new Tensor(2, 3);
        tensor.set(1, 0, 0);
        tensor.set(2, 0, 1);
        tensor.set(3, 0, 2);
        tensor.set(4, 1, 0);
        tensor.set(5, 1, 1);
        tensor.set(6, 1, 2);
        Tensor other = new Tensor(2, 3);
        other.set(1, 1, 0);
        other.set(2, 0, 1);
        other.set(3, 0, 2);
        other.set(4, 1, 0);
        other.set(5, 1, 1);
        other.set(6, 1, 2);

        tensor.getSubTensorBySubDimensions(2);
        // 输出子张量的形状
        System.out.println("SubTensor Shape: " + Arrays.toString(tensor.shape));

        // 输出子张量的数据
        for (int i = 0; i < tensor.data.length; i++) {
            System.out.println("SubTensor Data at index " + i + ": " + tensor.data[i]);
        }
    }





        /*Tensor result = tensor.add(other);
        System.out.println(result.get(0, 0)); // Output: 1
        System.out.println(result.get(1, 2)); // Output: 6

        Tensor result2 = tensor.sub(other);
        System.out.println(result2.get(0, 0)); // Output: 1
        System.out.println(result2.get(1, 2)); // Output : 6*/


    /*
     * Tensor2 result3 = tensor.pad(2);
     * System.out.println(result3.get(0, 0)); // Output: 1
     * System.out.println(result3.get(1, 2)); // Output: 6
     * System.out.println(result3.get(0, 1)); // Output: 0
     */


}


