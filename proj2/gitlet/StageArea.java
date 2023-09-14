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
    /** if addStage is not created, creat it, then return addStage */
    static StageArea getAddStage() {
        if (!ADD_STAGE.exists()) {
            return new StageArea();
        }
        return readObject(ADD_STAGE, StageArea.class);
    }
    static StageArea getRemoveStage() {
        if (!REMOVE_STAGE.exists()) {
            return new StageArea();
        }
        return readObject(REMOVE_STAGE, StageArea.class);
    }
    public void addFileToBlob(String fileName, String blobName) {
        this.fileToBlob.put(fileName, blobName);
    }
    /** remove file from stage */
    public void removeFileToBlob(String fileName) {
        this.fileToBlob.remove(fileName);
    }
    public HashMap<String, String> getFiletToBlob() {
        return this.fileToBlob;
    }
    static Boolean stageIsEmpty(StageArea stage) {
        return stage.fileToBlob.isEmpty();
    }
    public void clearStage() {
        this.fileToBlob.clear();
    }
    /* Update Stage content to commit content */
    public static HashMap<String, String> updateAddStageToCommit(StageArea addStage, StageArea rmStage, Commit commit) {
        HashMap<String, String> fileToBlob = commit.getFileToBlob();
        HashMap<String, String> addStageFileToBlob = addStage.getFiletToBlob();
        for (String fileName : addStageFileToBlob.keySet()) {
            fileToBlob.put(fileName, addStageFileToBlob.get(fileName));
        }
        HashMap<String, String> rmStageFileToBlob = rmStage.getFiletToBlob();
        for (String fileName : rmStageFileToBlob.keySet()) {
            fileToBlob.remove(fileName, rmStageFileToBlob.get(fileName));
        }
        return fileToBlob;
    }





}
