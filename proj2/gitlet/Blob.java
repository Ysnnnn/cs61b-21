package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.GITLET_DIR;
import static gitlet.Repository.OBJECT_DIR;
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
    /** file content is a sha1 String */
    String fileContent;

    public Blob(String fileName) {
        File file =join(GITLET_DIR,fileName);
        fileContent = readContentsAsString(file);
    }
    void saveBlob() {
        File blobFile = join(OBJECT_DIR, sha1(this.fileContent));
        writeObject(blobFile, this.fileContent);
    }
}

