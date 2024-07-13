package capers;

import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/** A repository for Capers 
 * @author TODO
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = CWD; // TODO Hint: look at the `join`
                                            //      function in Utils

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() throws IOException {
        // TODO
        File doggy = new File(CAPERS_FOLDER, "dogs");
        if(!doggy.exists()){
            if(!doggy.mkdir()){
                exitWithError("cannot make directory dogs");
            }
        }
        File sty = new File(CAPERS_FOLDER, "story");
        if(!sty.exists()){
            if(!sty.createNewFile()){
                exitWithError("cannot create file story");
            }
        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        File f = join(CAPERS_FOLDER, "story");
        String story = readContentsAsString(f);
        story += text;
        story += "\n";
        System.out.println(story);
        writeContents(f, story);
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) throws IOException {
        // TODO
        Dog a = new Dog(name, breed, age);
        File f = join(CAPERS_FOLDER, "dogs", name);
        if(!f.exists()){
            if(!f.createNewFile()){
                exitWithError("cannot create file "+name);
            }
        }
        writeObject(f, a);
        System.out.println(a.toString());
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) throws IOException {
        File f = join(CAPERS_FOLDER, "dogs", name);
        if(!f.exists()){
            exitWithError("cannot find file "+name);
        }
        Dog a = readObject(f, Dog.class);
        a.haveBirthday();
        writeObject(f, a);
    }
}
