package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;


public class StageArea implements Serializable {
    /** Map from file to blob */
    HashMap<String, String> fileToBlob ;

    StageArea() {
        fileToBlob = new HashMap<>();
    }
    void saveStage(File stageFile) {
        writeContents(stageFile, fileToBlob.toString());
    }



}
