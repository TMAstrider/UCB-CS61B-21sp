package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;


// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TMAstrider
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */
    public static final String defaultBranch = "master";

    public static final String headPrefix = "ref: refs/heads/";

    public static final File OBJECTS = join(GITLET_DIR, "objects");

    public static final File REFS = join(GITLET_DIR, "refs");

    public static final File HEADS = join(REFS, "heads");

    public static final File head = join(GITLET_DIR, "HEAD");






    public static void dirCheck() {
        File cwd = new File(System.getProperty("user.dir"));
        if(!GITLET_DIR.exists()) {
            MyUtils.exit("Not in an initialized Gitlet directory.");
        }
    }
    public static void commandCheck(String[] cmd) {
        if(cmd.length == 0) {
            MyUtils.exit("Please enter a command.");
        }
    }

    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            MyUtils.exit("Incorrect operands.");
        }
    }

    /**
     * Initialize a repository at the current working directory.
     * (mimic original git repository)
     *
     * .gitlet
     * ├--- HEAD - File - current commit which current head is pointing
     * ├--- object - Folder - save the file
     * └── refs - Folder - save branch(es)
     *     └── heads
     */

    public static void init() throws IOException {

        Commit initial = new Commit("initial commit", null);
        if(GITLET_DIR.exists()) {
            MyUtils.exit("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        OBJECTS.mkdir();
        REFS.mkdir();
        HEADS.mkdir();

        head.createNewFile();

    }


}
