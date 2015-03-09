package app.util;

import java.util.ArrayList;
import java.util.List;

public class ByteUtil {
    public static byte[] remove(byte[] input, int index) {
        List<Byte> bytes = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            bytes.add(input[i]);
        }
        bytes.remove(index);
        byte[] output = new byte[bytes.size()];
        for (int i = 0; i < output.length; i++) {
            output[i] = bytes.get(i);
        }
        return output;
    }

    public static byte[] removeListOfIndexes(byte[] input, List<Integer> integers) {
        List<Byte> bytes = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            bytes.add(input[i]);
        }
        for (int i: integers) {
            bytes.remove(i);
        }
        byte[] output = new byte[bytes.size()];
        for (int i = 0; i < output.length; i++) {
            output[i] = bytes.get(i);
        }
        return output;
    }
}
