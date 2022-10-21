package gitlet;

import java.io.File;
import java.io.Serializable;


public class Blob implements Serializable {
    private String id;
    private File fileName;
    private String filePathName;
    private File savedFileName;
    private byte[] bytes ;

    public Blob(File argFile) {
        this.fileName = argFile;
        filePathName = this.fileName.getPath();
        bytes = readContents(this.fileName);
        id = generateId();
        savedFileName = getFilePath(id);
    }

    public byte[] readContents(File file) {
        return Utils.readContents(file);
    }


    public String generateId() {
        return Utils.sha1(bytes, filePathName);
    }


    public String getBlobId() {
        return id;
    }

    public void save() {
        if(!savedFileName.getParentFile().exists()) {
            savedFileName.getParentFile().mkdir();
        }
        Utils.writeObject(savedFileName, this);
    }

    public String getFilePathName() {
        return filePathName;
    }
    public File getFilePath(String id) {
        String dirName = id.substring(0, 2);
        String fileName = id .substring(2, 40);

        return Utils.join(Repository.BLOB, id);
    }


}
