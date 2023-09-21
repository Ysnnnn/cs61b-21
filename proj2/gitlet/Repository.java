package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Blob.*;
import static gitlet.Commit.*;
import static gitlet.StageArea.*;
import static gitlet.Utils.*;
import static gitlet.MyHelperFunction.*;


/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author Ysnnnn
 */
public class Repository {
    /*
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
        } else {
            /* do not stage it to be added, and remove it from the staging
             * area if it is already there */
            addStage.removeFileToBlob(filename);
        }
        addStage.saveStage(ADD_STAGE);
        removeStage.saveStage(REMOVE_STAGE);
    }
    static void commit(String message) {
        if (message.isEmpty()) {
            exit("Please enter a commit message.");
        }
        StageArea addStage = getAddStage();
        StageArea rmStage = getRemoveStage();
        if (stageIsEmpty(addStage) && stageIsEmpty(rmStage)) {
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
        boolean fileInCommit = headCommit.getFileToBlob().containsKey(fileName);
        StageArea addStage = getAddStage();
        boolean fileInAddStage = addStage.getFiletToBlob().containsKey(fileName);
        StageArea rmStage = getRemoveStage();
        if (!fileInCommit && !fileInAddStage) {
            exit("No reason to remove the file.");
        }
        if (fileInAddStage) {
            addStage.removeFileToBlob(fileName);
            addStage.saveStage(ADD_STAGE);
        }
        if (fileInCommit) {
            rmStage.getFiletToBlob().put(fileName, headCommit.getFileToBlob().get(fileName));
            File file = join(CWD, fileName);
            restrictedDelete(file);
            rmStage.saveStage(REMOVE_STAGE);
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
                System.out.println("Merge: " + parents.get(0).substring(0, 7)
                        + " " + parents.get(1).substring(0, 7));
            }
            System.out.println("Date: " + commit.getTimeStamp());
            System.out.println(commit.getMessage() + "\n");
            if (parents.size() != 0) {
                commit = getCommit(parents.get(0));
            }
        } while (parents.size() != 0);
    }
    static void globalLog() {
        List<Commit> allCommit = getAllCommit();
        for (Commit commit : allCommit) {
            printCommit(commit);
        }
    }
    static void find(String commitMessage) {
        List<Commit> allCommit = getAllCommit();
        boolean find = false;
        for (Commit commit : allCommit) {
            if (commit.getMessage().equals(commitMessage)) {
                System.out.println(commit.getUID());
                find = true;
            }
        }
        if (!find) {
            exit("Found no commit with that message.");
        }
    }
    static  void status() {
        printStatusHeader("Branches");
        List<String> branches = plainFilenamesIn(HEADS_DIR);
        if (branches != null) {
            Collections.sort(branches);
        }
        assert branches != null;
        for (String branch : branches) {
            if (branch.equals(getCurBranch())) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        System.out.print("\n");
        //
        printStatusHeader("Staged Files");
        StageArea addStage = getAddStage();
        HashMap<String, String> addFileToBlob = addStage.getFiletToBlob();
        Set<String> stagedFilesSet = addFileToBlob.keySet();
        List<String> stagedFiles = new ArrayList<>(stagedFilesSet);
        Collections.sort(stagedFiles);
        for (String file : stagedFiles) {
            System.out.println(file);
        }
        System.out.print("\n");
        //
        printStatusHeader("Removed Files");
        StageArea rmStage = getRemoveStage();
        HashMap<String, String> rmFileToBlob = rmStage.getFiletToBlob();
        Set<String> removedFilesSet = rmFileToBlob.keySet();
        List<String> removedFiles = new ArrayList<>(removedFilesSet);
        Collections.sort(removedFiles);
        for (String file : removedFiles) {
            System.out.println(file);
        }
        System.out.print("\n");
        //
        printStatusHeader("Modifications Not Staged For Commit");
        List<String> files = plainFilenamesIn(CWD);
        List<String> modifiedFiles = new ArrayList<>();
        Set<String> deletedFilesSet = new HashSet<>();
        List<String> untrackedFiles = new ArrayList<>();
        Commit commit = getHeadCommit();
        HashMap<String, String> fileToBlob = commit.getFileToBlob();
        //judge if current files are modified or untracked.
        if (files != null) {
            for (String file : files) {
                byte[] fileContent = readContents(join(CWD, file));
                String fileSHA1 = sha1(fileContent);
                if (!fileToBlob.containsKey(file) && !stagedFiles.contains(file)) {
                    untrackedFiles.add(file);
                }
                if (stagedFiles.contains(file)) {
                    if (!addFileToBlob.get(file).equals(fileSHA1)) {
                        modifiedFiles.add(file);
                    }
                } else {
                    if (getHeadCommit().getFileToBlob().containsKey(file)) {
                        if (!sameFileAndHead(getHeadCommit(), file, fileSHA1)) {
                            modifiedFiles.add(file);
                        }
                    }
                }
            }
        }
        //find the deleted file
        for (String stagedFile : stagedFiles) {
            if (!fileExist(stagedFile)) {
                deletedFilesSet.add(stagedFile);
            }
        }
        for (String file : fileToBlob.keySet()) {
            if (!fileExist(file) && !removedFilesSet.contains(file)) {
                deletedFilesSet.add(file);
            }
        }
        List<String> deletedFiles = new ArrayList<>(deletedFilesSet);
        List<String> combinedList = new ArrayList<>();
        combinedList.addAll(deletedFiles);
        combinedList.addAll(modifiedFiles);
        Collections.sort(combinedList);
        for (String file : combinedList) {
            if (deletedFiles.contains(file)) {
                System.out.println(file + "(deleted)");
            } else {
                System.out.println(file + "(modified)");
            }
        }
        System.out.print("\n");
        printStatusHeader("Untracked Files");
        for (String file : untrackedFiles) {
            System.out.println(file);
        }
        System.out.print("\n");
    }
    static void branch(String branchName) {
        judgeBranchExist(branchName);
        File file = getBranchHeadFile(branchName);
        writeContents(file, getHeadCommit().getUID());
    }
    static void rmBranch(String branchName) {
        if (branchName.equals(getCurBranch())) {
            exit("Cannot remove the current branch.");
        }
        File branch = join(HEADS_DIR, branchName);
        if (!branch.exists()) {
            exit("A branch with that name does not exist.");
        }
        branch.delete();
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
        if (!branch.contains(branchName)) {
            exit("No such branch exists.");
        }
        List<String> untrackedFile = getUntrackedFile();
        switchHead(branchName);
        Commit commit = getHeadCommit();
        HashMap<String, String> fileToBlob = commit.getFileToBlob();
        Set<String> commitFiles = fileToBlob.keySet();
        for (String file : untrackedFile) {
            if (commitFiles.contains(file)) {
                exit("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
            }
        }
        List<String> currentFile = plainFilenamesIn(CWD);
        for (String file : commitFiles) {
            Blob blob = readObject(join(BLOB_DIR, fileToBlob.get(file)), Blob.class);
            writeContents(join(CWD, file), blob.getFileContent());
        }
        if (!currentFile.isEmpty()) {
            for (String file : currentFile) {
                if (!commitFiles.contains(file)) {
                    restrictedDelete(join(CWD, file));
                }
            }
        }
        clearBothStage();
    }
    static void reset(String commitUID) {
        List<String> trackedFile = getTrackedFile();
        List<String> untrackedFile = getUntrackedFile();
        Commit commit = getCommit(commitUID);
        Set<String> commitFiles = commit.getFileToBlob().keySet();
        for (String file : untrackedFile) {
            if (commitFiles.contains(file)) {
                exit("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
            }
        }
        for (String file : trackedFile) {
            if (!commitFiles.contains(file)) {
                restrictedDelete(join(CWD, file));
            }
        }
        for (String file : commitFiles) {
            Blob blob = readObject(join(BLOB_DIR, commit.getFileToBlob().get(file)), Blob.class);
            byte[] fileContent = blob.getFileContent();
            writeContents(join(CWD, file), fileContent);
        }
        clearBothStage();
        String curBranchName = readContentsAsString(HEAD_FILE);
        setBranchHead2Commit(curBranchName, commitUID);
    }
    static void merge(String branchName) {
        if (!bothStageIsEmpty()) {
            exit("You have uncommitted changes.");
        }
        File branch = join(HEADS_DIR, branchName);
        if (!branch.exists()) {
            exit("A branch with that name does not exist.");
        }
        if (branchName.equals(getCurBranch())) {
            exit("Cannot merge a branch with itself.");
        }
        boolean conflict = false;
        Commit ancestor = getAncestor(branchName);
        String branchUID =  getBranchCommitUID(branchName);
        if (branchUID.equals(ancestor.getUID())) {
            exit("Given branch is an ancestor of the current branch.");
        }
        if (ancestor.getUID().equals(getHeadCommit().getUID())) {
            checkout(branchName);
            exit("Current branch fast-forwarded.");
        }
        Commit given = getCommit(getBranchCommitUID(branchName));
        Commit current = getHeadCommit();
        Set<String> willBeRemoved = getWillBeRemoved(ancestor, given, current);
        Set<String> toBeCheckedOut = getToBeCheckedOut(ancestor, given, current);
        Set<String> diffFrom3 = getDiffFrom3(ancestor, given, current);
        Set<String> curModGivDel = getcurModGivDel(ancestor, given, current);
        Set<String> givModCurDel = getcurModGivDel(ancestor, current, given);
        Set<String> anDelDiff = getAnDelDiff(ancestor, current, given);
        Set<String> toBeDelOrMod = new HashSet<>();
        toBeDelOrMod.addAll(willBeRemoved);
        toBeDelOrMod.addAll(toBeCheckedOut);
        toBeDelOrMod.addAll(diffFrom3);
        toBeDelOrMod.addAll(curModGivDel);
        toBeDelOrMod.addAll(givModCurDel);
        toBeDelOrMod.addAll(anDelDiff);
        List<String> untrackedFile = getUntrackedFile();
        for (String file : untrackedFile) {
            if (toBeDelOrMod.contains(file)) {
                exit("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
        for (String file : willBeRemoved) {
            rm(file);
        }
        for (String file : toBeCheckedOut) {
            checkout(given.getUID(), file);
            add(file);
        }
        if (!diffFrom3.isEmpty()) {
            conflict = true;
        }
        mergeAndSave(diffFrom3, current, given);
        if (!curModGivDel.isEmpty()) {
            conflict = true;
        }
        for (String file : curModGivDel) {
            Blob currentBlob = getBlob(current.getFileToBlob().get(file));
            String mergeContent = mergeContent("", currentBlob.getFileContent());
            writeContents(join(CWD, file), mergeContent);
            add(file);
        }
        if (!givModCurDel.isEmpty()) {
            conflict = true;
        }
        for (String file : givModCurDel) {
            Blob givenBlob = getBlob(given.getFileToBlob().get(file));
            String mergeContent = mergeContent(givenBlob.getFileContent(), "");
            writeContents(join(CWD, file), mergeContent);
            add(file);
        }
        mergeAndSave(anDelDiff, current, given);
        mergeCommit("Merged " + branchName +" into " + getCurBranch() + ".", given);
        if (conflict) {
            exit("Encountered a merge conflict.");
        }
    }
    static void mergeCommit(String message, Commit given) {
        if (message.isEmpty()) {
            exit("Please enter a commit message.");
        }
        StageArea addStage = getAddStage();
        StageArea rmStage = getRemoveStage();
        if (stageIsEmpty(addStage) && stageIsEmpty(rmStage)) {
            exit("No changes added to the commit.");
        }
        Commit headCommit = getHeadCommit();
        HashMap<String, String> fileToBlob = updateAddStageToCommit(addStage, rmStage, headCommit);
        String headUID = headCommit.getUID();
        List<String> parents = new ArrayList<>();
        parents.add(headUID);
        parents.add(given.getUID());
        Commit newHeadCommit = new Commit(message, new Date(), fileToBlob, parents);
        newHeadCommit.saveCommit();
        String curBranchName = readContentsAsString(HEAD_FILE);
        setBranchHead2Commit(curBranchName, newHeadCommit.getUID());
        addStage.clearStage();
        addStage.saveStage(ADD_STAGE);
        rmStage.clearStage();
        rmStage.saveStage(REMOVE_STAGE);
    }
    /** merge content and save file. */
    private static void mergeAndSave(Set<String> files, Commit current, Commit given) {
        for (String file : files) {
            Blob givenBlob = getBlob(given.getFileToBlob().get(file));
            Blob currentBlob = getBlob(current.getFileToBlob().get(file));
            String mergeContent = mergeContent(givenBlob.getFileContent(), currentBlob.getFileContent());
            writeContents(join(CWD, file), mergeContent);
            add(file);
        }
    }
    /** return set of files which not existed in ancestor and different between given and current. */
    private static Set<String> getAnDelDiff(Commit ancestor, Commit current, Commit given) {
        Set<String> anDelDiff = new HashSet<>();
        Set<String> currentFiles = current.getFileToBlob().keySet();
        for (String file : currentFiles) {
            if (!ancestor.getFileToBlob().containsKey(file) && given.getFileToBlob().containsKey(file)) {
                if (!current.getFileToBlob().get(file).equals(given.getFileToBlob().get(file))) {
                    anDelDiff.add(file);
                }
            }
        }
        return anDelDiff;
    }

    /** return set of files which is modified in current but deleted in given. */
    private static Set<String> getcurModGivDel(Commit ancestor, Commit given, Commit current) {
        Set<String> curModGivDel = new HashSet<>();
        Set<String> ancestorFiles = ancestor.getFileToBlob().keySet();
        for (String file : ancestorFiles) {
            if (current.getFileToBlob().containsKey(file) && !given.getFileToBlob().containsKey(file)) {
                if (!current.getFileToBlob().get(file).equals(ancestor.getFileToBlob().get(file))) {
                    curModGivDel.add(file);
                }
            }
        }
        return curModGivDel;
    }

    /** return merged contents. */
    private static String mergeContent(byte[] givenContent, byte[] curContent) {
        String gContent = new String(givenContent);
        String cContent = new String(curContent);
        return "<<<<<<< HEAD\n" + cContent + "=======\n" + gContent + ">>>>>>>\n";
    }
    private static String mergeContent(String givenContent, byte[] curContent) {
        String cContent = new String(curContent);
        return "<<<<<<< HEAD\n" + cContent + "=======\n" + givenContent + ">>>>>>>\n";
    }
    private static String mergeContent(byte[] givenContent, String curContent) {
        String gContent = new String(givenContent);
        return "<<<<<<< HEAD\n" + curContent + "=======\n" + gContent + ">>>>>>>\n";
    }


    /** return set of files which is all exist, but they're different. */
    private static Set<String> getDiffFrom3(Commit ancestor, Commit given, Commit current) {
        Set<String> allHave = getFileAllHave(ancestor, given, current);
        Set<String> conflict = new HashSet<>();
        for (String file : allHave) {
            String ancestorBlob = ancestor.getFileToBlob().get(file);
            String givenBlob = given.getFileToBlob().get(file);
            String currentBlob = current.getFileToBlob().get(file);
            boolean ancGiven = !ancestorBlob.equals(givenBlob);
            boolean ancCur = !ancestorBlob.equals(currentBlob);
            boolean givCur = !givenBlob.equals(currentBlob);
            if (ancGiven && ancCur && givCur) {
                conflict.add(file);
            }
        }
        return conflict;
    }

    /** return files to be checked out from given branch.*/
    private static Set<String> getToBeCheckedOut(Commit ancestor, Commit given, Commit current) {
        Set<String> toBeCCheckedOut = new HashSet<>();
        Set<String> givenFiles = given.getFileToBlob().keySet();
        for (String file : givenFiles) {
            String ancestorBlob = ancestor.getFileToBlob().get(file);
            String givenBlob = given.getFileToBlob().get(file);
            String currentBlob = current.getFileToBlob().get(file);
            if (!ancestor.getFileToBlob().containsKey(file) &&
                    !current.getFileToBlob().containsKey(file)) {
                toBeCCheckedOut.add(file);
            } else if (!givenBlob.equals(ancestorBlob) && ancestorBlob.equals(currentBlob)) {
                toBeCCheckedOut.add(file);
            }
        }
        return toBeCCheckedOut;
    }

    /** return Set of files that ancestor have but one of the given or current don't have.*/
    private static Set<String> getWillBeRemoved(Commit ancestor, Commit given, Commit current) {
        Set<String> willBeRemoved = new HashSet<>();
        Set<String> ancestorFiles = ancestor.getFileToBlob().keySet();
        for (String file : ancestorFiles) {
            if (!given.getFileToBlob().containsKey(file) &&
                    current.getFileToBlob().containsKey(file)) {
                willBeRemoved.add(file);
            }
//            if (given.getFileToBlob().containsKey(file) &&
//                    !current.getFileToBlob().containsKey(file)) {
//                willBeRemoved.add(file);
//            }
        }
        return willBeRemoved;
    }
    /** return set of file names which all commits have. */
    private static Set<String> getFileAllHave(Commit ancestor, Commit given, Commit current) {
        Set<String> allHave = new HashSet<>();
        Set<String> ancestorFiles = ancestor.getFileToBlob().keySet();
        for (String file : ancestorFiles) {
            if (given.getFileToBlob().containsKey(file) &&
                current.getFileToBlob().containsKey(file)) {
                allHave.add(file);
            }
        }
        return allHave;
    }
    /** return the commit UID of the given branch. */
    private static String getBranchCommitUID(String branchName) {
        return readContentsAsString(getBranchHeadFile(branchName));
    }

    /** return the ancestor commit by the given branch. */
    private static Commit getAncestor(String branchName) {
        List<String> headCommits = getHeadCommits();
        String branchUID = getBranchCommitUID(branchName);
        Commit branchCommit = getCommit(branchUID);
        while (branchCommit.getParents().get(0) != null) {
            if (headCommits.contains(branchCommit.getUID())) {
                return branchCommit;
            }
            branchCommit = getCommit(branchCommit.getParents().get(0));
        }
        return branchCommit;
    }
    /** return list of all commits in head branch. */
    private static List<String> getHeadCommits() {
        Commit headCommit = getHeadCommit();
        List<String> headCommits = new ArrayList<>();
        while (!headCommit.getParents().isEmpty()) {
            headCommits.add(headCommit.getUID());
            headCommit = getCommit(headCommit.getParents().get(0));
        }
        headCommits.add(headCommit.getUID());
        return headCommits;
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
    static List<String> getUntrackedFile() {
        List<String> untrackedFile = new ArrayList<>();
        Commit commit = getHeadCommit();
        HashMap<String, String> fileToBlob = commit.getFileToBlob();
        List<String> currentFile = plainFilenamesIn(CWD);
        if (currentFile != null) {
            for (String file :currentFile) {
                if (!fileToBlob.containsKey(file)) {
                    untrackedFile.add(file);
                }
            }
        }
        return untrackedFile;
    }
    /** return List of tracked files name in current branch. */
    static List<String> getTrackedFile() {
        List<String> untrackedFile = new ArrayList<>();
        Commit commit = getHeadCommit();
        HashMap<String, String> fileToBlob = commit.getFileToBlob();
        List<String> currentFile = plainFilenamesIn(CWD);
        if (currentFile != null) {
            for (String file :currentFile) {
                if (fileToBlob.containsKey(file)) {
                    untrackedFile.add(file);
                }
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

    private static void printStatusHeader(String header) {
        System.out.println("=== " + header + " ===");
    }
    /** return the name of current branch. */
    private static String getCurBranch() {
        return readContentsAsString(HEAD_FILE);
    }
    /** return if the file exists in CWD */
    private static Boolean fileExist(String fileName) {
        File file = join(CWD, fileName);
        return file.exists();
    }

    /** return list of all commit. */
    private static List<Commit> getAllCommit() {
        List<Commit> allCommitList = new ArrayList<>();
        List<String> allCommitDir = allFilenamesIn(COMMIT_DIR);
        assert allCommitDir != null;
        for (String dir : allCommitDir) {
            File commitDir = join(COMMIT_DIR, dir);
            List<String> allCommit = plainFilenamesIn(commitDir);
            assert allCommit != null;
            for (String commitName : allCommit) {
                allCommitList.add(readObject(join(commitDir, commitName), Commit.class));
            }
        }
        return allCommitList;
    }
}
