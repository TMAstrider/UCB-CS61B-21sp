package gitlet;

// TODO: any imports you need here

import javax.swing.plaf.synth.SynthTreeUI;
import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TMAstrider
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date timestamp;
    private List<String> parents;
    private String id;
    private String strTimestamp;

    /**
     * The tracked file Map with
     * filePath as key
     * file Sha1 value as value
     * */
    private Map<String, String> filePathId = new HashMap<>();
    private File savedFileName;

//    private
    /* TODO: fill in the rest of this class. */
    public Commit(String commitMessage, List<String> parent) {
        this.message = commitMessage;
        this.parents = parent;
        this.timestamp = new Date();
        id = generateId();
        savedFileName = getFilePath(id);
        strTimestamp = getTimestamp(timestamp);
    }

    public Commit() {
        message = "initial commit";
        parents = new ArrayList<>();
        timestamp = new Date(0);
        id = generateId();
        savedFileName = getFilePath(id);
        strTimestamp = getTimestamp(timestamp);
    }

    public void save() {
        if(!savedFileName.getParentFile().exists()) {
            savedFileName.getParentFile().mkdir();
        }
        Utils.writeObject(savedFileName, this);
    }
    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public String getTimestamp(Date referredTimestamp) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        return dateFormat.format(referredTimestamp);
    }

    public String generateId(){
        return Utils.sha1(message.toString(), timestamp.toString(), getParent());
    }

    public File getFilePath(String id) {
        String dirName = id.substring(0, 2);
        String fileName = id .substring(2, 40);

        return Utils.join(Repository.OBJECTS, dirName, fileName);
    }

    public String getParent() {
        return parents.size() == 0 ? "null" : parents.get(parents.size() - 1);
    }

    public List<String> getParentsList() {
        return parents;
    }

    public Map<String, String> getMap() {
        return filePathId;
    }

    public void saveParent() {
        parents.add(id);
    }

    public void saveCommitMessage(String savedMessage) {
        message = savedMessage;
    }

    public void addStagedFile(StagingArea stage) {
        Map<String, String> addedStageMap = stage.getAddedStageMap();
        for(Map.Entry<String, String> entry : addedStageMap.entrySet()) {
            filePathId.put(entry.getKey(), entry.getValue());
        }
    }

    public void addPrevCommitMap(Map<String, String> prevCommitMap) {
        for(Map.Entry<String, String> entry : prevCommitMap.entrySet()) {
            filePathId.put(entry.getKey(), entry.getValue());
        }
    }



}
