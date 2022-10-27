package gitlet;

import java.io.File;
import java.io.IOException;
import static gitlet.Utils.*;
import static gitlet.Repository.commitInfo;

public class HeadAndBranch {


    /** Create a file with the given branchName and save the head commitId to branch */
    public static void saveBranchHead(String branchName) throws IOException {

        try {
            File branch = Utils.join(Repository.HEADS, branchName);
            if(!branch.exists()) {
                branch.createNewFile();
            }
            writeContents(branch, commitInfo.getId());
        } catch (IOException exception) {
            throw error("Internal error exists! ");
        }
    }

    public static void saveBranchHeadWithGivenBranchIdAndName(String branchName, String branchId) {
        File branch = Utils.join(Repository.HEADS, branchName);
        writeContents(branch, branchId);
    }

    /** Save the HeadCommitId to HeadFile */
    public static void saveHead() {
        writeContents(Repository.head, commitInfo.getId());
    }

    /** Save GivenCommitId to HeadFile */
    public static void saveHeadByGivenCommitId(String commitId) {
        writeContents(Repository.head, commitId);
    }

    /** Save currentBranchName to File: currentBranchName */
    public static void saveCurrentBranch(String branchName) {
        try {
            File currentBranch = Utils.join(Repository.HEADS, "currentBranchName");
            if (!currentBranch.exists()) {
                currentBranch.createNewFile();
            }
            writeContents(currentBranch, branchName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCurrentBranchName() {
        File currentBranchName = join(Repository.HEADS, "currentBranchName");
        return Utils.readContentsAsString(currentBranchName);
    }

    public static void saveBranchToHead(String branchId) {
        writeContents(Repository.head, branchId);
    }
}

