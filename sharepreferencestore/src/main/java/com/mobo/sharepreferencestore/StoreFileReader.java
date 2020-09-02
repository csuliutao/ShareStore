package com.mobo.sharepreferencestore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class StoreFileReader {
    byte[] bytes;
    int offset = 0;
    int length = 0;
    InputStream inputStream;

    public StoreFileReader(File file) {
        bytes = new byte[1024];
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            inputStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isAvailable() {
        if (length > offset) {
            return true;
        }
        flush();
        return length > offset;
    }

    public int read() {
        if (length <= offset) {
            flush();
        }
        return bytes[offset++] & 0xff;
    }

    private void flush() {
        try {
            length = inputStream.read(bytes, 0, 1024);
            offset = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int readShort() {
        int result = read();
        result = (result << 8) + read();
        return result;
    }

    public int readInt() {
        int result = readShort();
        result = (result << 16) + readShort();
        return result;
    }

    public long readLong() {
        long result = readInt();
        result = (result << 32) + readInt();
        return result;
    }

    public String readString(int len) {
        if (len < 1) {
            return "";
        }
        byte[] res = new byte[len];
        int hasByte = 0;
        while (len > length - offset && length > 0) {
            System.arraycopy(bytes, offset, res, hasByte, length - offset);
            hasByte = hasByte + (length - offset);
            len = len - (length - offset);
            flush();
        }

        if (len > 0 && length > 0) {
            System.arraycopy(bytes, offset, res, 0, len);
            offset += len;
        }
        return new String(res);
    }
}
