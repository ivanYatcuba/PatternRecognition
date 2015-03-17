package app.util;

import java.util.ArrayList;
import java.util.List;

public class ByteUtil {
    public static boolean[] remove(boolean[] input, int index) {
        List<Boolean> bits = new ArrayList<>();
        for (boolean anInput : input) {
            bits.add(anInput);
        }
        bits.remove(index);
        boolean[] output = new boolean[bits.size()];
        for (int i = 0; i < output.length; i++) {
            output[i] = bits.get(i);
        }
        return output;
    }

    public static boolean[] removeListOfIndexes(boolean[] input, List<Integer> integers) {
        List<Boolean> bits = new ArrayList<>();
        for (boolean anInput : input) {
            bits.add(anInput);
        }
        for(int i: integers) {
            try {
                bits.set(i, null);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("error removing i index");
            }

        }
        while(bits.remove(null));
        boolean[] output = new boolean[bits.size()];
        for (int i = 0; i < output.length; i++) {
            output[i] = bits.get(i);
        }
        return output;
    }
}
