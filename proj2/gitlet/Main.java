package gitlet;

import static gitlet.Commit.*;
import static gitlet.MyHelperFunction.*;
import static gitlet.Repository.*;
/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Ysnnnn
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            exit("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                checkArgs(args, 1);
                init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                checkArgs(args, 2);
                checkInitialized();
                add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                checkArgs(args, 2);
                checkInitialized();
                commit(args[1]);
                break;
            case "rm":
                checkArgs(args, 2);
                checkInitialized();
                rm(args[1]);
                break;
            case "log":
                checkArgs(args, 1);
                checkInitialized();
                log();
                break;
            case "global-log":
                checkArgs(args, 1);
                checkInitialized();
                globalLog();
            default:
                exit("No command with that name exists.");
        }
    }
    static void checkArgs(String[] args, int validNumber) {
        if (args.length != validNumber) {
            exit("Incorrect operands.");
        }
    }
    static void checkInitialized() {
        if (!GITLET_DIR.exists()) {
            exit("Not in an initialized Gitlet directory.");
        }
    }
}
