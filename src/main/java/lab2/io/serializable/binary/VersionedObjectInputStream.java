package lab2.io.serializable.binary;

import java.io.*;

public class VersionedObjectInputStream extends ObjectInputStream {

    private final int targetVersion;

    public VersionedObjectInputStream(InputStream out)
            throws IOException {
        super(out);
        this.targetVersion = readInt();
    }


    public int getTargetVersion() {
        return targetVersion;
    }
}
