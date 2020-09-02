package com.mobo.sharepreferencestore;

public class BooleanParser implements IParser {
    static final int TYPE = 160;
    @Override
    public StoreMessage read(StoreFileReader source, int keyLen, int valueLen) {
        StoreMessage msg = new StoreMessage();
        msg.key = source.readString(keyLen);
        msg.value = source.read() == 1;
        return msg;
    }

    @Override
    public void write(StoreFileWriter sink, StoreMessage msg) {
        int head = TYPE;
        if (msg.key.length() > KEY_MAX_LENGTH) {
            msg.key = msg.key.substring(KEY_MAX_LENGTH);
        }
        head += msg.key.length();
        sink.write(head);
        sink.writeString(msg.key);
        sink.write(Boolean.TRUE.equals(msg.value) ? 1 : 0);
    }
}
