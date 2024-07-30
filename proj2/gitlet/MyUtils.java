package gitlet;

import java.io.File;
import java.io.IOException;


class MyUtils {

    static void mkdir(File f){
        if(!f.exists()){
            try{
                f.mkdir();
            }
            catch (SecurityException e){
                System.exit(-1);
            }
        }
    }

    static void createFile(File f){
        if(!f.exists()){
            try{
                f.createNewFile();
            }
            catch (IOException | SecurityException exception){
                System.exit(-1);
            }
        }
    }

    static void deleteFile(File f){
        if(f.exists()) {
            try{
                f.delete();
            }
            catch (SecurityException e){
                System.exit(-1);
            }
        }
    }
}
