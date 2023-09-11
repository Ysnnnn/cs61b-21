package gitlet;

import java.io.File;
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
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     *     .gitlet
     *      *      |--objects
     *      *      |     |--commit and blob
     *      *      |--refs
     *      *      |    |--heads
     *      *      |         |--master
     *      *      |--HEAD
     *      *      |--index
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** object directory to store commit and blob. */
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    /** refs directory to store heads. */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /** heads directory to store master. */
    public static final File HEADS_DIR = join(GITLET_DIR, "heads");
    /* TODO: fill in the rest of this class. */
    static void init() {
        if (GITLET_DIR.exists()) {
            exit("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();
        Commit initCommit = new Commit();
        initCommit.saveCommit();
        changeMaster(initCommit.getUID());
    }
    public static void changeMaster(String commitUID) {
        writeContents(join(HEADS_DIR, "master"), commitUID);
    }
}
