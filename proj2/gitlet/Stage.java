package gitlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.io.File;
import java.util.Map;
import java.util.Set;

import static gitlet.Repository.*;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

class Stage implements Serializable {

    HashMap<String, String> fileName_blob_map = new HashMap<>();

    public void add_for_stage(File f) throws IOException {
        byte[] content = readContents(f);
        String blob = sha1(content);
        fileName_blob_map.put(f.getName(), blob);
        File stage_copy = join(Cache, blob);
        createFile(stage_copy);
        writeContents(stage_copy, content);
    }


    public void add_for_remove(File f) throws IOException{
        String blob = sha1(readContents(f));
        fileName_blob_map.put(f.getName(), blob);
    }

    public void remove_from_add(File f) throws IOException {
        String blob = fileName_blob_map.remove(f.getName());
        if(blob != null){
            File stage_copy = join(Cache, blob);
            deleteFile(stage_copy);
        }
    }

    public void remove_from_remove(File f) throws IOException{
        fileName_blob_map.remove(f.getName());
    }

    public static Stage getAdd(){
        return readObject(addStage, Stage.class);
    }

    public static Stage getRemove(){
        return readObject(removeStage, Stage.class);
    }

    public void saveAdd(){
        writeObject(addStage, this);
    }

    public void saveRemove(){
        writeObject(removeStage, this);
    }

    public static void clearStage() throws IOException {
        File[] files = Cache.listFiles();
        if(files != null) {
            for (File file : files) {
                deleteFile(file);
            }
        }
        Stage add = getAdd();
        Stage remove = getRemove();
        add.fileName_blob_map = new HashMap<>();
        remove.fileName_blob_map = new HashMap<>();
        add.saveAdd();
        remove.saveRemove();
    }

    public static void keepInTrack() throws IOException {
        File[] files = Cache.listFiles();
        if(files != null){
            for(File file : files){
                byte[] content = readContents(file);
                File blob = join(blobs, file.getName());
                createFile(blob);
                writeContents(blob, content);
            }
        }
    }

    public boolean isStaged(File f){
        String blob = sha1(readContents(f));
        return blob.equals(fileName_blob_map.get(f.getName()));
    }

    public boolean isStaged(String fileName, String blob){
        return blob.equals(fileName_blob_map.get(fileName));
    }

    public boolean ifAnythingStaged(){
        return !fileName_blob_map.isEmpty();
    }
}
