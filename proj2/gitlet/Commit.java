package gitlet;


import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.MyHelperFunction.*;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  does at a high level.
 *  @author Ysnnnn
 */
public class Commit implements Serializable {
    /**
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
    private final File commitFileName;

    /** generic commit constructor*/
    public Commit(String message, Date currentTime, HashMap<String,
                    String> fileToBlob, List<String> parents) {
        this.message = message;
        this.currentTime = new Date(0);
        this.timeStamp = dateToString(currentTime);
        this.fileToBlob = fileToBlob;
        this.parents = parents;
        this.UID = generateUID();
        this.commitFileName = generateFileName();
    }
    /** init commit constructor*/
    public Commit() {
        this.message = "initial commit";
        this.currentTime = new Date(0);
        this.timeStamp = dateToString(currentTime);
        this.fileToBlob = new HashMap<>();
        this.parents = new ArrayList<>();
        this.UID = generateUID();
        this.commitFileName = generateFileName();
    }

    private String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return sdf.format(date);
    }
    private String generateUID() {
        return sha1(message, timeStamp, fileToBlob.toString(), parents.toString());
    }

    private File generateFileName() {
        String uID = this.getUID();
        File commitDir = join(COMMIT_DIR, uID.substring(0, 2));
        commitDir.mkdir();
        return join(commitDir, this.UID);
    }
    public void saveCommit() {
        writeObject(commitFileName, this);
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
    /** return the newest  commit on current branch, which is pointed by head. */
    public static Commit getHeadCommit() {
        String head = readContentsAsString(HEAD_FILE);
        File currentHead = join(HEADS_DIR, head);
        String masterCommitUID = readContentsAsString(currentHead);
        File commitFile = join(COMMIT_DIR, masterCommitUID.substring(0, 2));
        return readObject(join(commitFile, masterCommitUID), Commit.class);
    }
    /** return Commit by UID. */
    public static Commit getCommit(String uID) {
        File commitFile = join(COMMIT_DIR, uID.substring(0, 2));
        if (!commitFile.exists()) {
            exit("No commit with that id exists.");
        }
        File uIDFile = join(COMMIT_DIR, "notExists");
        if (uID.length() == 40) {
            uIDFile = join(commitFile, uID);
        } else {
            List<String> commitsName = plainFilenamesIn(commitFile);
            for (String commitName : commitsName) {
                if (commitName.substring(0, uID.length()).equals(uID)) {
                    uIDFile = join(commitFile, commitName);
                }
            }
        }
        if (!uIDFile.exists()) {
            exit("No commit with that id exists.");
        }
        return readObject(uIDFile, Commit.class);
    }
    public static void printCommit(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getUID());
        if (commit.getParents().size() == 2) {
            System.out.println("Merge: " + commit.getParents().get(0).substring(0, 7)
                    + " " + commit.getParents().get(1).substring(0, 7));
        }
        System.out.println("Date: " + commit.getTimeStamp());
        System.out.println(commit.getMessage() + "\n");
    }
}
