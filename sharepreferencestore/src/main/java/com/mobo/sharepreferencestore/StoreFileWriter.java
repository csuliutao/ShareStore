package com.mobo.sharepreferencestore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StoreFileWriter {
    byte[] bytes;
    int size = 0;
    OutputStream outputStream;

    public StoreFileWriter(File file) {
        bytes = new byte[1024];
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(int b) {
        if (size > 1023) {
            flush();
            size = 0;
        }
        bytes[size++] = (byte) (b & 0xff);
    }

    public void writeShort(int s) {
        write(s >> 8);
        write(s);
    }

    public void writeInt(int i) {
        writeShort(i >> 16);
        writeShort(i);
    }

    public void writeLong(long l) {
        writeInt((int) (l >> 32));
        writeInt((int) (l));
    }

    public void writeString(String str) {
        if (size + str.length() > 1023) {
            flush();
            size = 0;
        }
        int len = str.length();
        int startIndex = 0;
        while (len > 1024) {
            System.arraycopy(str.getBytes(), startIndex, bytes, size , 1024 - size);
            len = len + size - 1024;
            startIndex = startIndex + 1024 - size;
            flush();
        }
        System.arraycopy(str.getBytes(), startIndex, bytes, size, len);
        size += len;
    }

    private void flush() {
        try {
            outputStream.write(bytes, 0, size);
            size = 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
