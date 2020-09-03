package com.mobo.sharepreferencestore;

import java.io.IOException;
import java.io.InputStream;

/**
 * 8位
 * 最高位 1 代表 value长度为2个字节
 * 次高位与下一位表示： 00 short， 01 int， 10 Long， 11 String
 */
public class HeaderInfo {
    private static final int TYPE_MASK = 224;
    private static final int KEY_LEN_MASK = 31;

    int keyLen, type, valueLen = 0;

    void parserHeader(StoreFileReader source) {
        int head = source.read();
        keyLen = head & KEY_LEN_MASK;
        type = head & TYPE_MASK;
        if (type == StringParser.TYPE) {
            valueLen = source.read();
        } else if (type  == LongStringParser.TYPE) {
            valueLen = source.read();
            valueLen = (valueLen << 8) + valueLen;
        }
    }

    IParser getReadParse() {
        switch (type) {
            case ByteParser.TYPE:
                return new ByteParser();
            case BooleanParser.TYPE:
                return new BooleanParser();
            case ShortParser.TYPE:
                return new ShortParser();
            case IntParser.TYPE:
                return new IntParser();
            case LongParser.TYPE:
                return new LongParser();
            case StringParser.TYPE:
                return new StringParser();
        }
        return new LongStringParser();
    }

    static IParser getWriteParse(Object value) {
        if (value.getClass() == Byte.class){
            return new ByteParser();
        } else if (value.getClass() == Boolean.class) {
            return new BooleanParser();
        } else if (value.getClass() == Short.class) {
            short temp = (short) value;
            if (temp == (byte) temp) {
                return new ByteParser();
            }
            return new ShortParser();
        } else if (value.getClass() == Integer.class) {
            int temp = (int) value;
            if (temp == (byte) temp) {
                return new ByteParser();
            } else if (temp == (short) temp) {
                return new ShortParser();
            }
            return new IntParser();
        } else if (value.getClass() == Long.class) {
            long temp = (long) value;
            if (temp == (byte) temp) {
                return new ByteParser();
            } else if (temp == (short) temp) {
                return new ShortParser();
            } else if (temp == (int) temp) {
                return new IntParser();
            }
            return new LongParser();
        } else {
            String temp = value.toString();
            if (temp.length() < 256) {
                return new StringParser();
            }
        }

        return new LongStringParser();
    }
}
