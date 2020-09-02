package com.mobo.sharepreferencestore;

public class IntParser implements IParser{
    static final int TYPE = 0;
    @Override
    public StoreMessage read(StoreFileReader source, int keyLen, int valueLen) {
        StoreMessage msg = new StoreMessage();
        msg.key = source.readString(keyLen);
        msg.value = source.readInt();
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
        if (msg.value instanceof Long) {
            sink.writeInt((int)((long) msg.value));
        } else {
            sink.writeInt((int) msg.value);
        }
    }
}
