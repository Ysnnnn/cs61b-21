package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Repository.OBJECT_DIR;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  @author Ysnnnn
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     *
     * A commit consist of a log message, timestamp, a mapping of file
     * names to blob references, a parent reference, and (for merges) a second parent reference.
     */

    /** The message of this Commit. */
    private String message;
    private Date currentTime;
    private HashMap<String, String> fileToBlob;
    private List<String> parents;
    private String UID;
    private String timeStamp;
    private File CommitFileName;

    /* TODO: fill in the rest of this class. */
    /** generic commit constructor*/
    public Commit(String message, Date currentTime, HashMap fileToBlob, List parents) {
        this.message = "initial commit";
        this.currentTime = new Date(0);
        this.timeStamp = dateToString(currentTime);
        this.fileToBlob = new HashMap<>();
        this.parents = new ArrayList<>();
        this.UID = generateUID();
        this.CommitFileName = generateFileName();
    }
    /** init commit constructor*/
    public Commit() {
        this.message = "initial commit";
        this.currentTime = new Date(0);
        this.timeStamp = dateToString(currentTime);
        this.fileToBlob = new HashMap<>();
        this.parents = new ArrayList<>();
        this.UID = generateUID();
        this.CommitFileName = generateFileName();
    }

    private String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String str = sdf.format(date);
        return str;
    }
    private String generateUID() {
        return sha1(message, timeStamp, fileToBlob.toString(), parents.toString());
    }

    private File generateFileName() {
        return join(OBJECT_DIR, UID);
    }
    public void saveCommit() {
        writeObject(CommitFileName, this);
    }
    public String getUID() {
        return this.UID;
    }
    public String getMessage() {
        return this.message;
    }
    public String getTimeStamp() {
        return this.timeStamp;
    }
}
