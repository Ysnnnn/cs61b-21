package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.MyHelperFunction.*;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Represents a gitlet blob object.
 *
 * The saved contents of files. Since Gitlet saves many versions of files,
 * a single file might correspond to multiple blobs: each being tracked in a different commit.
 * a blob should have fileContent,
 *
 *  @author Ysnnnn
 */
public class Blob implements Serializable {
    /** file content is byte array */
    private byte[] fileContent;

    public Blob(String fileName) {
        File file = join(CWD,fileName);
        if (!file.exists()) {
            exit("File does not exist.");
        }
        fileContent = readContents(file);
    }
    public String saveBlob() {
        String blobName = getBlobName();
        File blobFile = join(OBJECT_DIR, blobName);
        writeObject(blobFile, this);
        return blobName;
    }
    public String getBlobName() {
        String blobName = sha1(this.fileContent);
        return blobName;
    }
    public byte[] getFileContent() {
        return fileContent;
    }
}

