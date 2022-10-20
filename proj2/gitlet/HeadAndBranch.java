package gitlet;

import java.io.File;
import java.io.IOException;
import static gitlet.Utils.*;
import static gitlet.Repository.commitInfo;

public class HeadAndBranch {

    public static void saveBranchHead(String branchName) throws IOException {
        File branch = Utils.join(Repository.HEADS, branchName);
        if(!branch.exists()) {
            branch.createNewFile();
        }
        writeContents(branch, commitInfo.getId());
    }

    public static void saveHead() {
        writeContents(Repository.head, commitInfo.getId());
    }
}

