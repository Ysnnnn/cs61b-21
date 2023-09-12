package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;


public class StageArea implements Serializable {
    /** Map from file to blob */
    private HashMap<String, String> fileToBlob ;

    StageArea() {
        fileToBlob = new HashMap<>();
    }
    void saveStage(File stageFile) {
        writeObject(stageFile, this);
    }
    static StageArea getAddStage() {
        if (!ADD_STAGE.exists()) {
            return new StageArea();
        }
        return readObject(ADD_STAGE, StageArea.class);
    }
    public void addFileToBlob(String fileName, String blobName) {
        this.fileToBlob.put(fileName, blobName);
    }
    public void removeFileToBlob(String fileName) {
        this.fileToBlob.remove(fileName);
    }
    public HashMap getFiletToBlob() {
        return this.fileToBlob;
    }
    static Boolean stageIsEmpty(StageArea stage) {
        return stage.fileToBlob.isEmpty();
    }



}
