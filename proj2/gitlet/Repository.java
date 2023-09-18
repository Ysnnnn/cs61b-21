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
    public static final File COMMIT_DIR = join(OBJECT_DIR, "commit");
    /** blob directory */
    public static final File BLOB_DIR = join(OBJECT_DIR, "blob");
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
        setBranchHead2Commit("master", initCommit.getUID());

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
        String curBranchName = readContentsAsString(HEAD_FILE);
        setBranchHead2Commit(curBranchName, newMasterCommit.getUID());
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
        List<String> parents;
        do {
            System.out.println("===");
            System.out.println("commit " + commit.getUID());
            parents = commit.getParents();
            if (parents.size() == 2) {
                System.out.println("Merge: " + parents.get(0).substring(0, 7) +
                        " " + parents.get(1).substring(0, 7));
            }
            System.out.println("Date: " + commit.getTimeStamp());
            System.out.println(commit.getMessage() + "\n");
            if (parents.size() != 0) {
                commit = getCommit(parents.get(0));
            }
        } while (parents.size() != 0);
    }
    static void globalLog() {
        List<String> allCommit = plainFilenamesIn(COMMIT_DIR);
        for (String commitName : allCommit) {
            Commit commit = readObject(join(COMMIT_DIR, commitName), Commit.class);
            printCommit(commit);
        }
    }
    static void find(String commitMessage) {
        List<String> allCommit = plainFilenamesIn(COMMIT_DIR);
        Commit commit = null;
        for (String commitName : allCommit) {
             commit = readObject(join(COMMIT_DIR, commitName), Commit.class);
             if(commit.getMessage().equals(commitMessage)) {
                 System.out.println(commit.getUID());
             }
        }
        if (commit == null) {
            exit("Found no commit with that message.");
        }
    }
    //TODO when finish branch check branch
    static  void status() {

    }
    static  void branch(String branchName) {
        judgeBranchExist(branchName);
        setBranchHead2Commit(branchName, getHeadCommit().getUID());
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
    static void checkoutBranch(String branchName) {
        String currentBranch = readContentsAsString(HEAD_FILE);
        if (branchName.equals(currentBranch)) {
            exit("No need to checkout the current branch.");
        }
        List<String> branch = plainFilenamesIn(HEADS_DIR);
        if (!branch.contains(branch)) {
            exit("No such branch exists.");
        }
        List<String> untrackedFile = getUntrackedFile();
        if (!untrackedFile.isEmpty()) {
            exit("There is an untracked file in the way; delete it, or add and commit it first.");
        }
        switchHead(branchName);
        Commit commit = getHeadCommit();
        HashMap<String, String> fileToBlob = commit.getFileToBlob();
        List<String> currentFile = plainFilenamesIn(CWD);
        for (String file : fileToBlob.keySet()) {
            Blob blob = readObject(join(BLOB_DIR, fileToBlob.get(file)), Blob.class);
            writeContents(join(CWD, file), blob.getFileContent());
        }
        for (String file : currentFile) {
            if (!fileToBlob.containsKey(file)) {
                restrictedDelete(join(CWD, file));
            }
        }
        clearBothStage();
    }

    /** set branch head to commit by branch name and save current branch name in HEAD */
    static void setBranchHead2Commit(String branchHeadName, String commitUID) {
        File file = getBranchHeadFile(branchHeadName);
        writeContents(file, commitUID);
        writeContents(HEAD_FILE, branchHeadName);
    }
    /** return branch head file, which store commitID */
    static File getBranchHeadFile(String branchName) {
        return join(HEADS_DIR, branchName);
    }
    /** if the branch is already exist, print error message and exit.*/
    static void judgeBranchExist(String branchName) {
        File file = join(HEADS_DIR, branchName);
        if (file.exists()) {
            exit("A branch with that name already exists.");
        }
    }

    /** judge if the current working version of the file is identical to the version in
     * the current commit.If identical, return true, else false.
     */
     static Boolean sameFileAndHead(Commit headCommit, String filename, String newBlobName) {
        HashMap<String, String> commitFileToBlob = headCommit.getFileToBlob();
        if (commitFileToBlob.containsKey(filename)) {
            String blobName = commitFileToBlob.get(filename);
            return blobName.equals(newBlobName);
        }
        return false;
    }
    /** return List of untracked files name in current branch. */
    static List<String> getUntrackedFile(){
        List<String> untrackedFile = new ArrayList<>();
        Commit commit = getHeadCommit();
        HashMap<String , String> fileToBlob= commit.getFileToBlob();
        List<String> currentFile = plainFilenamesIn(CWD);
        for (String file :currentFile) {
            if (!fileToBlob.containsKey(file)) {
                untrackedFile.add(file);
            }
        }
        return untrackedFile;
    }
    /** change the branch of head points to, namely save current branch name in HEAD_FILE */
    static void switchHead(String branchName) {
        writeContents(HEAD_FILE, branchName);
    }
    static void clearBothStage() {
        StageArea addStage = getAddStage();
        StageArea rmStage = getRemoveStage();
        addStage.clearStage();
        rmStage.clearStage();
        addStage.saveStage(ADD_STAGE);
        rmStage.saveStage(REMOVE_STAGE);
    }

}
