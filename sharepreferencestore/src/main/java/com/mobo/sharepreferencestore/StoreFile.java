package com.mobo.sharepreferencestore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class StoreFile {
    private File mFile;
    private File mFileBack;

    public StoreFile(String filepath) {
        this.mFile = new File(filepath);
        this.mFileBack = new File(filepath + ".bk");
    }

    public HashMap<String, Object> loadFile() {
        if (mFileBack.exists()) {
            mFile.delete();
            mFileBack.renameTo(mFile);
        }
        if (!mFile.exists()) {
            return new HashMap<>();
        }

        StoreFileReader source = new StoreFileReader(mFile);
        HeaderInfo headerInfo = new HeaderInfo();
        IParser parser = null;
        StoreMessage message;
        HashMap<String, Object> map = new HashMap<>();
        while (source.isAvailable()) {
            headerInfo.parserHeader(source);
            parser = headerInfo.getReadParse();
            message = parser.read(source, headerInfo.keyLen, headerInfo.valueLen);
            map.put(message.key, message.value);
        }
        source.close();
        return map;
    }

    public void write(Map<String, Object> maps) {
        if (maps == null || maps.isEmpty()) {
            return;
        }

        StoreFileWriter sink = new StoreFileWriter(mFileBack);
        HeaderInfo headerInfo = new HeaderInfo();
        IParser parser = null;
        StoreMessage message = new StoreMessage();
        for (Map.Entry<String, Object> temp: maps.entrySet()) {
            message.key = temp.getKey();
            message.value = temp.getValue();
            parser = headerInfo.getWriteParse(message.value);
            parser.write(sink, message);
        }
        sink.close();
    }
}
