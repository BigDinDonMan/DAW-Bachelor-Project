package utils;

import java.util.Arrays;

public class ArrayUtils {

    private ArrayUtils(){}

    public static <T> void reverse(T[] array) {
        for (int i = 0, j = array.length - 1; i < array.length / 2; ++i, --j) {
            T temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static <T> void reverse(T[] array, int offset, int length) {
        for (int i = offset, j = offset + length - 1; i < (offset + length) / 2; ++i, --j) {
            T temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void reverse(float[] array) {
        for (int i = 0, j = array.length - 1; i < array.length / 2; ++i, --j) {
            float temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void reverse(float[] array, int offset, int length) {
        for (int i = offset, j = offset + length - 1; i < (offset + length) / 2; ++i, --j) {
            float temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void reverse(int[] array) {
        for (int i = 0, j = array.length - 1; i < array.length / 2; ++i, --j) {
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void reverse(double[] array) {
        for (int i = 0, j = array.length - 1; i < array.length / 2; ++i, --j) {
            double temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void reverse(byte[] array) {
        for (int i = 0, j = array.length - 1; i < array.length / 2; ++i, --j) {
            byte temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void reverse(short[] array) {
        for (int i = 0, j = array.length - 1; i < array.length / 2; ++i, --j) {
            short temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void reverse(char[] array) {
        for (int i = 0, j = array.length - 1; i < array.length / 2; ++i, --j) {
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void reverse(boolean[] array) {
        for (int i = 0, j = array.length - 1; i < array.length / 2; ++i, --j) {
            boolean temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static float[] slice(float[] array, int start, int end) {
        return slice(array, start, end, 1);
    }

    public static float[] slice(float[] array, int start, int end, int step) {
        float[] sliced = new float[(end - start) / step];
        int sliceIndex = 0;
        for (int i = start; i < end; i += step, ++sliceIndex) {
            sliced[sliceIndex] = array[i];
        }
        return sliced;
    }

}
