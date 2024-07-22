package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static gitlet.Repository.*;
import static gitlet.Utils.join;

public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        if(args.length==0){
            System.out.println("Please enter a command.");
        }
        else {
            String firstArg = args[0];
            boolean exec = true;
            if(!firstArg.equals("init")){
                if(!gitlet_dir.exists()){
                    System.out.println("Not in a initialized Gitlet directory.");
                    exec = false;
                }
            }
            if(exec) {
                switch (firstArg) {
                    case "init":
                        // TODO: handle the `init` command
                        init();
                        break;
                    case "add":
                        // TODO: handle the `add [filename]` command
                        if (args.length == 2) add(args[1]);
                        else System.out.println("Incorrect operands.");
                        break;
                    // TODO: FILL THE REST IN
                    case "commit":
                        if (args.length == 2) commit(args[1]);
                        else System.out.println("Incorrect operands.");
                        break;
                    case "rm":
                        if(args.length == 2) remove(args[1]);
                        else System.out.println("Incorrect operands");
                        break;
                    case "log":
                        log();
                        break;
                    case "global-log":
                        global_log();
                        break;
                    case "find":
                        if(args.length == 2) find(args[1]);
                        else System.out.println("Incorrect operands");
                        break;
                    case "status":
                        status();
                        break;
                    case "checkout":
                        if(args.length==3){
                            if(args[1].equals("--"))checkout_file(args[2]);
                            else checkout_commit(args[1], args[2]);
                        }
                        else if(args.length==2)checkout_branch(args[1]);
                        else System.out.println("Incorrect operands");
                        break;
                    case "branch":
                        if(args.length==2)createBranch(args[1]);
                        else System.out.println("Incorrect operands");
                        break;
                    case "rm-branch":
                        if(args.length==2)removeBranch(args[1]);
                        else System.out.println("Incorrect operands");
                        break;
                    case "reset":
                        if(args.length==2)reset(args[1]);
                        else System.out.println("Incorrect operands");
                        break;
                    case "merge":

                        break;
                    default:
                        System.out.println("No command with that name exists.");
                        break;
                }
            }
        }
    }
}
