package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Blob.*;
import static gitlet.Commit.*;
import static gitlet.StageArea.*;
import static gitlet.Utils.*;
import static gitlet.MyHelperFunction.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /*
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     *     .gitlet
     *      *      |--objects
     *      *      |     |--blob
     *      *      |     |--commit
     *      *      |--refs
     *      *      |    |--heads
     *      *      |         |--master
     *      *      |--HEAD
     *      *      |--addStage
     *      *      |--removeStage
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** object directory to store commit dir and blob dir. */
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    /** commit directory */
    public static final File COMMIT_DIR = join(GITLET_DIR, "commit");
    /** blob directory */
    public static final File BLOB_DIR = join(GITLET_DIR, "blob");
    /** refs directory to store heads. */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /** HEAD file */
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    /** heads directory to store master. */
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    /** master file */
    public static final File MASTER = join(HEADS_DIR, "master");
    /** addStage File */
    public static final File ADD_STAGE = join(GITLET_DIR, "addStage");
    /** removeStage File */
    public static final File REMOVE_STAGE = join(GITLET_DIR, "removeStage");

    /* TODO: fill in the rest of this class. */
    static void init() {
        if (GITLET_DIR.exists()) {
            exit("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        Commit initCommit = new Commit();
        initCommit.saveCommit();
        changeMaster(initCommit.getUID());
        changeHEAD("master");

    }
    static void add(String filename) {
        /* If the current working version of the file is identical to the version in
         * the current commit, do not stage it to be added, and remove it from the staging
         * area if it is already there (as can happen when a file is changed, added, and
         * then changed back to it’s original version).
         */

        /* creat a new blob for current file and get blobName
         */
        Blob newBlob = new Blob(filename);
        String newBlobName = newBlob.getBlobName();
        newBlob.saveBlob();
        /* get removeStage and remove the file from removeStage if it is staged. */
        StageArea removeStage = getRemoveStage();
        removeStage.removeFileToBlob(filename);
        /* get addStage */
        StageArea addStage = getAddStage();
        /* judge if the current working version of the file is identical to the version in
         * the current commit
         */
        Commit headCommit = getHeadCommit();
        if (!sameFileAndHead(headCommit, filename, newBlobName)) {
            /* add file to addition stage */
            addStage.addFileToBlob(filename, newBlobName);
            addStage.saveStage(ADD_STAGE);
        } else {
            /* do not stage it to be added, and remove it from the staging
             * area if it is already there */
            addStage.removeFileToBlob(filename);
        }

    }
    static void commit(String message) {
        if (message == null) {
            exit("Please enter a commit message.");
        }
        StageArea addStage = getAddStage();
        StageArea rmStage = getRemoveStage();
        if (stageIsEmpty(addStage)) {
            exit("No changes added to the commit.");
        }
        Commit headCommit = getHeadCommit();
        HashMap<String, String> fileToBlob = updateAddStageToCommit(addStage, rmStage, headCommit);
        String masterUID = headCommit.getUID();
        List<String> parents = new ArrayList<>();
        parents.add(masterUID);
        Commit newMasterCommit = new Commit(message, new Date(), fileToBlob, parents);
        newMasterCommit.saveCommit();
        changeMaster(newMasterCommit.getUID());
        addStage.clearStage();
        addStage.saveStage(ADD_STAGE);
        rmStage.clearStage();
        rmStage.saveStage(REMOVE_STAGE);
    }


    static void rm(String fileName) {
        /* Unstage the file if it is currently staged for addition. If the file is
        tracked in the current commit, stage it for removal and remove the file from
        the working directory if the user has not already done so.  If the file is
        neither staged nor tracked by the head commit, print the error message.*/
        Commit headCommit = getHeadCommit();
        Boolean fileInCommit = headCommit.getFileToBlob().containsKey(fileName);
        StageArea addStage = getAddStage();
        Boolean fileInAddStage = addStage.getFiletToBlob().containsKey(fileName);
        StageArea rmStage = getRemoveStage();
        if (fileInCommit && fileInAddStage) {
            exit("No reason to remove the file.");
        }
        if (fileInAddStage) {
            addStage.removeFileToBlob(fileName);
        }
        if (fileInCommit) {
            rmStage.getFiletToBlob().put(fileName, headCommit.getFileToBlob().get(fileName));
            File file = join(CWD, fileName);
            restrictedDelete(file);
        }
    }
    static void log() {
        Commit commit = getHeadCommit();
        do {
            System.out.println("===");
            System.out.println("commit " + commit.getUID());
            if (commit.getParents().size() == 2) {
                System.out.println("Merge: " + commit.getParents().get(0).substring(0, 7) +
                        " " + commit.getParents().get(1).substring(0, 7));
            }
            System.out.println("Date: " + commit.getTimeStamp());
            System.out.println(commit.getMessage());
            commit = getCommit(commit.getParents().get(0));
        } while (!commit.getParents().isEmpty());
    }
    static void globalLog() {
        List<String> allCommit = plainFilenamesIn(COMMIT_DIR);
        for (String commitName : allCommit) {
            Commit commit = readObject(join(COMMIT_DIR, commitName), Commit.class);
            printCommit(commit);
        }
    }
    static void checkout(String filename) {
        Commit commit = getHeadCommit();
        String blobName = commit.getFileToBlob().get(filename);
        if (blobName == null) {
            exit("File does not exist in that commit.");
        }
        Blob blob = getBlob(blobName);
        byte[] content = blob.getFileContent();
        writeContents(join(CWD, filename), content);
    }
    static void checkout(String commitUID, String filename) {
        Commit commit = getCommit(commitUID);
        String blobName = commit.getFileToBlob().get(filename);
        if (blobName == null) {
            exit("File does not exist in that commit.");
        }
        Blob blob = getBlob(blobName);
        byte[] content = blob.getFileContent();
        writeContents(join(CWD, filename), content);

    }

    /** let master points to new commit. */
    public static void changeMaster(String commitUID) {
        writeContents(MASTER, commitUID);
    }
    /** HEAD file save the name of the current branch. */
    public static void changeHEAD(String branchName) {
        writeContents(HEAD_FILE, branchName);
    }

    /** judge if the current working version of the file is identical to the version in
     * the current commit.If identical, return true, else false.
     */
    public static Boolean sameFileAndHead(Commit headCommit, String filename, String newBlobName) {
        HashMap<String, String> commitFileToBlob = headCommit.getFileToBlob();
        if (commitFileToBlob.containsKey(filename)) {
            String blobName = commitFileToBlob.get(filename);
            return blobName.equals(newBlobName);
        }
        return false;
    }


}
