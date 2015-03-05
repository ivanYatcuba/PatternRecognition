package app.util.pattern;

public class BitUtil {

    public static byte revertBit(byte b, int position){
        return b ^= 1 << position;
    }

    public static boolean bitIsSet(byte b, int position){
        return ((b >> position) & 1) == 1;
    }
}
