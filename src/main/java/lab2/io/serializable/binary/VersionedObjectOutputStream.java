package lab2.io.serializable.binary;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class VersionedObjectOutputStream extends ObjectOutputStream {

    private final int targetVersion;

    public VersionedObjectOutputStream(OutputStream out, int targetVersion)
            throws IOException {
        super(out);
        this.targetVersion = targetVersion;
        writeInt(targetVersion);
    }


    public long getTargetVersion() {
        return targetVersion;
    }
}
