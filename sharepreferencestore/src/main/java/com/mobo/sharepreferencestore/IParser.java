package com.mobo.sharepreferencestore;

public interface IParser {
    int KEY_MAX_LENGTH = 31;
    StoreMessage read(StoreFileReader source, int keyLen, int valueLen);
    void write(StoreFileWriter sink, StoreMessage msg);
}
