import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCounter {
    // minimum number of words required
    public final static int WordsMinNumber = 5;

    // used to track if stopword was found or not
    public static int FindStopWord;

    // checks if the program is running under junit 
    private static boolean isRunningJUnit() {
        return System.getProperty("java.class.path").contains("junit");
    }

    // reads file content into a stringbuffer
    // keeps prompting until a valid file is given (unless in junit)
    // throws emptyfileexception if file has no content
    public static StringBuffer processFile(String path) throws EmptyFileException {
        boolean isRead = false;
        String fl = path;
        Scanner scn = new Scanner(System.in);

        while (!isRead) {
            File f = new File(fl);
            if (!f.canRead()) {
                if (!isRunningJUnit()) {
                    System.out.print("can't read file " + fl);
                    System.out.print("select another file to process:");
                }
                fl = scn.next();
            } else {
                isRead = true;
            }
        }

        scn.close();
        StringBuffer sb = new StringBuffer();

        try {
            FileInputStream stream = new FileInputStream(fl);
            int c;
            while ((c = stream.read()) != -1) {
                sb.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (sb.length() == 0) {
            throw new EmptyFileException(fl + " was empty");
        }

        return sb;
    }

    // counts words in the text, optionally stopping at a stopword
    // throws toosmalltext if fewer than 5 words
    // throws invalidstopwordexception if stopword isn't found
    public static int processText(StringBuffer sb, String stopWord) throws InvalidStopwordException, TooSmallText {
        if (sb == null || sb.length() == 0) {
            FindStopWord += 2;
            throw new TooSmallText("only found 0 words.");
        }

        Pattern regex = Pattern.compile("[a-zA-Z0-9']+");
        Matcher regexMatcher = regex.matcher(sb.toString());
        int count = 0;
        int countThroughWord = 0;

        if (stopWord == null) {
            FindStopWord += 2;
            while (regexMatcher.find()) {
                count++;
            }
            if (count < WordsMinNumber) {
                throw new TooSmallText("only found " + count + " words.");
            }
            countThroughWord = count;
        } else {
            boolean isFound = false;
            while (regexMatcher.find()) {
                count++;
                String group = regexMatcher.group();
                if (!isFound && stopWord.equals(group)) {
                    isFound = true;
                    countThroughWord = count;
                }
            }
            if (count < WordsMinNumber) {
                throw new TooSmallText("only found " + count + " words.");
            }
            if (!isFound) {
                FindStopWord += 1;
                throw new InvalidStopwordException("couldn't find stopword: " + stopWord);
            } else {
                FindStopWord += 2;
            }
        }

        return countThroughWord;
    }

    // main method: handles user input and manages logic
    // asks user to choose between file or text input
    // handles exceptions and retry logic for stopword
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("main(): no arguments");
            return;
        }

        Scanner scn = new Scanner(System.in);
        int opt = 0;
        boolean loop = true;

        // ask for input type until valid option is entered
        while (loop) {
            if (!isRunningJUnit()) {
                System.out.print("choose option: 1 - process the file, 2 - text itself");
            }

            opt = scn.nextInt();
            if (opt == 1 || opt == 2) {
                loop = false;
            } else {
                if (!isRunningJUnit()) {
                    System.out.print("invalid option");
                }
            }
        }

        // grab text from either file or command line
        StringBuffer par1 = new StringBuffer(args[0]);
        if (opt == 1) {
            try {
                par1 = processFile(args[0]);
            } catch (EmptyFileException e) {
                System.out.println(e.toString());
                par1 = null;
            }
        }

        // check for optional stopword
        String par2 = null;
        if (args.length > 1) {
            par2 = args[1];
        }

        FindStopWord = 0;
        int WordNumber = 0;

        // allow retry once if stopword is missing
        while (FindStopWord < 2) {
            try {
                WordNumber = processText(par1, par2);
            } catch (TooSmallText e) {
                System.out.println(e.toString());
                scn.close();
                return;
            } catch (InvalidStopwordException e) {
                System.out.println(e.toString());
                if (FindStopWord < 2) {
                    if (!isRunningJUnit()) {
                        System.out.println(" try another stopword:");
                    }
                    par2 = scn.next();
                }
            }
        }

        // print final word count
        System.out.print("found " + WordNumber + " words.");
        scn.close();
    }
}
