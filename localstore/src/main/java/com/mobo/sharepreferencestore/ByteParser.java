package com.mobo.sharepreferencestore;

class ByteParser implements IParser {
    static final int TYPE = 128;
    @Override
    public StoreMessage read(StoreFileReader source, int keyLen, int valueLen) {
        StoreMessage msg = new StoreMessage();
        msg.key = source.readString(keyLen);
        msg.value = source.read();
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
            sink.write((int)((long) msg.value));
        } else if (msg.value instanceof Integer) {
            sink.write((int) msg.value);
        } else if (msg.value instanceof Short) {
            sink.write((short) msg.value);
        } else {
            sink.write((Byte) msg.value);
        }
    }
}
