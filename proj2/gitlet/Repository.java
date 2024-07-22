package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File gitlet_dir = join(CWD, ".gitlet");//directory

    public static final File staging_area = join(gitlet_dir, "staging_area");//directory

    public static final File repository = join(gitlet_dir, "repository");//directory

    public static final File staged_for_removal = join(staging_area, "staged_for_removal");//directory

    public static final File branches = join(gitlet_dir, "branches");//directory

    public static final File head = join(gitlet_dir, "head");//file

    public static final File current_branch = join(gitlet_dir, "current_branch");//file

    public static final File all_commmits = join(gitlet_dir, "all_commits");//file

    /* TODO: fill in the rest of this class. */
    private static Commit getHead(){
        Commit ret = new Commit();
        ret = readObject(head, ret.getClass());
        return ret;
    }

    private static void setHead(Commit c){
        writeObject(head, c);
    }

    private static String getCurrentBranch(){
        return readContentsAsString(current_branch);
    }

    private static void extendBranch(String branchName, Commit c){
        File branch = join(branches, branchName);
        extendBranch(branch, c);
    }

    private static void extendBranch(File branch, Commit c){
        if(!branch.exists()){
            throw new GitletException("Error! Cannot find branch "+branch.getName());
        }
        writeObject(branch, c);
    }

    private static void addCommit(Commit c){
        HashMap<String, Commit> all = new HashMap<>();
        all = readObject(all_commmits, all.getClass());
        all.put(sha1(serialize(c)), c);
        writeObject(all_commmits, all);
    }

    private static HashMap<String, Commit> getAllCommit(){
        HashMap<String, Commit> ret = new HashMap<>();
        ret = readObject(all_commmits, ret.getClass());
        return ret;
    }

    private static Commit getBranchHead(String branchName){
        File branch = join(branches, branchName);
        if(!branch.exists()){
            throw new GitletException("Error! Cannot find branch "+branchName);
        }
        Commit ret = new Commit();
        ret = readObject(branch, ret.getClass());
        return ret;
    }

    private static void staging_area_clear(){
        File[] f = staging_area.listFiles();
        if(f != null){
            for (File file : f) {
                if(file.isFile()) {
                    file.delete();
                }
            }
        }
        f = staged_for_removal.listFiles();
        if(f != null){
            for(File file : f){
                if(file.isFile()){
                    file.delete();
                }
            }
        }
    }

    public static void init() throws IOException {

        if(!gitlet_dir.exists()){

            if(!gitlet_dir.mkdir()){
                throw new GitletException("Error! Cannot init a repository");
            }

            if(!staging_area.exists()){
                if(!staging_area.mkdir()){
                    throw new GitletException("Error! Cannot create the staging area");
                }
            }

            if(!staged_for_removal.exists()){
                if(!staged_for_removal.mkdir()){
                    throw new GitletException("Error! Cannot create the staging area");
                }
            }

            if(!repository.exists()){
                if(!repository.mkdir()){
                    throw new GitletException("Error! Cannot create repository");
                }
            }

            if(!branches.exists()){
                if(!branches.mkdir()){
                    throw new GitletException("Error! Cannot init a repository");
                }
            }

            if(!head.exists()){
                if(!head.createNewFile()){
                    throw new GitletException("Error! Cannot init a repository");
                }
            }

            if(!current_branch.exists()){
                if(!current_branch.createNewFile()){
                    throw new GitletException("Error! Cannot create a master branch");
                }
            }

            if(!all_commmits.exists()){
                if(!all_commmits.createNewFile()){
                    throw new GitletException("Error Cannot init a repository");
                }
            }



            Commit initial = new Commit("initial commit", null, "master");

            HashMap<String, Commit> all = new HashMap<>();
            all.put(sha1(serialize(initial)), initial);
            writeObject(all_commmits, all);

            setHead(initial);

            createBranch("master");
            switchBranch("master");
        }
        else{
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
    }

    public static void add(String fileName) throws IOException{
        Commit head = getHead();
        File ready = join(CWD, fileName);
        if(!ready.exists()){
            throw new GitletException("Error! Cannot find file "+fileName);
        }
        byte[] content = readContents(ready);
        String sha = sha1(content);
        String cur_version = head.name_content_map.get(fileName);

        //if version is same
        if(sha.equals(cur_version)){
            File staged = join(staging_area, fileName);
            staged.delete();
        }
        //if version is different
        else{
            File staged = join(staging_area, fileName);
            if(!staged.exists()){
                if(!staged.createNewFile()){
                    throw new GitletException("Error! Cannot stage file "+fileName);
                }
            }
            writeContents(staged, content);
        }
    }

    public static void commit(String message) throws IOException{

        Commit head = getHead();
        String branch = getCurrentBranch();
        Commit new_commit = new Commit(message, head, branch);
        addCommit(new_commit);

        File[] f = staging_area.listFiles();
        if(f != null){
            for (File file : f) {
                if(file.isFile()) {
                    byte[] content = readContents(file);
                    String sha1 = sha1(content);
                    new_commit.name_content_map.put(file.getName(), sha1);

                    File blob = join(repository, sha1);
                    if (!blob.exists()) {
                        if (!blob.createNewFile()) {
                            throw new GitletException("Oops! Error when committing!");
                        }
                    }
                    writeContents(blob, content);

                    file.delete();
                }
            }
        }

        f = staged_for_removal.listFiles();
        if(f != null){
            for(File file : f){
                if(file.isFile()){
                    new_commit.name_content_map.remove(file.getName());
                    file.delete();
                }
            }
        }

        setHead(new_commit);
        extendBranch(branch, new_commit);
    }

    private static void switchBranch(String branchName){
        writeContents(current_branch, branchName);
        File branch_pointer = join(branches, branchName);
        writeContents(head, readContents(branch_pointer));
    }

    public static void remove(String fileName) throws IOException {
        File isStaged = join(staging_area, fileName);
        if(isStaged.exists()){
            isStaged.delete();
        }
        Commit head = getHead();
        if(head.name_content_map.containsKey(fileName)){
            File forRemoval = join(staged_for_removal, fileName);
            File realRemoval = join(CWD, fileName);
            if(!forRemoval.exists()){
                if(!forRemoval.createNewFile()){
                    throw new GitletException("Error! Cannot remove file "+fileName);
                }
            }
            if(realRemoval.exists()){
                realRemoval.delete();
            }
        }
    }

    public static void log(){
        Commit head = getHead();
        SimpleDateFormat formatter = new SimpleDateFormat("E MMM hh:mm:ss yyyy +0800");
        while(head != null){
            if(head.second_parent == null) {
                String sha = sha1(serialize(head));
                System.out.println("===\n");
                System.out.println("commit " + sha);
                System.out.println("Date: " + formatter.format(head.date));
                System.out.println(head.message);
                System.out.print("\n");
                head = head.parent;
            }
            else{
                String sha = sha1(serialize(head));
                System.out.println("===\n");
                System.out.println("commit " + sha);
                String s1 = sha1(serialize(head.parent)).substring(0, 6);
                String s2 = sha1(serialize(head.second_parent)).substring(0, 6);
                System.out.println("Merge: "+s1+" "+s2);
                System.out.println("Date: " + formatter.format(head.date.toInstant()));
                System.out.println(head.message);
                head = head.parent;
            }
        }
    }

    public static void global_log(){
        HashMap<String, Commit> all = getAllCommit();
        Set<String> keys = all.keySet();
        Commit head = new Commit();
        SimpleDateFormat formatter = new SimpleDateFormat("E MMM hh:mm:ss yyyy +0800");
        for(String key : keys){
            head = all.get(key);
            if(head.second_parent == null) {
                System.out.println("===\n");
                System.out.println("commit " + key);
                System.out.println("Date: " + formatter.format(head.date));
                System.out.println(head.message);
                System.out.print("\n");
            }
            else{
                System.out.println("===\n");
                System.out.println("commit " + key);
                String s1 = sha1(serialize(head.parent)).substring(0, 6);
                String s2 = sha1(serialize(head.second_parent)).substring(0, 6);
                System.out.println("Merge: "+s1+" "+s2);
                System.out.println("Date: " + formatter.format(head.date.toInstant()));
                System.out.println(head.message);
            }
        }
    }

    public static void find(String message){
        HashMap<String, Commit> all = getAllCommit();
        Set<String> keys = all.keySet();
        Commit head = new Commit();
        for(String key : keys){
            head = all.get(key);
            if(head.message.equals(message))System.out.println(key);
        }
    }

    public static void status(){

        System.out.println("=== Branches ===");
        List<String> b = plainFilenamesIn(branches);
        String cur = getCurrentBranch();
        assert b != null;
        for(String branch : b){
            if(cur.equals(branch))System.out.print('*');
            System.out.println(branch);
        }
        System.out.print('\n');

        System.out.println("=== Staged Files ===");
        List<String> f = plainFilenamesIn(staging_area);
        if(f != null){
            f.sort(String::compareTo);
            for(String name : f){
                System.out.println(name);
            }
        }
        System.out.print('\n');

        System.out.println("=== Removed Files ===");
        f = plainFilenamesIn(staged_for_removal);
        if(f != null){
            f.sort(String::compareTo);
            for(String name : f){
                System.out.println(name);
            }
        }
        System.out.print('\n');

        System.out.println("=== Modifications Not Staged For Commit ===");
        Commit head = getHead();
        HashMap<String, String> map = head.name_content_map;
        List<String> file = plainFilenamesIn(CWD);
        file.sort(String::compareTo);
        List<String> out = new ArrayList<>();
        for(String name : file){
            File current = join(CWD, name);
            String sha1 = sha1(readContents(current));
            File staged = join(staging_area, name);
            String sha2 = sha1(readContents(staged));
            if(!staged.exists() && map.containsKey(name) && !map.get(name).equals(sha1)){
                out.add(name+" (modified)");
            }
            else if(staged.exists() && !sha1.equals(sha2)){
                out.add(name+" (modified)");
            }
        }
        file =plainFilenamesIn(staging_area);
        if(file != null) {
            file.sort(String::compareTo);
            for (String name : file) {
                File current = join(CWD, name);
                if (!current.exists()) {
                    out.add(name + " (deleted)");
                }
            }
        }
        Set<String> keys = map.keySet();
        for(String key : keys){
            File removal = join(staged_for_removal, key);
            File current = join(CWD, key);
            if(!current.exists() && !removal.exists()){
                out.add(key + " (deleted)");
            }
        }

        for(String item : out){
            System.out.println(item);
        }
        System.out.print('\n');

        System.out.println("=== Untracked Files ===");
        file = plainFilenamesIn(CWD);
        if(file != null) {
            file.sort(String::compareTo);
            for (String name : file) {
                File staged = join(staging_area, name);
                if (!staged.exists() && !map.containsKey(name)){
                    System.out.println(name);
                }
            }
        }
        System.out.print('\n');
    }

    public static void checkout_file(String file_name) throws IOException {
        Commit head = getHead();
        String sha = head.name_content_map.get(file_name);
        if(sha != null) {
            File blob = join(repository, sha);
            if (!blob.exists()) {
                throw new GitletException("Error! Cannot find old versions of file " + file_name);
            }
            File cur = join(CWD, file_name);
            if(!cur.exists())cur.createNewFile();
            writeContents(cur, readContents(blob));
        }
        else System.out.println("File does not exist in that commit.");
    }

    public static void checkout_commit(String commit_id, String file_name) throws IOException {
        HashMap<String, Commit> all = getAllCommit();
        Commit head = all.get(commit_id);
        if(head != null) {
            String sha = head.name_content_map.get(file_name);
            File blob = join(repository, sha);
            if (!blob.exists()) {
                throw new GitletException("Error! Cannot find old versions of file " + file_name);
            }
            File cur = join(CWD, file_name);
            if(!cur.exists())cur.createNewFile();
            writeContents(cur, readContents(blob));
        }
        else System.out.println("No commit with that id exists");
    }

    public static void checkout_branch(String branchName){
        String cur_branch = getCurrentBranch();
        Commit head = getHead();
        File branch = join(branches, branchName);

        if(cur_branch.equals(branchName)){
            System.out.println("No need to checkout the current branch.");
        }
        else{
            if(!branch.exists()){
                System.out.println("No such branch exists");
            }
            else{
                List<String> files = plainFilenamesIn(CWD);
                if(files != null) {
                    for (String file : files) {
                        if(!head.name_content_map.containsKey(file)){
                            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                            break;
                        }
                    }
                }

                head = getBranchHead(branchName);
                if(files != null){
                    for(String file : files){
                        File cur = join(CWD, file);
                        if(!head.name_content_map.containsKey(file)){
                            cur.delete();
                        }
                        else{
                            String sha = head.name_content_map.get(file);
                            File blob = join(repository, sha);
                            String sha1 = sha1(readContents(cur));
                            if(!sha1.equals(sha)){
                                writeContents(cur, readContents(blob));
                            }
                        }
                    }
                }
                staging_area_clear();
            }
        }

        switchBranch(branchName);
    }

    public static void createBranch(String branchName) throws IOException {
        File branch = join(branches, branchName);
        if(!branch.exists()){
            if(!branch.createNewFile()){
                throw new GitletException("Error! Cannot create branch "+branchName);
            }
        }
        writeObject(branch, getHead());
    }

    public static void removeBranch(String branchName){
        File branch = join(branches, branchName);
        if(!branch.exists()){
            System.out.println("A branch with that name does not exist.");
        }
        else if(branchName.equals(getCurrentBranch())){
            System.out.println("Cannot remove the current branch.");
        }
        else{
            branch.delete();
        }
    }

    public static void reset(String id) throws IOException {
        HashMap<String, Commit> all = getAllCommit();
        Commit head = all.get(id);
        if(head != null) {
            Set<String> files = head.name_content_map.keySet();
            for (String file_name : files) {
                String sha = head.name_content_map.get(file_name);
                File blob = join(repository, sha);
                if (!blob.exists()) {
                    throw new GitletException("Error! Cannot find old versions of file " + file_name);
                }
                File cur = join(CWD, file_name);
                if(!cur.exists())cur.createNewFile();
                writeContents(cur, readContents(blob));
                staging_area_clear();
            }
        }
        else{
            System.out.println("No commit with that id exists.");
        }
    }

    public static void merge(String branchName){
        throw new UnsupportedOperationException("FUCK");
    }
}