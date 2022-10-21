package gitlet;

import javax.xml.namespace.QName;
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

    public static final File COMMIT = join(OBJECTS, "commit");

    public static final File BLOB = join(OBJECTS, "blob");
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

    public static void validateCommit(String cmd, String[] args, int n) {
        if(args.length > n) {
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
     *        └── commit
     *        └── blob
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
        COMMIT.mkdir();
        BLOB.mkdir();
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
        StagingArea addArea = addStageFromFile();
        if(!isCommitMapExists(addedFileBlob) && !addArea.isExist(addedFileBlob)) {
            addArea.addFile(addedFileBlob.getFilePathName(), addedFileBlob.getBlobId());
            addArea.saveAddStage();
            addedFileBlob.save();
        }
    }

    public static StagingArea addStageFromFile() {
        return !addStagingArea.exists() ?
                new StagingArea() :
                Utils.readObject(addStagingArea, StagingArea.class);
    }


    public static boolean isCommitMapExists(Blob addedFileBlob) {
        return getCurrentCommitMap().containsValue(addedFileBlob.getBlobId());
    }

    public static Commit getCurrentCommit() {
        String commitId = Utils.readContentsAsString(head);
        File commitFilePath = getCommitFilePath(commitId);
        return Utils.readObject(commitFilePath, Commit.class);
    }


    /** get commitPath which stores commit serialization stuff */
    public static File getCommitFilePath(String id) {
        String dirName = id.substring(0, 2);
        String fileName = id .substring(2, 40);
        return Utils.join(Repository.COMMIT, id);
    }


    public static Map<String, String> getCurrentCommitMap() {
        return getCurrentCommit().getMap();
    }


    /** Commit method */
    public static void commit(String commitMessage) throws IOException {
        if(checkMessage(commitMessage)) {
            MyUtils.exit("Please enter a commit message.");
        }
        Commit currentCommit = getNewCommit(commitMessage);
        saveCommit(currentCommit);
    }

    public static Commit getNewCommit(String commitMessage) {
        checkBrandNew(addStageFromFile(), rmStageFromFile());

        commitInfo = getCurrentCommit();
        commitInfo.saveParent();
        List<String> parents = commitInfo.getParentsList();
        Map<String, String> prevCommitMap = getCurrentCommitMap();
        Commit newCommit = new Commit(commitMessage, parents);
        newCommit.addPrevCommitMap(prevCommitMap);
        newCommit.addStagedFile(addStageFromFile());
        newCommit.rmStagedFile(rmStageFromFile());

        return newCommit;
    }

    public static void saveCommit(Commit newCommit) throws IOException {
        newCommit.save();
        commitInfo = newCommit;
        saveBranchHead(defaultBranch);
        saveHead();
        StagingArea addArea = addStageFromFile();
        addArea.clear();
        addArea.saveAddStage();
        StagingArea rmArea = rmStageFromFile();
        rmArea.clear();
        rmArea.saveRmStage();
    }

    public static void checkBrandNew(StagingArea addArea, StagingArea rmArea) {
        if(addArea.getStageMap().isEmpty() && rmArea.getStageMap().isEmpty()) {
            MyUtils.exit("No changes added to the commit.");
        }
    }
    public static boolean checkMessage(String commitMessage) {
        if(commitMessage == "") {
            return true;
        }
        return false;
    }

    /** Remove method */
    public static void rm(String fileName) {
        File rmFile = getFile(fileName);
        if(!isFileExist(rmFile)) {
            MyUtils.exit("File does not exist.");
        }
        rmFromStagingArea(rmFile);
    }



    public static void rmFromStagingArea(File rmFile) {
        Blob rmFileBlob = new Blob(rmFile);
        StagingArea rmArea = rmStageFromFile();
        StagingArea addArea = addStageFromFile();
        if(isCommitMapExists(rmFileBlob)) {
            rmArea.addFile(rmFileBlob.getFilePathName(),rmFileBlob.getBlobId());
            rmArea.saveRmStage();
            rmFile.delete();
        } else if(isAddStageExist(rmFileBlob)) {
            addArea.rmFile(rmFileBlob);
        } else {
            MyUtils.exit("No reason to remove the file.");
        }
        addArea.saveAddStage();
    }
    public static boolean isAddStageExist(Blob rmFileBlob) {
        return addStageFromFile().isExist(rmFileBlob);
    }

    public static StagingArea rmStageFromFile() {
        if(!rmStagingArea.exists()) {
            return new StagingArea();
        }
        return readObject(rmStagingArea, StagingArea.class);
    }


    /** Helper method
     * Using FilePath to get File Object */
    public static File getFile(String fileName) {
        return join(CWD, fileName);
    }



    /** Git log
     *
     * ===
     * commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
     * Date: Thu Nov 9 20:00:05 2017 -0800
     * A commit message.
     *
     * Include a blank line
     * */

    public static void log() {
        commitInfo = getCurrentCommit();
        List<String> parentsList = commitInfo.getParentsList();
        printOutSelf(commitInfo);
        for(int anySize = parentsList.size(); anySize > 0; anySize--) {
            Commit tempCommit = getCommitFromFile(parentsList.get(anySize - 1));
            printOutLogInFormat(tempCommit);
        }
    }


    public static void printOutSelf(Commit commitInfo) {
        printOutLogInFormat(commitInfo);
    }
    public static void printOutLogInFormat(Commit tempCommit) {
        System.out.println(
                "===\n" +
                "commit " + tempCommit.getId() + "\n" +
                "Date: " + tempCommit.getStrTimestamp() + "\n" +
                tempCommit.getMessage() + "\n" +
                "\n"
        );
    }

    /** */
    public static Commit getCommitFromFile(String commitId) {
        File commitFilePath = getCommitFilePath(commitId);
        return Utils.readObject(commitFilePath, Commit.class);
    }


    /** Git global-log
     * Print out all the commit
     * ===
     * commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
     * Date: Thu Nov 9 20:00:05 2017 -0800
     * A commit message.
     *
     * Include a blank line
     */
    public static void globalLog() {
        List<String> commitIdList = getCurrentCommitIdList();
        for(String temp : commitIdList) {
            Commit commitTemp = getCommitFromFile(temp);
            printOutGlobalLogInFormat(commitTemp);
        }
    }

    public static void printOutGlobalLogInFormat(Commit commitTemp) {
        printOutLogInFormat(commitTemp);
    }

    public static List<String> getCurrentCommitIdList() {
        return Utils.plainFilenamesIn(COMMIT);
    }



    /** Git find */
    public static void find(String commitMessage) {
        List<String> commitIdList = getCurrentCommitIdList();
        boolean withMessage = false;
        for(String temp : commitIdList) {
            Commit commitTemp = getCommitFromFile(temp);
            if(printOutCommitWithMsg(commitTemp, commitMessage)) {
                withMessage = true;
            }
        }
        isWithoutMessage(withMessage);
    }

    public static boolean printOutCommitWithMsg(Commit commitTemp, String commitMessage) {
        boolean withMessage = false;
        if(commitMessage.equals(commitTemp.getMessage())) {
            System.out.println(commitTemp.getId());
            withMessage = true;
        }
        return withMessage;
    }

    public static void isWithoutMessage(boolean withMessage) {
        if(!withMessage) {
            MyUtils.exit("Found no commit with that message.");
        }
    }



    /** Git status
     * === Branches ===
     * *master
     * other-branch
     *
     * === Staged Files ===
     * wug.txt
     * wug2.txt
     *
     * === Removed Files ===
     * goodbye.txt
     *
     * === Modifications Not Staged For Commit ===
     * junk.txt (deleted)
     * wug3.txt (modified)
     *
     * === Untracked Files ===
     * random.stuff
     *
     * */

    public static void status() {
        List<String> branchList = getBranchList();
        printOutBranches(branchList);
        printOutStagedFile();
        printOutRemovedFile();
        printOutEC();
    }

    public static String cutOffCwd(String filePathName) {
        int beginEnd = CWD.getPath().length() + 1;
        int endEnd = filePathName.length();
        return filePathName.substring(beginEnd, endEnd);
    }

    public static void printOutStagedFile() {
        StagingArea addArea = addStageFromFile();
        System.out.println("=== Staged Files ===");
        for(Map.Entry<String, String> entry : addArea.getStageMap().entrySet()) {
            System.out.println(cutOffCwd(entry.getKey()));
        }
        System.out.println();
    }

    public static void printOutRemovedFile() {
        StagingArea rmArea = rmStageFromFile();
        System.out.println("=== Removed Files ===");
        for(Map.Entry<String, String> entry : rmArea.getStageMap().entrySet()) {
            System.out.println(entry.getKey());
        }
        System.out.println();
    }

    public static void printOutEC() {
        System.out.println(
                "=== Modifications Not Staged For Commit ===" + "\n" +
                "\n" +
                "=== Untracked Files ===" + "\n" +
                "\n"
        );
    }

    public static void printOutBranches(List<String> branchList) {
        String headCommitId = getHeadBranchCommitId();
        System.out.println("=== Branches ===");
        for(String branchTemp : branchList) {
            if(getBranchFileContents(branchTemp).equals(headCommitId)) {
                System.out.println("*" + branchTemp);
            } else {
                System.out.println(branchTemp);
            }
        }
        System.out.println();
    }

    public static String getHeadBranchCommitId() {
        return readContentsAsString(head);
    }

    public static String getBranchFileContents(String fileName) {
        File branch = join(HEADS, fileName);
        return readContentsAsString(branch);
    }

    public static List<String> getBranchList() {
        return Utils.plainFilenamesIn(HEADS);
    }



    /** Git checkout */

    public static void validateCheckoutArgs(String cmd, String[] args) {

    }
    public static void checkout() {

    }
}
