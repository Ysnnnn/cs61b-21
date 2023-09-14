package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.MyHelperFunction.*;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  @author Ysnnnn
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     * A commit consist of a log message, timestamp, a mapping of file
     * names to blob references, a parent reference, and (for merges) a second parent reference.
     */

    /** The message of this Commit. */
    private final String message;
    private final Date currentTime;
    private final HashMap<String, String> fileToBlob;
    private final List<String> parents;
    private final String UID;
    private final String timeStamp;
    private final File CommitFileName;

    /* TODO: fill in the rest of this class. */
    /** generic commit constructor*/
    public Commit(String message, Date currentTime, HashMap<String, String> fileToBlob, List<String> parents) {
        this.message = message;
        this.currentTime = new Date(0);
        this.timeStamp = dateToString(currentTime);
        this.fileToBlob = fileToBlob;
        this.parents = parents;
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
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.CHINA);
        return sdf.format(date);
    }
    private String generateUID() {
        return sha1(message, timeStamp, fileToBlob.toString(), parents.toString());
    }

    private File generateFileName() {
        return join(COMMIT_DIR, UID);
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
    public HashMap<String, String> getFileToBlob() {
        return this.fileToBlob;
    }
    public List<String> getParents() {
        return this.parents;
    }
    /** return the newest  commit on current branch. */
    public static Commit getHeadCommit() {
        String head = readContentsAsString(HEAD_FILE);
        File currentHead = join(HEADS_DIR, head);
        String masterCommitUID = readContentsAsString(currentHead);
        return readObject(join(COMMIT_DIR, masterCommitUID), Commit.class);
    }
    /** return Commit by UID. */
    public static Commit getCommit(String UID) {
        File UIDFile = join(COMMIT_DIR, UID);
        if (!UIDFile.exists()) {
            exit("No commit with that id exists.");
        }
        return readObject(UIDFile, Commit.class);
    }
    public static void printCommit(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getUID());
        if (commit.getParents().size() == 2) {
            System.out.println("Merge: " + commit.getParents().get(0).substring(0, 7) +
                    " " + commit.getParents().get(1).substring(0, 7));
        }
        System.out.println("Date: " + commit.getTimeStamp());
        System.out.println(commit.getMessage());
    }
}
