package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class StagingArea implements Serializable {
    private Map<String, String> added = new HashMap<>();

    public StagingArea() {
        added = new HashMap<>();
    }

    public void save() {
        Utils.writeObject(Repository.addStagingArea, this);
    }

    public void addFile(String key, String value) {
        added.put(key, value);
    }

    public Map<String, String> getAddedStageMap() {
        return added;
    }

    public void clear() {
        added.clear();
    }

    public boolean isExist(Blob stageBlob) {
        return added.containsValue(stageBlob.getBlobId());
    }
}
