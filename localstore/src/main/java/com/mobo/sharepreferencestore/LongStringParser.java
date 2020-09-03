package com.mobo.sharepreferencestore;

class LongStringParser implements IParser {
    static final int TYPE = 224;
    @Override
    public StoreMessage read(StoreFileReader source, int keyLen, int valueLen) {
        StoreMessage msg = new StoreMessage();
        msg.key = source.readString(keyLen);
        msg.value = source.readString(valueLen);
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

        head = msg.value.toString().length();
        sink.write(head >> 8);
        sink.write(head);

        sink.writeString(msg.key);
        sink.writeString(msg.value.toString());
    }
}
