
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
    public Tensor getSubTensor(int... indices) {
        int[] subShape = new int[indices.length];
        for (int i = 0; i < indices.length; i++) {
            subShape[i] = shape[indices[i]];
        }
        Tensor subTensor = new Tensor(subShape);
        int[] subIndices = new int[indices.length];
        for (int i = 0; i < data.length; i++) {
            calculateSubIndices(i, indices, subIndices);
            Object value = get(subIndices);
            subTensor.set(value, subIndices);
        }
        return subTensor;
    }

    private void calculateSubIndices(int linearIndex, int[] indices, int[] subIndices) {
        int remainingIndex = linearIndex;
        for (int i = indices.length - 1; i >= 0; i--) {
            int dimSize = shape[i];
            subIndices[i] = remainingIndex % dimSize;
            remainingIndex /= dimSize;
        }
    }

    // 对张量进行填充，在最后两个维度的位置扩展的大小设置为0
    public void pad(int padding) {
        // 在增加维度之前，建立一个数组，用来保存原来的data
        Object[] oldData = new Object[data.length];
        for (int i = 0; i < oldData.length; i++) {
            oldData[i] = data[i];
        }

        // 将原来的shape的最后两个维度加上padding
        shape[shape.length - 1] = shape[shape.length - 1] + 2 * padding;
        shape[shape.length - 2] = shape[shape.length - 2] + 2 * padding;
        // 重新计算data的大小
        data = new Object[calculateSize(this.shape)];

        int dimensions = shape.length;
        // 初始化，indices中的每个元素都是0
        int[] indices = new int[dimensions];
        // temp是保存每一次循环的所有索引，用来使用函数get()
        int[] temp = new int[dimensions];
        int count = 0;
        while (true) {
            // 执行循环体操作,每次循环结束都是一个多维数组的索引
            for (int i = 0; i < dimensions; i++) {
                temp[i] = indices[i];
            }
            // 此时的temp中保存的是每一次循环的所有索引
            // 开始判断位置，如果在padding的范围内，设置为0

            if (indices[dimensions - 2] < padding || indices[dimensions - 2] >= shape[shape.length - 2] - padding
                    || indices[dimensions - 1] < padding
                    || indices[dimensions - 1] >= shape[dimensions - 1] - padding) {
                this.set(0, temp);
            } else {
                // 事实上，如果是原先的值，调用的顺序和原来是一样的

                this.set(oldData[count], temp);
                // System.out.println("在范围内：" + get(temp));
                count++;
                // temp[dimensions - 2] -= padding;
                // temp[dimensions - 1] -= padding;
                // System.out.println("在范围外：" + temp[dimensions - 2] + " " + temp[dimensions -
                // 1]);
                // this.set(this.get(temp), indices);
                // System.out.println(this.get(indices));

            }
            // 更新索引
            // 确保从最后一维度开始改变索引
            int j = dimensions - 1;
            while (j >= 0 && indices[j] == shape[j] - 1) {
                indices[j] = 0;
                j--;
            }
            if (j < 0) {
                break;
            }
            // 一开始都是0，将当前索引加1，直至值为loopCounts-1
            indices[j]++;
        }
        // this.data=result.data;
    }

    // 拉伸：将一个张量的后两个维度拉伸到指定大小。
    // 用拉伸前的像素值计算拉伸后的像素值，比如平均值
    public void stretch(int height, int width) {
        // 在增加维度之前，建立一个数组，用来保存原来的data
        // 将data分为每个二维数组的值，方便求平均值
        int times = data.length / (shape[shape.length - 1] * shape[shape.length - 2]);
        int everySize = shape[shape.length - 1] * shape[shape.length - 2];
        int[] average = new int[times];
        // System.out.println("average.length" + average.length);
        Object[] oldData = new Object[data.length];
        for (int i = 0; i < times; i++) {
            int sum = 0;
            for (int j = 0; j < everySize; j++) {
                oldData[i * everySize + j] = data[i * everySize + j];
                sum += (Integer) data[i * everySize + j];
            }
            average[i] = sum / everySize;
            System.out.println("average[i]" + average[i]);
        }
        int paddingHeight = height / 2;
        int paddingWidth = width / 2;
        // 注意！认为高度在前，宽度在后
        shape[shape.length - 2] = height;
        shape[shape.length - 1] = width;
        // 重新计算data的大小
        data = new Object[calculateSize(this.shape)];
        int dimensions = shape.length;
        // 初始化，indices中的每个元素都是0
        int[] indices = new int[dimensions];
        // temp是保存每一次循环的所有索引，用来使用函数get()
        int[] temp = new int[dimensions];
        int sizeNow = height * width;
        int count = 0;
        int color = 0;
        while (true) {
            // 执行循环体操作,每次循环结束都是一个多维数组的索引
            for (int i = 0; i < dimensions; i++) {
                temp[i] = indices[i];
            }
            // 此时的temp中保存的是每一次循环的所有索引
            // 开始判断位置，如果在padding的范围内，设置为0

            if (indices[dimensions - 2] < paddingHeight
                    || indices[dimensions - 2] >= shape[shape.length - 2] - paddingHeight
                    || indices[dimensions - 1] < paddingWidth
                    || indices[dimensions - 1] >= shape[dimensions - 1] - paddingWidth) {
                // 每一张二维数组的周围都填充color的某个元素的值
                this.set(average[color / sizeNow], temp);
                // color++;现在的everysize是二维数组现在的大小，不是这个循环进行的大小
            } else {
                // 事实上，如果是原先的值，调用的顺序和原来是一样的

                this.set(oldData[count], temp);
                // System.out.println("在范围内：" + get(temp));
                count++;
                // temp[dimensions - 2] -= padding;
                // temp[dimensions - 1] -= padding;
                // System.out.println("在范围外：" + temp[dimensions - 2] + " " + temp[dimensions -
                // 1]);
                // this.set(this.get(temp), indices);
                // System.out.println(this.get(indices));

            }
            color++;// 此处进行++保证color的总次数是data的总次数
            // 更新索引
            // 确保从最后一维度开始改变索引
            int j = dimensions - 1;
            while (j >= 0 && indices[j] == shape[j] - 1) {
                indices[j] = 0;
                j--;
            }
            if (j < 0) {
                break;
            }
            // 一开始都是0，将当前索引加1，直至值为loopCounts-1
            indices[j]++;
        }

    }

    public static void main(String[] args) {

        Tensor tensor = new Tensor(2, 3, 2);
        tensor.set(10, 0, 0, 0);
        tensor.set(20, 0, 0, 1);
        tensor.set(30, 0, 1, 0);
        tensor.set(40, 0, 1, 1);
        tensor.set(50, 0, 2, 0);
        tensor.set(60, 0, 2, 1);
        tensor.set(70, 1, 0, 0);
        tensor.set(80, 1, 0, 1);
        tensor.set(90, 1, 1, 0);
        tensor.set(100, 1, 1, 1);
        tensor.set(110, 1, 2, 0);
        tensor.set(120, 1, 2, 1);
        Tensor other = new Tensor(2, 3, 2);
        other.set(1, 0, 0, 0);
        other.set(2, 0, 0, 1);
        other.set(3, 0, 1, 0);
        other.set(4, 0, 1, 1);
        other.set(5, 0, 2, 0);
        other.set(6, 0, 2, 1);
        other.set(7, 1, 0, 0);
        other.set(8, 1, 0, 1);
        other.set(9, 1, 1, 0);
        other.set(10, 1, 1, 1);
        other.set(11, 1, 2, 0);
        other.set(12, 1, 2, 1);
        Tensor result = tensor.add(other);

        System.out.println(result.get(0, 0, 0)); // Output: 11
        System.out.println(result.get(1, 2, 0)); // Output: 121

        Tensor result2 = tensor.sub(other);
        System.out.println(result2.get(0, 0, 0)); // Output: 9
        System.out.println(result2.get(1, 2, 0)); // Output: 99

        tensor.pad(1);
        System.out.println(tensor.get(0, 0, 0));// Output: 0
        System.out.println(tensor.get(0, 1, 1));// Output: 10
        System.out.println(tensor.get(1, 2, 0));// Output: 0

        tensor.stretch(10, 10);
        System.out.println(tensor.get(0, 0, 0));// Output: 35
        System.out.println(tensor.get(1, 9, 9));// Output: 95

        /*
         * Tensor2 result3 = tensor.pad(2);
         * System.out.println(result3.get(0, 0)); // Output: 1
         * System.out.println(result3.get(1, 2)); // Output: 6
         * System.out.println(result3.get(0, 1)); // Output: 0
         */

    }

}