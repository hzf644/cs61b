package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  record message of current commit and helps track previous commit
 *  @author TODO
 */
public class Commit implements Serializable{

    public static final File CWD = new File(System.getProperty("user.dir"));

    public static final File gitlet_dir = join(CWD, ".gitlet");

    public static final File staging_area = join(gitlet_dir, "staging_area");

    public static final File staged_for_removal = join(staging_area, "staged_for_removal");

    public static final File repository = join(gitlet_dir, "repository");

    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    public final String message;

    public final String branch;

    public final Commit parent;

    public final Commit second_parent;

    public final Date date;

    public HashMap<String, String> name_content_map;

    /* TODO: fill in the rest of this class. */
    public Commit(){
        branch = null;
        message = null;
        parent = null;
        date = null;
        second_parent = null;
        name_content_map = null;
    }

    //regular constructor for a regular commit
    public Commit(String msg, Commit p, String branch){
        this.branch = branch;
        parent = p;
        message = msg;
        second_parent = null;
        date = new Date();
        if(p!=null){
            name_content_map = new HashMap<>(p.name_content_map);
        }
        else{
            name_content_map = new HashMap<>();
        }
    }

    //special constructor for a merge commit
    public Commit(Commit p1, Commit p2){
        message = "Merged "+p2.branch+" into "+p1.branch;
        parent = p1;
        second_parent = p2;
        date = new Date();
        branch = p1.branch;
    }
}
