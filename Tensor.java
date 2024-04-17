
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
    public Tensor getSubTensorBySubDimensions(int... subDimensions) {
        int[] subShape = new int[subDimensions.length];
        for (int i = 0; i < subDimensions.length; i++) {
            subShape[i] = shape[shape.length - subDimensions.length + i];
        }
        Tensor subTensor = new Tensor(subShape);
        int[] subIndices = new int[subShape.length];

        // 计算用shape的维度表示的subIndices
        int[] totalDimensions = new int[shape.length];
        for (int i = 0; i < shape.length; i++) {
            totalDimensions[i] = i;
        }
        int[] indices = new int[shape.length];

        for (int i = 0; i < data.length; i++) {
            // 由i,shape和get方法计算indices
            calculateSubIndicesBySubDimensions(i, totalDimensions, indices);
            Object value = get(indices);

            calculateSubIndicesBySubDimensions(i, subDimensions, subIndices);

            subTensor.set(value, subIndices);
            // padding(subTensor, subIndices);
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

    public void pad(int padding) {
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
                temp[dimensions - 2] -= padding;
                temp[dimensions - 1] -= padding;
                System.out.println("在范围外：" + temp[dimensions - 2] + " " + temp[dimensions - 1]);
                this.set(this.get(temp), indices);
                System.out.println(this.get(indices));
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

    // public Tensor2 pad(int padding) {
    // int[] newShape = new int[2];
    // 图片高度是shape[shape.length-2]，宽度是shape[shape.length-1]
    // newShape[0] = shape[shape.length - 2] + 2 * padding;
    // newShape[1] = shape[shape.length - 1] + 2 * padding;
    // 建立新的二维的扩大的张量
    // Tensor2 result = new Tensor2(newShape);
    /*
     * // indices的每个元素的初始值都是0(代表x轴和y轴)
     * int[] indices = new int[2];
     * // temp的每个元素的初始值都是0
     * int[] temp = new int[2];
     * while (true) {
     * for (int i = 0; i < 2; i++) {
     * temp[i] = indices[i];
     * }
     * if (indices[0] < padding || indices[0] >= shape[shape.length - 2] + padding
     * || indices[1] < padding
     * || indices[1] >= shape[shape.length - 1] + padding) {
     * result.set(0, temp);
     * } else {
     * temp[0] -= padding;
     * temp[1] -= padding;
     * result.set(get(temp), indices);
     * }
     * int j = 1;
     * while (j >= 0 && indices[j] == newShape[j] - 1) {
     * indices[j] = 0;
     * j--;
     * }
     * if (j < 0) {
     * break;
     * }
     * indices[j]++;
     * }
     */
    // return result;
    // }

    public static void main(String[] args) {
        Tensor tensor = new Tensor(2, 3, 1);
        tensor.set(1, 0, 0, 0);
        tensor.set(2, 0, 1, 0);
        tensor.set(3, 0, 2, 0);
        tensor.set(4, 1, 0, 0);
        tensor.set(5, 1, 1, 0);
        tensor.set(6, 1, 2, 0);
        Tensor other = new Tensor(2, 3);
        other.set(1, 0, 0);
        other.set(2, 0, 1);
        other.set(3, 0, 2);
        other.set(7, 1, 0);
        other.set(1, 1, 1);
        other.set(6, 1, 2);

        /*
         * Tensor subTensor = tensor.getSubTensorBySubDimensions(1, 2);
         * // 输出子张量的形状
         * System.out.println("SubTensor Shape: " + Arrays.toString(subTensor.shape));
         * 
         * // 输出子张量的数据
         * for (int i = 0; i < subTensor.data.length; i++) {
         * System.out.println("SubTensor Data at index " + i + ": " +
         * subTensor.data[i]);
         * }
         * 
         */
        tensor.pad(1);
        System.out.println(tensor.get(0, 0, 0));
        System.out.println(tensor.get(0, 1, 1));
    }

    /*
     * Tensor result = tensor.add(other);
     * System.out.println(result.get(0, 0)); // Output: 1
     * System.out.println(result.get(1, 2)); // Output: 6
     * 
     * Tensor result2 = tensor.sub(other);
     * System.out.println(result2.get(0, 0)); // Output: 1
     * System.out.println(result2.get(1, 2)); // Output : 6
     */

    /*
     * Tensor2 result3 = tensor.pad(2);
     * System.out.println(result3.get(0, 0)); // Output: 1
     * System.out.println(result3.get(1, 2)); // Output: 6
     * System.out.println(result3.get(0, 1)); // Output: 0
     */

}
