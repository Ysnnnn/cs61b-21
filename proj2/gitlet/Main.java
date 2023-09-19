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
        if (args.length == 0) {
            exit("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                checkArgs(args, 1);
                init();
                break;
            case "add":
                checkArgs(args, 2);
                checkInitialized();
                add(args[1]);
                break;
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
                break;
            case "find":
                checkArgs(args, 2);
                checkInitialized();
                find(args[1]);
                break;
            case "status":
                checkArgs(args, 1);
                checkInitialized();
                status();
                break;
            case "checkout":
                switch(args.length) {
                    case 3:
                        if (!args[1].equals("--")) {
                            exit("Incorrect operands.");
                        }
                        checkout(args[2]);
                        break;
                    case 4:
                        if (!args[2].equals("--")) {
                            exit("Incorrect operands.");
                        }
                        checkout(args[1], args[3]);
                        break;
                    case 2:
                        checkoutBranch(args[1]);
                }
                break;
            case "branch":
                checkArgs(args, 2);
                checkInitialized();
                branch(args[1]);
                break;
            case "rm-branch":
                checkArgs(args, 2);
                checkInitialized();
                rmBranch(args[1]);
                break;
            case "reset":
                checkArgs(args, 2);
                checkInitialized();
                reset(args[1]);
                break;
            case "merge":
                checkArgs(args,2);
                checkInitialized();
                merge(args[1]);
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
