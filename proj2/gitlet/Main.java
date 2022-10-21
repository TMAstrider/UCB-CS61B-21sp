package gitlet;

import java.io.File;
import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TMAstrider
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     *  Usage: java gitlet.Main init
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?

        Repository.commandCheck(args);
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.validateNumArgs("init", args, 1);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                Repository.dirCheck();
                Repository.validateNumArgs("add", args, 2);
                Repository.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                Repository.dirCheck();
                Repository.validateNumArgs("commit", args, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                Repository.dirCheck();
                Repository.validateNumArgs("rm", args, 2);
                Repository.rm(args[1]);
                break;
            case "log":
                Repository.dirCheck();
                Repository.validateNumArgs("log", args, 1);
                Repository.log();
                break;
            case "global-log":
                Repository.dirCheck();
                Repository.validateNumArgs("global-log", args, 1);
                Repository.globalLog();
                break;
            case "find":
                Repository.dirCheck();
                Repository.validateNumArgs("find", args, 2);
                Repository.find(args[1]);
                break;
            case "status":
                Repository.dirCheck();
                Repository.validateNumArgs("status", args, 1);
                Repository.status();
                break;
            case "checkout":
                Repository.dirCheck();
                Repository.validateCheckoutArgs("checkout", args);
                break;
            case "branch":
                Repository.dirCheck();

                break;
            case "rm-branch":
                Repository.dirCheck();

                break;
            case "reset":
                Repository.dirCheck();

                break;
            case "merge":
                Repository.dirCheck();

                break;

            default:
                MyUtils.exit("No command with that name exists.");
        }
    }

}
