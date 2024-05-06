
public class Tensor {
    private int[] shape;
    private Object[] data;

    public Tensor(int... shape) {
        this.shape = shape;
        this.data = new Object[calculateSize(shape)];
    }

    public Tensor() {

    }

    // 写getShape和setShape方法
    public int getShape(int i) {
        return shape[i];
    }

    public void setShape(int i, int value) {
        shape[i] = value;
    }

    private int calculateSize(int[] shape) {
        int size = 1;
        for (int dim : shape) {
            size *= dim;
        }
        return size;
    }

    // get和set方法可以用来访问和修改数组中的元素
    public Object get(int... indexes) {
        int index = calculateIndex(indexes, shape, 0);
        return data[index];
    }

    public void set(Object value, int... indexes) {
        int index = calculateIndex(indexes, shape, 0);
        data[index] = value;
    }

    private int calculateIndex(int[] indexes, int[] shape, int depth) {
        if (depth == shape.length - 1) {
            return indexes[depth];
        }
        int stride = calculateStride(shape, depth);
        return indexes[depth] * stride + calculateIndex(indexes, shape, depth + 1);
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
        result = addLoop(shape, other);
        return result;
    }

    public Tensor addLoop(int[] sumSize, Tensor other) {
        int dimensions = sumSize.length;
        // 初始化，indices中的每个元素都是0
        int[] indexes = new int[dimensions];
        // temp是保存每一次循环的所有索引，用来使用函数get()
        int[] temp = new int[dimensions];
        Tensor result = new Tensor(shape);
        while (true) {
            // 执行循环体操作,每次循环结束都是一个多维数组的索引
            for (int i = 0; i < dimensions; i++) {
                temp[i] = indexes[i];
            }
            // 此时的temp中保存的是每一次循环的所有索引，使用get方法进行比较
            Object value = (Integer) get(temp) + (Integer) other.get(temp);
            result.set(value, temp);
            // 更新索引
            // 确保从最后一维度开始改变索引
            int j = dimensions - 1;
            while (j >= 0 && indexes[j] == sumSize[j] - 1) {
                indexes[j] = 0;
                j--;
            }
            if (j < 0) {
                break;
            }
            // 一开始都是0，将当前索引加1，直至值为sumSize-1
            indexes[j]++;
        }
        return result;
    }

    // 传入另一个tensor类，计算两个tensor类的差
    public Tensor sub(Tensor other) {
        Tensor result = new Tensor(shape);
        result = subLoop(shape, other);
        return result;
    }

    public Tensor subLoop(int[] loopCounts, Tensor other) {
        int dimensions = loopCounts.length;
        // 初始化，indexes中的每个元素都是0
        int[] indexes = new int[dimensions];
        // temp是保存每一次循环的所有索引，用来使用函数get()
        int[] temp = new int[dimensions];
        Tensor result = new Tensor(shape);
        while (true) {
            // 执行循环体操作,每次循环结束都是一个多维数组的索引
            for (int i = 0; i < dimensions; i++) {
                temp[i] = indexes[i];
            }
            // 此时的temp中保存的是每一次循环的所有索引，使用get方法进行比较
            Object value = (Integer) get(temp) - (Integer) other.get(temp);
            result.set(value, temp);
            // 更新索引
            // 确保从最后一维度开始改变索引
            int j = dimensions - 1;
            while (j >= 0 && indexes[j] == loopCounts[j] - 1) {
                indexes[j] = 0;
                j--;
            }
            if (j < 0) {
                break;
            }
            // 一开始都是0，将当前索引加1，直至值为loopCounts-1
            indexes[j]++;
        }
        return result;
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
        // 初始化，indexes中的每个元素都是0
        int[] indexes = new int[dimensions];
        // temp是保存每一次循环的所有索引，用来使用函数get()
        int[] temp = new int[dimensions];
        int count = 0;
        while (true) {
            // 执行循环体操作,每次循环结束都是一个多维数组的索引
            for (int i = 0; i < dimensions; i++) {
                temp[i] = indexes[i];
            }
            // 此时的temp中保存的是每一次循环的所有索引
            // 开始判断位置，如果在padding的范围内，设置为0

            if (indexes[dimensions - 2] < padding || indexes[dimensions - 2] >= shape[shape.length - 2] - padding
                    || indexes[dimensions - 1] < padding
                    || indexes[dimensions - 1] >= shape[dimensions - 1] - padding) {
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
            while (j >= 0 && indexes[j] == shape[j] - 1) {
                indexes[j] = 0;
                j--;
            }
            if (j < 0) {
                break;
            }
            // 一开始都是0，将当前索引加1，直至值为loopCounts-1
            indexes[j]++;
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
        // 初始化，indexes中的每个元素都是0
        int[] indexes = new int[dimensions];
        // temp是保存每一次循环的所有索引，用来使用函数get()
        int[] temp = new int[dimensions];
        int sizeNow = height * width;
        int count = 0;
        int color = 0;
        while (true) {
            // 执行循环体操作,每次循环结束都是一个多维数组的索引
            for (int i = 0; i < dimensions; i++) {
                temp[i] = indexes[i];
            }
            // 此时的temp中保存的是每一次循环的所有索引
            // 开始判断位置，如果在padding的范围内，设置为0

            if (indexes[dimensions - 2] < paddingHeight
                    || indexes[dimensions - 2] >= shape[shape.length - 2] - paddingHeight
                    || indexes[dimensions - 1] < paddingWidth
                    || indexes[dimensions - 1] >= shape[dimensions - 1] - paddingWidth) {
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
            while (j >= 0 && indexes[j] == shape[j] - 1) {
                indexes[j] = 0;
                j--;
            }
            if (j < 0) {
                break;
            }
            // 一开始都是0，将当前索引加1，直至值为loopCounts-1
            indexes[j]++;
        }

    }

}