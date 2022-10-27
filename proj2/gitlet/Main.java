package gitlet;


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

        try {
            Repository.commandCheck(args);
            String firstArg = args[0];
            switch (firstArg) {
                case "init" -> {
                    // TODO: handle the `init` command
                    Repository.validateNumArgs("init", args, 1);
                    Repository.init();
                }
                case "add" -> {
                    // TODO: handle the `add [filename]` command
                    Repository.dirCheck();
                    Repository.validateNumArgs("add", args, 2);
                    Repository.add(args[1]);
                }
                // TODO: FILL THE REST IN
                case "commit" -> {
                    Repository.dirCheck();
                    Repository.validateNumArgs("commit", args, 2);
                    Repository.commit(args[1]);
                }
                case "rm" -> {
                    Repository.dirCheck();
                    Repository.validateNumArgs("rm", args, 2);
                    Repository.rm(args[1]);
                }
                case "log" -> {
                    Repository.dirCheck();
                    Repository.validateNumArgs("log", args, 1);
                    Repository.log();
                }
                case "global-log" -> {
                    Repository.dirCheck();
                    Repository.validateNumArgs("global-log", args, 1);
                    Repository.globalLog();
                }
                case "find" -> {
                    Repository.dirCheck();
                    Repository.validateNumArgs("find", args, 2);
                    Repository.find(args[1]);
                }
                case "status" -> {
                    Repository.dirCheck();
                    Repository.validateNumArgs("status", args, 1);
                    Repository.status();
                }
                case "checkout" -> {
                    Repository.dirCheck();
                    Repository.validateCheckoutArgs("checkout", args);
                }
                case "branch" -> {
                    Repository.dirCheck();
                    Repository.validateNumArgs("branch", args, 2);
                    Repository.branch(args[1]);
                }
                case "rm-branch" -> {
                    Repository.dirCheck();
                    Repository.validateNumArgs("rm-branch", args, 2);
                    Repository.rmBranch(args[1]);
                }
                case "reset" -> {
                    Repository.dirCheck();
                    Repository.validateNumArgs("reset", args, 2);
                    Repository.reset(args[1]);
                }
                case "merge" -> {
                    Repository.dirCheck();
                    Repository.validateNumArgs("merge", args, 2);
                    Repository.merge(args[1]);
                }
                default -> MyUtils.exit("No command with that name exists.");
            }
        } catch (IOException exception) {
            MyUtils.exit("Error!");
        }
    }

}
