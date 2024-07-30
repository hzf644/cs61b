package gitlet;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


class MyUtils {

    static void mkdir(File f) throws IOException {
        if(!f.exists()){
            if(!f.mkdir()){
                throw new IOException("cannot make directory "+f.getName());
            }
        }
    }

    static void createFile(File f) throws IOException{
        if(!f.exists()){
            if(!f.createNewFile()){
                throw new IOException("cannot create file "+f.getName());
            }
        }
    }

    static void deleteFile(File f) throws IOException{
        if(f.exists()) {
            if (!f.delete()) {
                throw new IOException("cannot delete file " + f.getName());
            }
        }
    }
}
