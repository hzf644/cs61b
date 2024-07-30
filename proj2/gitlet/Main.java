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
            if(!firstArg.equals("init")){
                if(!gitlet_dir.exists()){
                    System.out.println("Not in a initialized Gitlet directory.");
                    System.exit(0);
                }
            }
            switch (firstArg) {
                case "init":
                    // TODO: handle the `init` command
                    init();
                    break;
                case "add":
                    // TODO: handle the `add [filename]` command
                    if(validateOperand(args, 2))add(args[1]);
                    break;
                // TODO: FILL THE REST IN
                case "commit":
                    if(validateOperand(args, 2))commit(args[1]);
                    break;
                case "rm":
                    if(validateOperand(args, 2))remove(args[1]);
                    break;
                case "log":
                    if(validateOperand(args, 1))log();
                    break;
                case "global-log":
                    if(validateOperand(args, 1))global_log();
                    break;
                case "find":
                    if(validateOperand(args, 2))find(args[1]);
                    break;
                case "status":
                    if(validateOperand(args, 1))status();
                    break;
                case "checkout":
                    switch (args.length) {
                        case 3:
                            if (!args[1].equals("--")) {
                                System.out.println("Incorrect operands.");
                                System.exit(0);
                            }
                            /* * checkout -- [file name] */
                            checkout_file(args[2]);
                            break;
                        case 4:
                            if (!args[2].equals("--")) {
                                System.out.println("Incorrect operands.");
                                System.exit(0);
                            }
                            /* * checkout [commit id] -- [file name] */
                            checkout_commit(args[1], args[3]);
                            break;
                        case 2:
                            /* * checkout [branch name] */
                            checkout_branch(args[1]);
                            break;
                        default:
                            System.out.println("Incorrect operands.");
                            System.exit(0);
                    }
                    break;
                case "branch":
                    if(validateOperand(args, 2))createBranch(args[1]);
                    break;
                case "rm-branch":
                    if(validateOperand(args, 2))removeBranch(args[1]);
                    break;
                case "reset":
                    if(validateOperand(args, 2))reset(args[1]);
                    break;
                case "merge":
                    if(validateOperand(args, 2))merge(args[1]);
                    break;
                default:
                    System.out.println("No command with that name exists.");
                    break;
            }

        }
    }

    private static boolean validateOperand(String[] args, int num){
        if(args.length == num){
            return true;
        }
        else{
            System.out.println("Incorrect operands.");
            System.exit(0);
            return false;
        }
    }
}
