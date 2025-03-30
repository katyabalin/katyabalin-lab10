import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCounter {
    public final static int WordsMinNumber = 5;
     public static int FindStopWord;
     public static StringBuffer processFile(String path) throws EmptyFileException {
        Boolean isRead = false;
        String fl = path;
         Scanner scn = new Scanner(System.in);
        while(!isRead) {
            File f = new File(fl);
            if(!f.canRead()) {
                System.out.print("Can't read file " + fl);
                System.out.print("Select another file to process:");
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
            while((c = stream.read()) != -1) {
                sb.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(sb.length() == 0) {
                throw new EmptyFileException(fl + " was empty");
        }
        return sb;
    }
    public static int processText(StringBuffer sb, String StopWord) throws InvalidStopwordException, TooSmallText {

        if(sb == null || sb.length() == 0) {
               FindStopWord +=2; // no need to change StopWord
                throw new TooSmallText("Only found 0 words.");

        }
        Pattern regex = Pattern.compile("\\b[\\w[']]+\\b");
        Matcher regexMatcher = regex.matcher(sb.toString());
        int count = 0;
        int countThroughWord = 0;
        if(StopWord == null) { // no StopWord
            FindStopWord +=2; // no need to change StopWord
            while(regexMatcher.find()) {
                count++;
            }
            if(count < WordsMinNumber) {
                throw new TooSmallText("Only found " + count + " words.");
            }
            countThroughWord = count;
        } else {
            Boolean isFound = false;
            while(regexMatcher.find()) {
                count++;
                String group = regexMatcher.group();
                if(!isFound && StopWord.equals(regexMatcher.group())) {
                    isFound = true;
                    countThroughWord = count;
                }
            }
            if(count < WordsMinNumber) {
                throw new TooSmallText("Only found " + count + " words.");
            }
            if(!isFound) { // StopWord doesn't found
                FindStopWord +=1; // possibly, to change StopWord
                throw new InvalidStopwordException("Couldn't find stopword: " + StopWord);

            }
            else {
                FindStopWord +=2; // no need to change StopWord
            }
        }

        return  countThroughWord;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("main(): No arguments");
        } else {
            Boolean loop = true;
            Scanner scn = new Scanner(System.in);
            int opt = 0;
            while (loop) {
               System.out.print("Choose option: 1 - process the file, 2 - text itself");
               opt = scn.nextInt();
               if ((opt == 1) || (opt == 2)) {
                   loop = false;
               } else {
                   System.out.print("Invalid option");
               }
            }

            StringBuffer par1 = new StringBuffer(args[0]);
            if (opt == 1) {
                try {
                    par1 = processFile(args[0]);
                }
                catch (EmptyFileException e) {
                    System.out.println(e.toString());
                    par1 = null;
                }

            }

            String par2 = null;
            if (args.length > 1) {
                par2 = args[1];
            }
            FindStopWord = 0;
            int WordNumber = 0;
            while(FindStopWord < 2) { // if stopword was't found there is another chance
                try {
                    WordNumber = processText(par1, par2);
                }
                catch(TooSmallText e) {
                    System.out.println(e.toString());
                    return;
                }
                catch(InvalidStopwordException e) {
                    System.out.println(e.toString());
                    if(FindStopWord < 2) {
                        System.out.println(" Try another stopword:");
                        par2 = scn.next();
                    }
                }
            }
            System.out.print("Found " + WordNumber + " words.");
            scn.close();
        }
    }
}

