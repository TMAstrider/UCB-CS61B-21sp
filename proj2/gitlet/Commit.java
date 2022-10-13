package gitlet;

// TODO: any imports you need here

import javax.swing.plaf.synth.SynthTreeUI;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class

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

    private String timestamp;

    private String parent;

    private String id;

//    private
    /* TODO: fill in the rest of this class. */
    public Commit(String message, String parent) {
        this.message = message;
        this.parent = parent;

        if(this.parent == null) {
            this.timestamp = "00:00:00 UTC, Thursday, 1 January 1970";
        } else {
            this.timestamp = ".";
        }
    }

    public String getDate() {
        Date date = new Date();
        return date.toString();
    }
    public String getMessage() {
        return this.message;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getParent() {
        return this.parent;
    }
}
