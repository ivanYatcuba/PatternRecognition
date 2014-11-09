package app.util;

import org.springframework.stereotype.Component;

@Component
public class BitUtil {

    public byte revertBit(byte b, int position){
        return b ^= 1 << position;
    }

    public boolean bitIsSet(byte b, int position){
        return ((b >> position) & 1) == 1;
    }
}
