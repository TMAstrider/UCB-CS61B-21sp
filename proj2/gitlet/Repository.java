package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;
import static gitlet.HeadAndBranch.*;


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

    public static final File addStagingArea = join(GITLET_DIR, "addStage");

    public static final File rmStagingArea = join(GITLET_DIR, "rmStage");
    public static Commit commitInfo;





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
     * ├--- HEAD - File - current commit which current head pointed with
     * ├--- object - Folder - save the file
     * ├── refs - Folder - save branch(es)
     *     └── heads
     * └--- stage
     */
    public static void init() throws IOException {
        if(GITLET_DIR.exists()) {
            MyUtils.exit("A Gitlet version-control system already exists in the current directory.");
        }
        /* file directory initialization */
        GITLET_DIR.mkdir();
        OBJECTS.mkdir();
        REFS.mkdir();
        HEADS.mkdir();
        head.createNewFile();
        /* file directory initialization */
        createInitialCommit();
        HeadAndBranch.saveHead();
        HeadAndBranch.saveBranchHead(defaultBranch);
    }


    private static void createInitialCommit() {
        Commit initialCommit = new Commit();
        commitInfo = initialCommit;
        initialCommit.save();
    }

    /** Add method
     * Usage: java gitlet.Main add [file name]
     * */
    public static void add(String fileName) {
        File addedFile = getFile(fileName);
        if(!isFileExist(addedFile)) {
            MyUtils.exit("File does not exist.");
        }
        addToStagingArea(addedFile);


    }

    public static boolean isFileExist(File fileName) {
        return fileName.exists();
    }

    public static void addToStagingArea(File addedFile) {
        Blob addedFileBlob = new Blob(addedFile);
        StagingArea currentStage = addStageFromFile();
        if(!isFileMapExists(addedFileBlob) && !currentStage.isExist(addedFileBlob)) {
            currentStage.addFile(addedFileBlob.getFilePathName(), addedFileBlob.getBlobId());
            currentStage.save();
            addedFileBlob.save();
        }
    }

    public static StagingArea addStageFromFile() {
        return !addStagingArea.exists() ?
                new StagingArea() :
                Utils.readObject(addStagingArea, StagingArea.class);
    }


    public static boolean isFileMapExists(Blob addedFileBlob) {
        return getCurrentCommitMap().containsValue(addedFileBlob.getBlobId());
    }

    public static Commit getCurrentCommit() {
        String commitId = Utils.readContentsAsString(head);
        File commitFilePath = getFilePath(commitId);
        return Utils.readObject(commitFilePath, Commit.class);
    }

    public static File getFilePath(String id) {
        String dirName = id.substring(0, 2);
        String fileName = id .substring(2, 40);
        return Utils.join(Repository.OBJECTS, dirName, fileName);
    }


    public static Map<String, String> getCurrentCommitMap() {
        return getCurrentCommit().getMap();
    }

    /** Commit method */

    public static void commit(String commitMessage) throws IOException {
        Commit currentCommit = getNewCommit(commitMessage);
        saveCommit(currentCommit);
    }

    public static Commit getNewCommit(String commitMessage) {
        commitInfo = getCurrentCommit();
        commitInfo.saveParent();
        List<String> parents = commitInfo.getParentsList();
        Map<String, String> prevCommitMap = getCurrentCommitMap();
        Commit newCommit = new Commit(commitMessage, parents);
        newCommit.addPrevCommitMap(prevCommitMap);
        newCommit.addStagedFile(addStageFromFile());

        return newCommit;
    }

    public static void saveCommit(Commit newCommit) throws IOException {
        newCommit.save();
        commitInfo = newCommit;
        saveBranchHead(defaultBranch);
        saveHead();
        StagingArea addedStage = addStageFromFile();
        addedStage.clear();
        addedStage.save();
    }
    /** Helper method */
    public static File getFile(String fileName) {
        return join(CWD, fileName);
    }





}
