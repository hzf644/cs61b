package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  record message of current commit and helps track previous commit
 *  @author TODO
 */
public class Commit implements Serializable{
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private final String message;

    private final String parent_id;

    private final String second_parent_id;

    private final String timeStamp;

    private String id;

    public HashMap<String, String> file_blob_map;

    public Date date;

    /* TODO: fill in the rest of this class. */

    //regular constructor for a regular commit
    public Commit(String msg, String p){
        parent_id = p;
        message = msg;
        second_parent_id = "";
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.CHINA);
        date = new Date();
        timeStamp = dateFormat.format(date);
        file_blob_map = new HashMap<>();
        id = "";
    }

    public void save(){
        File thisCommit = join(object, id);
        writeObject(thisCommit, this);
    }

    //special constructor for a merge commit
    public Commit(String msg, String p1, String p2){
        message = msg;
        parent_id = p1;
        second_parent_id = p2;
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        date = new Date();
        timeStamp = dateFormat.format(date);
        file_blob_map = new HashMap<>();
        id = "";
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return timeStamp;
    }

    public String getParentId() {
        return parent_id;
    }

    public String getSecondParentId() {
        return second_parent_id;
    }

    public Commit getParent(){
        File parent = join(object, parent_id);
        return readObject(parent, Commit.class);
    }

    public Commit getSecondParent(){
        File parent = join(object, second_parent_id);
        return readObject(parent, Commit.class);
    }

    public boolean isMerge(){
        return !second_parent_id.isEmpty();
    }

    public void setID(){
        id = sha1(message, parent_id, date.toString(), file_blob_map.toString());
    }

    public String getId() {
        return id;
    }

    public boolean isInitial(){
        return parent_id.isEmpty();
    }

    public boolean equals(Commit c){
        return id.equals(c.id);
    }
}
