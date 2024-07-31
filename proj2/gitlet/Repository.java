package gitlet;


import java.io.File;
import java.util.*;
import java.util.Map.*;

import static gitlet.Stage.*;
import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
    gitlet

     --repository
         blobs

     --object
         commits

     --branches
         heads

     --stage
         addStage
         addCache
         removeStage
         removeCache

     cur_branch

     head
     */

    /** The current working directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    static final File gitlet_dir = join(CWD, ".gitlet");//directory

    static final File blobs = join(gitlet_dir, "blobs");//directory

    static final File object = join(gitlet_dir, "object");//directory

    static final File branches = join(gitlet_dir, "branches");//directory

    static final File stage = join(gitlet_dir, "stage");//directory

    static final File Cache = join(stage, "add");//directory

    static final File addStage = join(stage, "addStage");//file

    static final File removeStage = join(stage, "removeStage");//file

    static final File cur_branch = join(gitlet_dir, "cur_branch");//file

    static final File head = join(gitlet_dir, "head");

    /* TODO: fill in the rest of this class. */

    private static void initCommit(){

        Commit initial = new Commit("initial commit", "");
        initial.setID();
        initial.save();
        writeObject(head, initial);
        writeContents(cur_branch, "master");

        File master = join(branches, "master");
        createFile(master);
        writeObject(master, initial);

    }

    private static void initStage(){
        Stage add = new Stage();
        Stage remove = new Stage();
        writeObject(addStage, add);
        writeObject(removeStage, remove);
    }

    public static void init(){

        if(gitlet_dir.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        mkdir(gitlet_dir);
        mkdir(blobs);
        mkdir(object);
        mkdir(branches);
        createFile(cur_branch);
        createFile(head);
        mkdir(stage);
        createFile(addStage);
        createFile(removeStage);
        mkdir(Cache);

        initCommit();

        initStage();

    }

    private static Commit getHead(){
        return readObject(head, Commit.class);
    }

    private static boolean checkIfHaveNew(File f){
        String blob = sha1(readContents(f));
        Commit head = getHead();
        return !blob.equals(head.file_blob_map.get(f.getName()));
    }


    public static void add(String fileName){
        Stage add = getAdd();
        Stage remove = getRemove();
        File f = join(CWD, fileName);
        if(! f.exists()){
            System.out.println("File does not exist.");
            System.exit(0);
        }
        if(checkIfHaveNew(f)){
            add.stage_for_add(f);
        }
        else{
            add.remove_from_add(f);
        }
        remove.remove_from_remove(f);
        add.saveAdd();
        remove.saveRemove();
    }

    private static void setHead(Commit c){
        writeObject(head, c);
    }

    private static void setCurrentBranch(String m){
        writeContents(cur_branch, m);
    }

    private static String getCurrentBranch(){
        return readContentsAsString(cur_branch);
    }

    private static void extendBranch(Commit c){
        String cur = getCurrentBranch();
        File cur_b = join(branches, cur);
        writeObject(cur_b, c);
    }

    private static void setCommit(Commit newCommit){

        HashMap<String, String> current = getHead().file_blob_map;
        HashMap<String, String> newMap = new HashMap<>(current);
        Stage add = getAdd();
        Stage remove = getRemove();
        Set<Entry<String, String>> addition = add.fileName_blob_map.entrySet();
        for(Entry<String, String> entry : addition){
            newMap.put(entry.getKey(), entry.getValue());
        }
        Set<String> removal = remove.fileName_blob_map.keySet();
        for(String entry : removal){
            newMap.remove(entry);
        }

        keepInTrack();
        clearStage();

        newCommit.file_blob_map = newMap;
        newCommit.setID();
    }

    private static boolean ifAnythingStaged(){
        Stage add = getAdd();
        Stage remove = getRemove();
        return add.ifAnythingStaged() || remove.ifAnythingStaged();
    }

    public static void commit(String message){
        if(!ifAnythingStaged()){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if(message.isBlank()){
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        Commit head = getHead();
        Commit newCommit = new Commit(message, head.getId());
        setCommit(newCommit);
        newCommit.save();
        setHead(newCommit);
        extendBranch(newCommit);
    }

    private static boolean checkIfCurrentlyTracked(String fileName){
        Commit head = getHead();
        return head.file_blob_map.containsKey(fileName);
    }

    public static void remove(String fileName){
        Stage add = getAdd();
        Stage remove = getRemove();
        File f = join(CWD, fileName);
        if(add.isStaged(fileName) || checkIfCurrentlyTracked(fileName)) {
            add.remove_from_add(f);
            if (checkIfCurrentlyTracked(fileName)) {
                remove.stage_for_remove(f);
                deleteFile(f);
            }
        }
        else{
            System.out.println("No reason to remove the file.");
        }
        add.saveAdd();
        remove.saveRemove();
    }

    public static void printCommit(Commit c){
        System.out.println("===");
        System.out.println("commit " + c.getId());
        System.out.println("Date: "+c.getDate());
        System.out.println(c.getMessage());
        System.out.print("\n");
    }

    public static void printMerge(Commit c){
        System.out.println("===");
        System.out.print("commit " + c.getId() + "\n");
        System.out.println("Merge: " + c.getParentId().substring(0, 7) + " " + c.getSecondParentId().substring(0, 7));
        System.out.println("Date: "+c.getDate());
        System.out.println(c.getMessage());
        System.out.print("\n");
    }

    public static void log(){
        Commit head = getHead();
        while(true){
            if(head.isMerge()){
                printMerge(head);
            }
            else{
                printCommit(head);
                if(head.isInitial())break;
            }
            head = head.getParent();
        }
    }

    public static void global_log(){
        Commit ret = null;
        File[] files = object.listFiles();
        if(files != null){
            for(File file : files){
                ret = readObject(file, Commit.class);
                if(ret.isMerge()){
                    printMerge(ret);
                }
                else{
                    printCommit(ret);
                }
            }
        }
    }

    public static void find(String message){
        Commit ret = null;
        File[] files = object.listFiles();
        boolean is = true;
        if(files != null){
            for(File file : files){
                ret = readObject(file, Commit.class);
                if(message.equals(ret.getMessage())){
                    System.out.println(ret.getId());
                    is = false;
                }
            }
        }
        if(is){
            System.out.println("Found no commit with that message.");
        }
    }

    private static void printBranches(){
        System.out.println("=== Branches ===");
        List<String> b = plainFilenamesIn(branches);
        assert b != null;
        b.sort(String::compareTo);
        String cur = getCurrentBranch();
        for(String a : b){
            if(a.equals(cur)){
                System.out.print('*');
            }
            System.out.println(a);
        }
        System.out.println();
    }

    private static void printStaged(){
        System.out.println("=== Staged Files ===");
        Stage add = getAdd();
        List<String> a = new ArrayList<>(add.fileName_blob_map.keySet());
        a.sort(String::compareTo);
        for(String name : a){
            System.out.println(name);
        }
        System.out.println();
    }

    private static void printRemoved(){
        System.out.println("=== Removed Files ===");
        Stage remove = getRemove();
        List<String> a = new ArrayList<>(remove.fileName_blob_map.keySet());
        a.sort(String::compareTo);
        for(String name : a){
            System.out.println(name);
        }
        System.out.println();
    }

    private static HashMap<String, String> getCWDCondition(){
        List<String> cur = plainFilenamesIn(CWD);
        HashMap<String, String> ret = new HashMap<>();
        if(cur != null){
            for(String fileName : cur){
                String sha1 = sha1(readContents(join(CWD, fileName)));
                ret.put(fileName, sha1);
            }
        }
        return ret;
    }

    private static void printModified(){
        System.out.println("=== Modifications Not Staged For Commit ===");

        List<String> out = new ArrayList<>();
        HashMap<String, String> cwd = getCWDCondition();
        Stage add = getAdd();
        Stage remove = getRemove();
        Commit head = getHead();

        for(String key : cwd.keySet()){
            if(!cwd.get(key).equals(add.fileName_blob_map.get(key)) && head.file_blob_map.containsKey(key) && !cwd.get(key).equals(head.file_blob_map.get(key))){
                out.add(key+" (modified)");
            }
        }

        for(String key : add.fileName_blob_map.keySet()){
            if(!cwd.containsKey(key)){
                out.add(key+" (deleted)");
            }
        }

        for(String key : head.file_blob_map.keySet()){
            if(!cwd.containsKey(key) && !remove.fileName_blob_map.containsKey(key)){
                out.add(key + " (deleted)");
            }
        }

        out.sort(String::compareTo);

        for(String name : out){
            System.out.println(name);
        }
        System.out.println();
    }

    private static void printUntracked(){
        System.out.println("=== Untracked Files ===");
        HashMap<String, String> cwd = getCWDCondition();
        Stage add = getAdd();
        Commit head = getHead();
        Set<String> a = cwd.keySet();
        List<String> out = new ArrayList<>();
        for(String name : a){
            if(!add.fileName_blob_map.containsKey(name) && !head.file_blob_map.containsKey(name)){
                out.add(name);
            }
        }
        out.sort(String::compareTo);
        for(String b : out){
            System.out.println(b);
        }
        System.out.println();
    }

    public static void status(){
        printBranches();
        printStaged();
        printRemoved();
        printModified();
        printUntracked();
    }

    private static Commit getCommit(String id){
        File c = join(object, id);
        if(!c.exists()){
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        return readObject(c, Commit.class);
    }

    public static void checkout_file(String file_name){
        Commit head = getHead();
        if(!head.file_blob_map.containsKey(file_name)){
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File old_version = join(blobs, head.file_blob_map.get(file_name));
        File now = join(CWD, file_name);
        Stage add = getAdd();
        add.remove_from_add(now);
        createFile(now);
        writeContents(now, readContents(old_version));
    }

    public static void checkout_commit(String commit_id, String file_name){
        Commit head = getCommit(commit_id);
        if(!head.file_blob_map.containsKey(file_name)){
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File old_version = join(blobs, head.file_blob_map.get(file_name));
        File now = join(CWD, file_name);
        Stage add = getAdd();
        add.remove_from_add(now);
        createFile(now);
        writeContents(now, readContents(old_version));
    }

    private static void clearCWD(){
        List<String> a = plainFilenamesIn(CWD);
        if(a != null) {
            for (String name : a) {
                File f = join(CWD, name);
                deleteFile(f);
            }
        }
    }

    private static Commit getBranch(String BranchName){
        File b = join(branches, BranchName);
        if(! b.exists()){
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        return readObject(b, Commit.class);
    }

    private static void checkUntracked(){
        HashMap<String, String> cwd = getCWDCondition();
        Stage add = getAdd();
        Commit head = getHead();
        Set<String> a = cwd.keySet();
        for(String name : a){
            if(!add.fileName_blob_map.containsKey(name) && !head.file_blob_map.containsKey(name)){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    public static void checkout_branch(String branchName){
        if(branchName.equals(getCurrentBranch())){
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        checkUntracked();
        Commit head = getBranch(branchName);
        setHead(head);
        setCurrentBranch(branchName);
        clearCWD();
        Set<String> names = head.file_blob_map.keySet();
        for(String name : names){
            File f = join(CWD, name);
            File g = join(blobs, head.file_blob_map.get(name));
            createFile(f);
            writeContents(f, readContents(g));
        }
    }

    public static void createBranch(String branchName){
        List<String> a = plainFilenamesIn(branches);
        assert a != null;
        if(a.contains(branchName)){
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        File new_branch = join(branches, branchName);
        createFile(new_branch);
        writeObject(new_branch, getHead());
    }

    public static void removeBranch(String branchName){

        List<String> a = plainFilenamesIn(branches);
        assert a != null;
        if(!a.contains(branchName)){
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        if(branchName.equals(getCurrentBranch())){
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }

        File b = join(branches, branchName);
        deleteFile(b);
    }

    public static void reset(String id){
        checkUntracked();
        Commit head = getCommit(id);
        setHead(head);
        clearCWD();
        Set<String> names = head.file_blob_map.keySet();
        for(String name : names){
            File f = join(CWD, name);
            File g = join(blobs, head.file_blob_map.get(name));
            createFile(f);
            writeContents(f, readContents(g));
        }
    }

    private static Commit findSplitPoint(Commit a, Commit b){
        int result = a.date.compareTo(b.date);
        while(result != 0){
            if(result > 0){
                a = a.getParent();
            }
            else{
                b = b.getParent();
            }
            result = a.date.compareTo(b.date);
        }
        return a;
    }

    private static void checkUncommitted(){
        Stage add = getAdd();
        Stage remove = getRemove();
        if(!add.fileName_blob_map.isEmpty() || !remove.fileName_blob_map.isEmpty()){
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
    }

    private static void checkAncestor(Commit a, Commit b, String branchName){
        if(a.date.compareTo(b.date) < 0){
            while(!a.equals(b) && a.date.compareTo(b.date) < 0){
                b = b.getParent();
            }
            if(a.equals(b)){
                System.out.println("Given branch is an ancestor of the current branch.");
                System.exit(0);
            }
        }
        else if(a.date.compareTo(b.date) > 0){
            while(!a.equals(b) && a.date.compareTo(b.date) > 0){
                a = a.getParent();
            }
            if(a.equals(b)){
                System.out.println("Current branch fast-forwarded.");
                checkout_branch(branchName);
                System.exit(0);
            }
        }
        else{
            System.out.println("Current branch fast-forwarded.");
            checkout_branch(branchName);
            System.exit(0);
        }
    }

    private static HashMap<String, String> getAddedFiles(Commit head, Commit branch, Commit splitPoint){
        HashMap<String, String > ret = new HashMap<>();
        for(String key : branch.file_blob_map.keySet()){
            String h = head.file_blob_map.get(key);
            String b = branch.file_blob_map.get(key);
            String s = splitPoint.file_blob_map.get(key);
            if(!b.equals(s) && ((h == null && s == null) || (h != null && h.equals(s)))){
                ret.put(key, b);
            }
        }
        return ret;
    }

    private static List<String> getRemovedFiles(Commit head, Commit branch, Commit splitPoint){
        List<String> ret = new ArrayList<>();
        for(String key : head.file_blob_map.keySet()){
            String h = head.file_blob_map.get(key);
            String b = branch.file_blob_map.get(key);
            String s = splitPoint.file_blob_map.get(key);
            if(b == null && h != null && h.equals(s)){
                ret.add(key);
            }
        }
        return ret;
    }

    private static HashMap<String, String > getConflictFiles(Commit head, Commit branch, Commit splitPoint){
        HashMap<String, String > conflict = new HashMap<>();

        for(String fileName : head.file_blob_map.keySet()){
            String b = branch.file_blob_map.get(fileName);
            String s = splitPoint.file_blob_map.get(fileName);
            String h = head.file_blob_map.get(fileName);
            if(!h.equals(s) && !h.equals(b) && ((b != null && !b.equals(s)) || (b == null && s != null))){
                String x = readContentsAsString(join(blobs, h));
                String y = "\n";
                if(b != null)y = readContentsAsString(join(blobs, b));
                String newContent = "<<<<<<< HEAD\n" + x + "=======\n" + y + ">>>>>>>\n";
                conflict.put(fileName, newContent);
                if(!conflict.containsKey(fileName)){
                    System.out.println("Encountered a merge conflict.");
                }
            }
        }

        for(String fileName : branch.file_blob_map.keySet()){
            String b = branch.file_blob_map.get(fileName);
            String s = splitPoint.file_blob_map.get(fileName);
            String h = head.file_blob_map.get(fileName);
            if(!b.equals(s) && !b.equals(h) && ((h != null && !h.equals(s)) || (h == null && s != null))){
                String x = "\n";
                if(h != null)x = readContentsAsString(join(blobs, h));
                String y = readContentsAsString(join(blobs, b));
                String newContent = "<<<<<<< HEAD\n" + x + "=======\n" + y + ">>>>>>>\n";
                conflict.put(fileName, newContent);
                if(!conflict.containsKey(fileName)){
                    System.out.println("Encountered a merge conflict.");
                }
            }
        }
        return conflict;
    }

    private static void setMerge(Commit head, Commit branch, Commit splitPoint, Commit merge){
        List<String> removed = getRemovedFiles(head, branch, splitPoint);
        HashMap<String, String> added = getAddedFiles(head, branch, splitPoint);
        HashMap<String, String> conflict = getConflictFiles(head, branch, splitPoint);

        for(Entry<String, String> item : added.entrySet()){
            merge.file_blob_map.put(item.getKey(), item.getValue());
            File new_add = join(CWD, item.getKey());
            createFile(new_add);
            writeContents(new_add, readContents(join(blobs, item.getValue())));
        }

        for(String key : removed){
            merge.file_blob_map.remove(key);
            File r = join(CWD, key);
            deleteFile(r);
        }

        for(Entry<String, String> item : conflict.entrySet()){
            File con = join(CWD, item.getKey());
            createFile(con);
            String blob = sha1(item.getValue());
            writeContents(con, item.getValue());
            merge.file_blob_map.put(item.getKey(), blob);
            File new_blob = join(blobs, blob);
            createFile(new_blob);
            writeContents(new_blob, item.getValue());
        }
    }

    public static void merge(String branchName){

        Commit branch = getBranch(branchName);
        Commit head = getHead();

        List<String> a = plainFilenamesIn(branches);
        assert a != null;
        if(!a.contains(branchName)){
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if(branchName.equals(getCurrentBranch())){
            System.out.println("Cannot merge a branch with itself");
            System.exit(0);
        }
        checkUntracked();
        checkUncommitted();
        checkAncestor(branch, head, branchName);

        Commit splitPoint = findSplitPoint(branch, head);

        Commit merge = new Commit("Merged "+ branchName + " into " + getCurrentBranch(), head.getId(), branch.getId());
        merge.file_blob_map = new HashMap<>(head.file_blob_map);

        setMerge(head, branch, splitPoint, merge);

        merge.setID();
        merge.save();

        setHead(merge);
        extendBranch(merge);
    }
}