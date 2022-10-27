package gitlet;
import edu.princeton.cs.algs4.Merge;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static gitlet.Utils.*;
import static gitlet.HeadAndBranch.*;



/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author TMAstrider
 */
public class Repository {
    /** List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you. */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final String defaultBranch = "master";
    public static final File OBJECTS = join(GITLET_DIR, "objects");
    public static final File COMMIT = join(OBJECTS, "commit");
    public static final File BLOB = join(OBJECTS, "blob");
    public static final File REFS = join(GITLET_DIR, "refs");
    public static final File HEADS = join(REFS, "heads");
    public static final File head = join(GITLET_DIR, "HEAD");
    public static final File addStagingArea = join(GITLET_DIR, "addStage");
    public static final File rmStagingArea = join(GITLET_DIR, "rmStage");
    public static final File BYTE_TEMP = join(REFS, "byteTemp");
    public static Commit commitInfo;





    public static void dirCheck() {
        File cwd = new File(System.getProperty("user.dir"));
        if (!GITLET_DIR.exists()) {
            MyUtils.exit("Not in an initialized Gitlet directory.");
        }
    }
    public static void commandCheck(String[] cmd) {
        if (cmd.length == 0) {
            MyUtils.exit("Please enter a command.");
        }
    }

    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            MyUtils.exit("Incorrect operands.");
        }
    }

    public static void validateCommit(String cmd, String[] args, int n) {
        if (args.length > n) {
            MyUtils.exit("Incorrect operands.");
        }
    }

    /**
     * Initialize a repository at the current working directory.
     * (mimic original git repository)
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
        if (GITLET_DIR.exists()) {
            MyUtils.exit("A Gitlet version-control system already exists in the current directory.");
        }
        /* file directory initialization */

        try {
            GITLET_DIR.mkdir();
            OBJECTS.mkdir();
            COMMIT.mkdir();
            BLOB.mkdir();
            REFS.mkdir();
            HEADS.mkdir();
            head.createNewFile();
            BYTE_TEMP.createNewFile();
            /* file directory initialization */
            createInitialCommit();
            HeadAndBranch.saveHead();
            HeadAndBranch.saveBranchHead(defaultBranch);
        } catch (IOException exception) {
            throw error("Internal error exists! ");
        }
        HeadAndBranch.saveCurrentBranch(defaultBranch);
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
        if (!isFileExist(addedFile)) {
            MyUtils.exit("File does not exist.");
        }
        if (!checkIdenticalFile(addedFile)) {
            addToStagingArea(addedFile);
        }
    }

    public static boolean checkIdenticalFile(File fileName) {
        boolean isIdentical = false;
        Blob addedFileBlob = new Blob(fileName);
        StagingArea addArea = addStageFromFile();
        StagingArea rmArea = rmStageFromFile();
        if (isCommitMapExists(addedFileBlob)) {
            isIdentical = true;
            if (rmArea.getStageMap().containsValue(addedFileBlob.getBlobId())) {
                rmArea.rmFile(addedFileBlob);
                rmArea.saveRmStage();
            }
            if (addArea.getStageMap().containsValue(addedFileBlob.getBlobId())) {
                addArea.rmFile(addedFileBlob);
                addArea.saveAddStage();
            }
        }
        return isIdentical;

    }


    public static boolean isFileExist(File fileName) {
        return fileName.exists();
    }

    public static void addToStagingArea(File addedFile) {
        Blob addedFileBlob = new Blob(addedFile);
        StagingArea addArea = addStageFromFile();
        if (!isCommitMapExists(addedFileBlob) && !addArea.isExist(addedFileBlob)) {
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
        return Utils.join(Repository.COMMIT, id);
    }


    public static Map<String, String> getCurrentCommitMap() {
        return getCurrentCommit().getMap();
    }


    /** Commit method */
    public static void commit(String commitMessage) throws IOException {
        try {
            if (checkMessage(commitMessage)) {
                MyUtils.exit("Please enter a commit message.");
            }
            Commit currentCommit = getNewCommit(commitMessage);
            saveCommit(currentCommit);
        } catch (IOException exception) {
            throw error("Error!");
        }
    }

    public static Commit getNewCommit(String commitMessage) {
        checkBrandNew(addStageFromFile(), rmStageFromFile());
        commitInfo = getCurrentCommit();
        List<String> parents = new ArrayList<>();
        parents.add(getCurrentCommit().getId());
        Map<String, String> prevCommitMap = getCurrentCommitMap();
        Commit newCommit = new Commit(commitMessage, parents);
        newCommit.addPrevCommitMap(prevCommitMap);
        newCommit.addStagedFile(addStageFromFile());
        newCommit.rmStagedFile(rmStageFromFile());
        return newCommit;
    }


    public static void saveCommit(Commit newCommit) throws IOException {

        try {
            newCommit.save();
            commitInfo = newCommit;
            saveBranchHead(HeadAndBranch.getCurrentBranchName());
            saveHead();
            StagingArea addArea = addStageFromFile();
            addArea.clear();
            addArea.saveAddStage();
            StagingArea rmArea = rmStageFromFile();
            rmArea.clear();
            rmArea.saveRmStage();
        } catch (IOException exception) {
            throw error("Internal error exists! ");
        }
    }

    public static void checkBrandNew(StagingArea addArea, StagingArea rmArea) {
        if (addArea.getStageMap().isEmpty() && rmArea.getStageMap().isEmpty()) {
            MyUtils.exit("No changes added to the commit.");
        }
    }
    public static boolean checkMessage(String commitMessage) {
        return commitMessage.equals("");
    }

    /** Remove method */
    public static void rm(String fileName) {
        File rmFile = getFile(fileName);
        if (!isFileExist(rmFile)) {
            rmFromStagingAreaDeleted(fileName);
        } else {
            rmFromStagingArea(rmFile);
        }


    }
    public static void rmFromStagingAreaDeleted(String fileName) {
        File file = getFile(fileName);

        StagingArea rmArea = rmStageFromFile();
        StagingArea addArea = addStageFromFile();
        if (isCommitMapExistFileName(fileName)) {

            rmArea.addFile(file.getPath(), getCurrentCommitMap().get(file.getPath()));
            rmArea.saveRmStage();
        } else if (isAddStageExistFileName(fileName)) {
            addArea.rmFile(file);
            addArea.saveAddStage();
        }
    }
    public static boolean isAddStageExistFileName(String fileName) {
        return addStageFromFile().isExistFileName(getFile(fileName));
    }
    public static void rmFromStagingArea(File rmFile) {
        Blob rmFileBlob = new Blob(rmFile);
        StagingArea rmArea = rmStageFromFile();
        StagingArea addArea = addStageFromFile();
        if (isCommitMapExists(rmFileBlob)) {
            rmArea.addFile(rmFileBlob.getFilePathName(),rmFileBlob.getBlobId());
            rmArea.saveRmStage();
            rmFile.delete();
        } else if (isAddStageExist(rmFileBlob)) {
            addArea.rmFile(rmFileBlob);
            addArea.saveAddStage();
        } else {
            MyUtils.exit("No reason to remove the file.");
        }

    }
    public static boolean isCommitMapExistFileName(String fileName) {
        return getCurrentCommitMap().containsKey(getFile(fileName).getPath());
    }
    public static boolean isAddStageExist(Blob rmFileBlob) {
        return addStageFromFile().isExist(rmFileBlob);
    }

    public static StagingArea rmStageFromFile() {
        if (!rmStagingArea.exists()) {
            return new StagingArea();
        }
        return readObject(rmStagingArea, StagingArea.class);
    }


    /** Helper method
     * Using FilePath to get File Object */
    public static File getFile(String fileName) {
        return join(CWD, fileName);
    }

    public static File getBranchFile(String branchName) {
        return join(HEADS, branchName);
    }

    /** Git log
     *
     * ===
     * commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
     * Date: Thu Nov 9 20:00:05 2017 -0800
     * A commit message.
     *
     * Include a blank line */
    public static void log() {
        Commit currentCommit = getCurrentCommit();
        printOutSelf(currentCommit);
        while (!currentCommit.getParent().isEmpty()) {
            currentCommit = getCommitFromFile(currentCommit.getParent().get(0));
            if (currentCommit.getParent().size() == 2) {
                printOutMergeCommitInFormat(currentCommit);
            } else {
                printOutLogInFormat(currentCommit);
            }


        }
    }
    public static void printOutSelf(Commit commitInfo) {
        printOutLogInFormat(commitInfo);
    }
    public static void printOutLogInFormat(Commit tempCommit) {
        System.out.println("===");
        System.out.println("commit " + tempCommit.getId());
        System.out.println("Date: " + tempCommit.getStrTimestamp());
        System.out.println(tempCommit.getMessage());
        System.out.println();
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
        for (String temp : commitIdList) {
            Commit commitTemp = getCommitFromFile(temp);
            if (commitTemp.getParent().size() == 2) {
                printOutMergeCommitInFormat(commitTemp);
            } else {
                printOutGlobalLogInFormat(commitTemp);
            }
        }
    }
    public static void printOutMergeCommitInFormat(Commit tempCommit) {
        System.out.println("===");
        System.out.println("commit " + tempCommit.getId());
        String firstParent = tempCommit.getParent().get(0).substring(0, 7);
        String secondParent = tempCommit.getParent().get(1).substring(0, 7);
        System.out.println("Merge: " + firstParent + " " + secondParent);
        System.out.println("Date: " + tempCommit.getStrTimestamp());
        System.out.println(tempCommit.getMessage());
        System.out.println();
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
        for (String temp : commitIdList) {
            Commit commitTemp = getCommitFromFile(temp);
            if (printOutCommitWithMsg(commitTemp, commitMessage)) {
                withMessage = true;
            }
        }
        isWithoutMessage(withMessage);
    }

    public static boolean printOutCommitWithMsg(Commit commitTemp, String commitMessage) {
        boolean withMessage = false;
        if (commitMessage.equals(commitTemp.getMessage())) {
            System.out.println(commitTemp.getId());
            withMessage = true;
        }
        return withMessage;
    }

    public static void isWithoutMessage(boolean withMessage) {
        if (!withMessage) {
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
        for (Map.Entry<String, String> entry : addArea.getStageMap().entrySet()) {
            System.out.println(cutOffCwd(entry.getKey()));
        }
        System.out.println();
    }

    public static void printOutRemovedFile() {
        StagingArea rmArea = rmStageFromFile();
        System.out.println("=== Removed Files ===");
        for (Map.Entry<String, String> entry : rmArea.getStageMap().entrySet()) {
            System.out.println(cutOffCwd(entry.getKey()));
        }
        System.out.println();
    }

    public static void printOutEC() {
        System.out.println(
                "=== Modifications Not Staged For Commit ===" + "\n"
                        + "\n"
                        + "=== Untracked Files ===" + "\n"
                        + "\n"
        );
    }

    public static void printOutBranches(List<String> branchList) {
        String headCommitId = getHeadBranchCommitId();
        System.out.println("=== Branches ===");
        for (String branchTemp : branchList) {
            if (branchTemp.equals("currentBranchName")) {
                continue;
            }
            if (getBranchFileContents(branchTemp).equals(headCommitId)) {
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

    /** fileName: simply branchName. e.g. master, main, etc. */
    public static String getBranchFileContents(String fileName) {
        File branch = join(HEADS, fileName);
        return readContentsAsString(branch);
    }

    public static List<String> getBranchList() {
        return Utils.plainFilenamesIn(HEADS);
    }




    /** Git checkout
     * java gitlet.Main checkout -- [file name]
     * ------------------------------------------------------
     * java gitlet.Main checkout [commit id] -- [file name]
     * ------------------------------------------------------
     * java gitlet.Main checkout [branch name]
     * */
    public static void validateCheckoutArgs(String cmd, String[] args) {
        switch (args.length) {
            case 2 -> checkoutBranch(args[1]);
            case 3 -> {
                checkDash(args[1]);
                checkoutCurrentCommitFile(args[2]);
            }
            case 4 -> {
                checkDash(args[2]);
                checkoutPrevCommitFile(args[1], args[3]);
            }
            default -> MyUtils.exit("Incorrect operands.");
        }
    }
    public static void checkDash(String dash) {
        if (!dash.equals("--")) {
            MyUtils.exit("Incorrect operands.");
        }
    }
    /** java gitlet.Main checkout [branch name] */
    public static void checkoutBranch(String branchName) {
        checkFailure(branchName);
        Commit branchCommit = getCommitFromFile(getBranchFileContents(branchName));
        Map<String, String> branchCommitMap = branchCommit.getMap();
        pickUpRestoreSameFile(branchCommitMap);
        pickUpDeleteFile(branchCommitMap);
        pickUpCreateFile(branchCommitMap);
        saveHeadAsCurrBranch(branchName);
        saveBranchAsCurrentBranch(branchName);
        clearBothStage();
    }
    public static void saveHeadAsCurrBranch(String branchName) {
        saveBranchToHead(getBranchFileContents(branchName));
    }

    public static void saveBranchAsCurrentBranch(String branchName) {
        HeadAndBranch.saveCurrentBranch(branchName);
    }

    public static void pickUpDeleteFile(Map<String, String> branchCommitMap) {
        Map<String, String> currentCommitMap = getCurrentCommitMap();
        if (!branchCommitMap.isEmpty()) {
            for (Map.Entry<String, String> entry : branchCommitMap.entrySet()) {
                String temp = entry.getKey();
                if (currentCommitMap.containsKey(temp)) {
                    currentCommitMap.remove(temp);
                }
            }
        }
        deleteFile(currentCommitMap);
    }
    public static void deleteFile(Map<String, String> removedFileMap) {
        if (!removedFileMap.isEmpty()) {
            for (Map.Entry<String, String> entry : removedFileMap.entrySet()) {
                String temp = entry.getKey();
                File removedFile = getFile(cutOffCwd(temp));
                removedFile.delete();
            }
        }
    }
    public static void pickUpCreateFile(Map<String, String> branchCommitMap) {
        Map<String, String> currentCommitMap = getCurrentCommitMap();
        for (Map.Entry<String, String> entry : currentCommitMap.entrySet()) {
            String temp = entry.getKey();
            if (branchCommitMap.containsKey(temp)) {
                branchCommitMap.remove(temp);
            }
        }
        createNewFile(branchCommitMap);
    }
    public static void createNewFile(Map<String, String> addedFileMap) {
        if (!addedFileMap.isEmpty()) {
            for (Map.Entry<String, String> entry : addedFileMap.entrySet()) {
                String temp = entry.getKey();
                File newFile = getFile(cutOffCwd(temp));
                restoreFile(newFile, addedFileMap);
            }
        }
    }
    public static void pickUpRestoreSameFile(Map<String, String> branchCommitMap) {
        for (Map.Entry<String, String> entry : branchCommitMap.entrySet()) {
            restoreBranchSameFile(entry.getKey(), branchCommitMap);
        }
    }
    public static void restoreBranchSameFile(String fileName, Map<String, String> addedFileMap) {
        File restoredFile = getFile(cutOffCwd(fileName));



        if (getCurrentCommitMap().containsKey(fileName)) {
            restoreFile(restoredFile, addedFileMap);
        }
    }

    /** Check failure cases for java gitlet.Main checkout [branch name] */
    public static void checkFailure(String branchName) {
        if (!branchExist(branchName)) {
            MyUtils.exit("No such branch exists.");
        } else if (isBranchHeadSame(branchName)) {
            MyUtils.exit("No need to checkout the current branch.");
        } else {
            dealWithUntracked(branchName);
        }
    }

    public static void dealWithUntracked(String branchName) {
        Commit branch = getCommitFromFile(getBranchFileContents(branchName));
        Map<String, String> branchMap = branch.getMap();

        for (Map.Entry<String, String> entry : branchMap.entrySet()) {
            String temp = entry.getKey();
            if (!getCurrentCommitMap().containsKey(temp)) {
                if (getFile(cutOffCwd(temp)).exists()){
                    MyUtils.exit("There is an untracked file in the way; " +
                            "delete it, or add and commit it first.");
                }
            }
        }
    }

    /** Check if given branchName exist. e.g. branchExist(master); */
    public static boolean branchExist(String branchName) {
        return getBranchList().contains(branchName);
    }

    public static boolean isBranchHeadSame(String branchName) {
        return HeadAndBranch.getCurrentBranchName().equals(branchName);
    }


    /** java gitlet.Main checkout [commit id] -- [file name] */
    public static void checkoutPrevCommitFile(String commitId, String fileName) {
        if (commitId.length() < 40) {
            for (String commit : getCurrentCommitIdList()) {
                if (commitId.equals(commit.substring(0, commitId.length()))) {
                    commitId = commit;
                }
            }
        }
        File restoredFile = getFile(fileName);
        if (!checkCommitIdExist(commitId)) {
            MyUtils.exit("No commit with that id exists.");
        }
        if (!isExistInCurCommit(restoredFile)) {
            MyUtils.exit("File does not exist in that commit.");
        }
        restoreFileFromCommit(commitId, restoredFile);
        unStageFile(restoredFile);
    }

    public static void restoreFileFromCommit(String commitId, File restoredFile) {
        commitInfo = getCommitFromFile(commitId);
        String restoredFileId = commitInfo.getMap().get(restoredFile.getPath());
        Blob restoredBlob = restoreUniqueBlob(restoredFileId);
        Utils.writeContents(restoredFile, restoredBlob.getBytes());
    }

    public static Blob restoreUniqueBlob(String id) {
        File restoredBlob = join(BLOB, id);
        return Utils.readObject(restoredBlob, Blob.class);
    }
    public static boolean checkCommitIdExist(String commitId) {
        if (commitId.length() < 40) {
            for (String commit : getCurrentCommitIdList()) {
                if (commitId.equals(commit.substring(0, commitId.length()))) {
                    return true;
                }
            }
        } else {
            return getCurrentCommitIdList().contains(commitId);
        }
        return false;
    }



    /** java gitlet.Main checkout -- [file name] */
    public static void checkoutCurrentCommitFile(String fileName) {
        File restoredFile = getFile(fileName);
        if (!isExistInCurCommit(restoredFile)) {
            MyUtils.exit("File does not exist in that commit.");
        }
        restoreFile(restoredFile, getCurrentCommitMap());
        unStageFile(restoredFile);
    }


    public static void unStageFile(File tobeUnStage) {
        StagingArea addArea = addStageFromFile();
        addArea.rmFile(tobeUnStage);
        addArea.saveAddStage();
        StagingArea rmArea = rmStageFromFile();
        rmArea.rmFile((tobeUnStage));
        rmArea.saveRmStage();
    }

    public static boolean isExistInCurCommit(File restoredFile) {
        return getCurrentCommitMap().containsKey(restoredFile.getPath());
    }

    public static void restoreFile(File restoredFile, Map<String, String> addedFileMap) {
        Blob restoredBlob = restoreBlob(restoredFile, addedFileMap);
        try {
            if (!restoredFile.exists()) {
                restoredFile.createNewFile();
            }
        } catch (IOException e) {
            throw error("Error!");
        }
//        if (restoredFile.exists()) {
//            System.out.println(restoredFile.getPath());
//        }
        Utils.writeContents(restoredFile, restoredBlob.getBytes());
    }

    public static Blob restoreBlob(File restoredFile, Map<String, String> addedFileMap) {
        String restoredId = addedFileMap.get(restoredFile.getPath());
        return getBlobFromFile(restoredId);
    }

    public static Blob getBlobFromFile(String id) {
        File restoredBlob = join(BLOB, id);
        return Utils.readObject(restoredBlob, Blob.class);
    }


    /** Git branch */
    public static void branch(String branchName) throws IOException {

        try {
            checkBranchExist(branchName);
            commitInfo = getCurrentCommit();
            HeadAndBranch.saveBranchHead(branchName);
        } catch (IOException exception) {
            throw error("Internal error exists!");
        }
    }

    public static void checkBranchExist(String branchName) {
        if (getBranchList().contains(branchName)) {
            MyUtils.exit("A branch with that name already exists.");
        }
    }


    /** Git rmBranch */

    public static void rmBranch(String branchName) {
        isBranchExist(branchName);
        isCurrentBranch(branchName);
        deleteBranch(branchName);
    }


    public static void isBranchExist(String branchName) {
        if (!getBranchList().contains(branchName)) {
            MyUtils.exit("A branch with that name does not exist.");
        }
    }

    public static void isCurrentBranch(String branchName) {
        if (branchName.equals(getCurrentBranchName())) {
            MyUtils.exit("Cannot remove the current branch.");
        }
    }

    public static String getCurrentBranchName() {
        for (String branch : getBranchList()) {
            if (getBranchFileContents(branch).equals(getHeadBranchCommitId())) {
                return branch;
            }
        }
        return null;
    }

    public static void deleteBranch(String branchName) {
        File tobeDel =  join(HEADS, branchName);
        tobeDel.delete();
    }

    /** Git reset */
    public static void reset(String commitId) {
        commitIdExist(commitId);
        Map<String, String> givenCommitMap = getCommitFromFile(commitId).getMap();
        Map<String, String> currentCommitMap = getCurrentCommitMap();
        dealWithUntrackedByMap(givenCommitMap);
        pickUpRestoreSameFile(givenCommitMap);
        pickUpCreateFile(givenCommitMap);
        pickUpDeleteFile(givenCommitMap);
        clearBothStage();
        moveBranchHeadToCommit(commitId);
    }
    public static void dealWithUntrackedByMap(Map<String, String> branchMap) {
        for (Map.Entry<String, String> entry : branchMap.entrySet()) {
            String temp = entry.getKey();
            if (!getCurrentCommitMap().containsKey(temp)) {
                if (getFile(cutOffCwd(temp)).exists()){
                    MyUtils.exit("There is an untracked file in the way; " +
                            "delete it, or add and commit it first.");
                }
            }
        }
    }

    public static String readCurrentBranch() {
        File currentBranchName = join(HEADS, "currentBranchName");
        return readContentsAsString(currentBranchName);
    }

    public static void moveBranchHeadToCommit(String commitId) {
        HeadAndBranch.saveBranchHeadWithGivenBranchIdAndName(readCurrentBranch(),
               commitId);
        HeadAndBranch.saveHeadByGivenCommitId(commitId);
    }

    /** Check the given commitId,
     * if id don't exist, exit with the msg */
    public static void commitIdExist(String commitId) {
        if (!checkCommitIdExist(commitId)) {
            MyUtils.exit("No commit with that id exists.");
        }
    }
    public static void clearBothStage() {
        StagingArea addArea = addStageFromFile();
        StagingArea rmArea = rmStageFromFile();
        addArea.clear();
        rmArea.clear();
        addArea.saveAddStage();
        rmArea.saveRmStage();
    }




    /** Git merge
     * branchName: the branch to be merged with current branch */
    public static void merge(String branchName) throws IOException {
        checkStageEmpty();
        checkMergeBranchExist(branchName);
        checkSelf(branchName);
        dealWithUntracked(branchName);
        mergePreCheck(branchName);
        Map<String, String> splitPointMap = getCommitFromFile(getSplitPoint(branchName)).getMap();
        Map<String, String> branchMap = getCommitFromFile(getBranchFileContents(branchName)).getMap();
        dealWithCaseOne(splitPointMap, branchMap);
        dealWithCaseTwo(splitPointMap, branchMap);
        dealWithCaseThree(splitPointMap, branchMap);
        dealWithCaseFour(splitPointMap, branchMap);
        dealWithCaseFive(splitPointMap, branchMap);
        dealWithCaseSix(splitPointMap, branchMap);

        dealWithCaseSeven(splitPointMap, branchMap);

        /* conflict file */
        dealWithCaseEight(splitPointMap, branchMap);
        try {
            mergeCommit(branchName);
        } catch (IOException exception) {
            throw error("Error");
        }

    }
    public static void checkMergeBranchExist(String branchName) {
        if (!getBranchList().contains(branchName)) {
            MyUtils.exit("A branch with that name does not exist.");
        }
    }
    /** Case 8 */
    public static void dealWithCaseEight(Map<String, String> splitPointMap, Map<String, String> branchMap) {
        Map<String, String> conflictMap = getCaseEight(splitPointMap, branchMap);
        if (!conflictMap.isEmpty()) {
            System.out.println("Encountered a merge conflict.");
            restoreConflictFile(conflictMap);
        }
    }
    public static Commit getNewMergeCommit(String commitMessage, String branchName) {
        checkBrandNew(addStageFromFile(), rmStageFromFile());
        List<String> parents = new ArrayList<>();
        parents.add(getCurrentCommit().getId());
        parents.add(getBranchFileContents(branchName));
        Map<String, String> prevCommitMap = getCurrentCommitMap();
        Commit newCommit = new Commit(commitMessage, parents);
        newCommit.addPrevCommitMap(prevCommitMap);
        newCommit.addStagedFile(addStageFromFile());
        newCommit.rmStagedFile(rmStageFromFile());
        return newCommit;
    }


    public static void mergeCommit(String branchName) throws IOException {
        String currentId = getCurrentCommit().getId();
        String branchId = getBranchFileContents(branchName);
        StringBuilder commitMessage = new StringBuilder();
        commitMessage.append("Merged ");
        commitMessage.append(branchName);
        commitMessage.append(" into ");
        commitMessage.append(getCurrentBranchName());
        commitMessage.append(".");
        try {
            Commit currentCommit = getNewMergeCommit(commitMessage.toString(), branchName);
            saveCommit(currentCommit);
        } catch (IOException exception) {
            throw error("Error");
        }
    }
    public static File readTemp() {
        return null;
    }
    public static void restoreAndReplace(String fileName, String currentId, String branchId) {
        StringBuilder Header = new StringBuilder();
        Header.append("<<<<<<< HEAD\n");
        Header.append(getCurrentContents(currentId));
        Header.append("=======\n");
        Header.append(getBranchContents(branchId));
        Header.append(">>>>>>>\n");
        String fileContents = Header.toString();
        byte[] contents = fileContents.getBytes(StandardCharsets.UTF_8);
        File currentFile = join(CWD, cutOffCwd(fileName));
        writeContents(currentFile, fileContents);
    }
    public static String getCurrentContents(String currentId) {
        if (currentId.equals("0")) {
            return "";
        }
        Blob restoreBlob = getBlobFromFile(currentId);
        return new String(restoreBlob.getBytes(), StandardCharsets.UTF_8);
    }
    public static String getBranchContents(String branchId) {
        if (branchId.equals("0")) {
            return "";
        }
        Blob restoreBlob = getBlobFromFile(branchId);
        return new String(restoreBlob.getBytes(), StandardCharsets.UTF_8);
    }
    public static void restoreConflictFile(Map<String, String> conflictMap) {
        for (Map.Entry<String, String> entry : conflictMap.entrySet()) {
            String currentFile = entry.getKey();
            File restoredFile = getFile(cutOffCwd(currentFile));
            if (!getCurrentCommitMap().containsKey(currentFile)) {
                restoreAndReplace(currentFile, "0", entry.getValue());
            } else if (entry.getValue().equals("0")) {
                restoreAndReplace(currentFile, getCurrentCommitMap().get(currentFile), "0");
            } else {
                restoreAndReplace(currentFile, getCurrentCommitMap().get(currentFile), entry.getValue());
            }
        }
    }
    public static Map<String, String> getCaseEight(Map<String, String> splitPointMap, Map<String, String> branchMap) {
        Map<String, String> FileMap = new HashMap<>();
        Map<String, String> currentMap = getCurrentCommitMap();
        /* both changed but with different contents */
        for (Map.Entry<String, String> entry : splitPointMap.entrySet()) {
            String splitKey = entry.getKey();
            String splitValue = entry.getValue();
            if (currentMap.containsKey(splitKey) && branchMap.containsKey(splitKey)) {
                if (!currentMap.get(splitKey).equals(splitValue) && !branchMap.get(splitKey).equals(splitValue)) {
                    FileMap.put(splitKey, branchMap.get(splitKey));
                }
            }
        }
        /* one changed and one deleted */
        for (Map.Entry<String, String> entry : splitPointMap.entrySet()) {
            String splitKey = entry.getKey();
            String splitValue = entry.getValue();
            if (currentMap.containsKey(splitKey) && !branchMap.containsKey(splitKey)) {
                if (!currentMap.get(splitKey).equals(splitValue)) {
                    FileMap.put(splitKey, "0");
                }
            } else if (!currentMap.containsKey(splitKey) && branchMap.containsKey(splitKey)) {
                if (!branchMap.get(splitKey).equals(splitValue)) {
                    FileMap.put(splitKey, branchMap.get(splitKey));
                }
            }
        }
        /* not present in splitPoint but present in both branches with different contents */
        for (Map.Entry<String, String> entry : branchMap.entrySet()) {
            String branchKey = entry.getKey();
            String branchValue = entry.getValue();
            if (currentMap.containsKey(branchKey) && !splitPointMap.containsKey(branchKey)) {
                if (!currentMap.get(branchKey).equals(branchValue)) {
                    FileMap.put(branchKey, branchMap.get(branchKey));
                }
            }
        }
        return FileMap;
    }

    /** Case 7-- */
    public static void dealWithCaseSeven(Map<String, String> splitPointMap, Map<String, String> branchMap) {

    }
    public static Map<String, String> getCaseSeven(Map<String, String> splitPointMap, Map<String, String> branchMap) {
        Map<String, String> FileMap = new HashMap<>();
        Map<String, String> currentMap = getCurrentCommitMap();
        for (Map.Entry<String, String> entry : splitPointMap.entrySet()) {
            String splitKey = entry.getKey();
            String splitValue = entry.getValue();
            if (!currentMap.containsKey(splitKey) && branchMap.containsKey(splitKey)) {
                if (branchMap.get(splitKey).equals(splitValue)) {
                    FileMap.put(splitKey, splitValue);
                }
            }
        }
        return FileMap;
    }

    /** Case 6 */
    public static void dealWithCaseSix(Map<String, String> splitPointMap, Map<String, String> branchMap) {
        Map<String, String> removeAndUntrackedMap = getCaseSix(splitPointMap, branchMap);
    }
    public static Map<String, String> getCaseSix(Map<String, String> splitPointMap, Map<String, String> branchMap) {
        Map<String, String> FileMap = new HashMap<>();
        Map<String, String> currentMap = getCurrentCommitMap();
        for (Map.Entry<String, String> entry : splitPointMap.entrySet()) {
            String splitKey = entry.getKey();
            String splitValue = entry.getValue();
            if (currentMap.containsKey(splitKey) && !branchMap.containsKey(splitKey)) {
                if (currentMap.get(splitKey).equals(splitValue)) {
                    FileMap.put(splitKey, splitValue);
                    rm(cutOffCwd(splitKey));
                }
            }
        }
        return FileMap;
    }
    /** Case 5 */
    public static void dealWithCaseFive(Map<String, String> splitPointMap, Map<String, String> branchMap) {
        Map<String, String> checkBranchFileMap = getCaseFive(splitPointMap, branchMap);
        for (Map.Entry<String, String> entry : checkBranchFileMap.entrySet()) {
            File restoredFile = getFile(cutOffCwd(entry.getKey()));
            restoreFile(restoredFile, checkBranchFileMap);
            add(cutOffCwd(entry.getKey()));
        }
    }
    public static Map<String, String> getCaseFive(Map<String, String> splitPointMap, Map<String, String> branchMap) {
        Map<String, String> FileMap = new HashMap<>();
        Map<String, String> currentMap = getCurrentCommitMap();
        for (Map.Entry<String, String> entry : branchMap.entrySet()) {
            String branchKey = entry.getKey();
            String branchValue = entry.getValue();
            if (!currentMap.containsKey(branchKey) && !splitPointMap.containsKey(branchKey)) {
                FileMap.put(branchKey, branchValue);
            }
        }
        return FileMap;
    }
    /** Case 4-- */
    public static void dealWithCaseFour(Map<String, String> splitPointMap, Map<String, String> branchMap) {

    }
    public static Map<String, String> getCaseFour(Map<String, String> splitPointMap, Map<String, String> branchMap) {
        return null;
    }
    /** Case 3** */
    public static void dealWithCaseThree(Map<String, String> splitPointMap, Map<String, String> branchMap) {
        Map<String, String> caseMap = getCaseThree(splitPointMap, branchMap);
    }

    public static Map<String, String> getCaseThree(Map<String, String> splitPointMap, Map<String, String> branchMap) {
        Map<String, String> FileMap = new HashMap<>();
        Map<String, String> currentMap = getCurrentCommitMap();
        for (Map.Entry<String, String> entry : splitPointMap.entrySet()) {
            String splitKey = entry.getKey();
            String splitValue = entry.getValue();
            if (currentMap.containsKey(splitKey) && branchMap.containsKey(splitKey)) {
                if (!currentMap.get(splitKey).equals(splitValue) && !branchMap.get(splitKey).equals(splitValue)) {
                    if (currentMap.get(splitKey).equals(branchMap.get(splitKey))) {
                        FileMap.put(splitKey, splitValue);
                    }
                }
            } else if (!currentMap.containsKey(splitKey) && !branchMap.containsKey(splitKey)) {
                FileMap.put(splitKey, splitValue);
                File removedFile = getFile(cutOffCwd(splitKey));
                if (removedFile.exists()) {
                    removedFile.delete();
                    StagingArea addArea = addStageFromFile();
                    if (addArea.isExistFileName(removedFile)) {
                        addArea.rmFile(removedFile);
                    }
                    addArea.saveAddStage();
                }
            }
        }
        return FileMap;
    }


    /** Case 2--
     * Files that have been modified in the current but not in branch */
    public static void dealWithCaseTwo(Map<String, String> splitPointMap, Map<String, String> branchMap) {

    }
    public static Map<String, String> getCaseTwo(Map<String, String> splitPointMap, Map<String, String> branchMap) {
        Map<String, String> FileMap = new HashMap<>();
        Map<String, String> currentMap = getCurrentCommitMap();
        for (Map.Entry<String, String> entry : splitPointMap.entrySet()) {
            String splitKey = entry.getKey();
            String splitValue = entry.getValue();
            if (currentMap.containsKey(splitKey) && branchMap.containsKey(splitKey)) {
                if (!currentMap.get(splitKey).equals(splitValue) && branchMap.get(splitKey).equals(splitValue)) {
                    FileMap.put(splitKey, branchMap.get(splitKey));
                }
            }
        }
        return FileMap;
    }

    /** Case 1**
     * Files that have been modified in the branch but not in the current branch */
    public static void dealWithCaseOne(Map<String, String> splitPointMap, Map<String, String> branchMap) {
        Map<String, String> overwriteFileMap = getCaseOne(splitPointMap, branchMap);

        if (!overwriteFileMap.isEmpty()) {
            pickUpRestoreSameFile(overwriteFileMap);
            addMapToStage(overwriteFileMap);
        }
    }
    public static void addMapToStage(Map<String, String> addMap) {
        for (Map.Entry<String, String> entry : addMap.entrySet()) {
            add(cutOffCwd(entry.getKey()));
        }
    }
    public static Map<String, String> getCaseOne(Map<String, String> splitPointMap, Map<String, String> branchMap) {
        Map<String, String> FileMap = new HashMap<>();
        Map<String, String> currentMap = getCurrentCommitMap();
        for (Map.Entry<String, String> entry : splitPointMap.entrySet()) {
            String splitKey = entry.getKey();
            String splitValue = entry.getValue();
            if (currentMap.containsKey(splitKey) && branchMap.containsKey(splitKey)) {
                if (currentMap.get(splitKey).equals(splitValue) && !branchMap.get(splitKey).equals(splitValue)) {
                    FileMap.put(splitKey, branchMap.get(splitKey));
                }
            }
        }
        return FileMap;
    }
    /** Any files have been modified in the current branch
     * but not in the given branch since the split point */
    public static void getStaySameFileMap(Map<String, String> splitPointCommitMap) {

    }


    public static String getSplitPoint(String branchName) {
        String splitPoint = null;
        String branchCommitId = getBranchFileContents(branchName);
        Commit branchCommit = getCommitFromFile(branchCommitId);
        List<String> branchParentList = branchCommit.getParentsList();
        List<String> currentParentList= getCurrentCommit().getParentsList();
        for (int currentCommitIndex = currentParentList.size() - 1; currentCommitIndex >= 0; currentCommitIndex--) {
            String tempCurrentCommitId = currentParentList.get(currentCommitIndex);
            for (int branchIndex = branchParentList.size() - 1; branchIndex >= 0; branchIndex--) {
                if (tempCurrentCommitId.equals(branchParentList.get(branchIndex))) {
                    splitPoint = tempCurrentCommitId;
                    return splitPoint;
                }
            }
        }
        return splitPoint;
    }
    public static void checkSelf(String branchName) {
        if (getCurrentBranchName().equals(branchName)) {
            MyUtils.exit("Cannot merge a branch with itself.");
        }
    }
    public static void checkStageEmpty() {
        StagingArea addArea = addStageFromFile();
        StagingArea rmArea = rmStageFromFile();
        if (!addArea.isEmpty() || !rmArea.isEmpty()) {
            MyUtils.exit("You have uncommitted changes.");
        }
    }
    public static void mergeWithoutConflict(String branchName) {

    }
    /** Case ancestor*/
    public static void mergePreCheck(String branchName) {
        checkAncestor(branchName);
        checkFastForward(branchName);
    }
    public static void checkAncestor(String branchName) {
        Commit currentCommit = getCurrentCommit();
        Commit branchCommit = getCommitFromFile(getBranchFileContents(branchName));
        while (!currentCommit.getParent().isEmpty()) {
            if (branchCommit.getId().equals(currentCommit.getId())) {
                MyUtils.exit("Given branch is an ancestor of the current branch.");
            }
            currentCommit = getCommitFromFile(currentCommit.getParent().get(0));
        }
    }
    public static void checkFastForward(String branchName) {
        Commit currentCommit = getCurrentCommit();
        Commit branchCommit = getCommitFromFile(getBranchFileContents(branchName));
        while (!branchCommit.getParent().isEmpty()) {
            if (branchCommit.getId().equals(currentCommit.getId())) {
                checkoutBranch(branchName);
                MyUtils.exit("Current branch fast-forwarded.");
            }
            branchCommit = getCommitFromFile(branchCommit.getParent().get(0));
        }
    }
}





