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
                // TODO: handle the `add [filename]` command
                Repository.dirCheck();
                Repository.validateNumArgs("commit", args, 2);
                Repository.commit(args[1]);
                break;
            default:
                MyUtils.exit("No command with that name exists.");
        }
    }

}
