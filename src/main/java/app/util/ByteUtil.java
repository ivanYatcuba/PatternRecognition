package app.util;

import java.util.ArrayList;
import java.util.List;

public class ByteUtil {
    public static byte[] remove(byte[] input, int index) {
        List<Byte> bytes = new ArrayList<>();
        for (byte anInput : input) {
            bytes.add(anInput);
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
        for (byte anInput : input) {
            bytes.add(anInput);
        }
        for(int i: integers) {
            try {
                bytes.set(i, null);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("error removing i index");
            }

        }
        while(bytes.remove(null));
        byte[] output = new byte[bytes.size()];
        for (int i = 0; i < output.length; i++) {
            output[i] = bytes.get(i);
        }
        return output;
    }
}
