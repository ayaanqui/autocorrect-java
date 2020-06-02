import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class Autocorrect {
    private final char[] delimeters = new char[] { ' ', ',', '.', '(', ')', '?', '"', '\'', '“', '”', '’', '-', '!',
            ';' };
    private char[] alphabets = new char[26];

    private String text; // Holds the original text of the user
    private ArrayList<String> parsedText;
    private HashMap<String, String> wordsTable;

    public Autocorrect() throws IOException {
        wordsTable = new HashMap<>();
        // Load words into wordMap
        loadWords(new String[] { "words_alpha.txt", "words.txt", "words_english.txt" });
        // Initialize alphabets
        IntStream.range(0, 26).forEach(i -> alphabets[i] = (char) ('a' + i));
    }

    public void suggest(String text) throws IOException {
        this.text = text;
        parse();
        search();
    }

    private void loadWords(String[] wordsArray) throws IOException {
        for (String wordFileName : wordsArray) {
            Scanner file = new Scanner(new File(wordFileName));
            while (file.hasNext()) {
                String word = file.next().toLowerCase();
                wordsTable.put(word, word);
            }
            file.close();
        }
        System.out.println("*Successfully loaded " + wordsTable.size() + " files.\n\n");
    }

    /**
     * Input: "Hello, this is Ayaan" <br/>
     * Output: ["Hello", "this", "is", "Ayaan"]
     */
    private void parse() {
        // Reset ArrayList
        parsedText = new ArrayList<>();

        int start = 0;
        for (int i = 0; i < text.length(); i++) {
            for (char delim : delimeters) {
                if (text.charAt(i) == delim) {
                    String sub = text.substring(start, i);
                    if (sub.length() > 0)
                        parsedText.add(sub);
                    start = i + 1;
                    break;
                }
            }
        }

        String remainder = text.substring(start);
        if (!remainder.equals(""))
            parsedText.add(remainder);
    }

    private void search() {
        ArrayList<String> invalidWords = new ArrayList<>();
        parsedText.forEach(word -> {
            if (wordsTable.get(word.toLowerCase()) == null)
                invalidWords.add(word);
        });

        invalidWords.forEach(invalidWord -> {
            ArrayList<String> possibleWordCombos = wordCombiantions(invalidWord.toLowerCase());

            if (!possibleWordCombos.isEmpty()) {
                System.out.print("In place of " + invalidWord + " did you mean ");
                if (possibleWordCombos.size() == 1) {
                    System.out.println(possibleWordCombos.get(0) + "?\n");
                } else {
                    System.out.println(possibleWordCombos + "\n");
                }
            }
        });
    }

    private ArrayList<String> wordCombiantions(String word) {
        ArrayList<String> possibleWords = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            String start = word.substring(0, i);
            String end = word.substring(i + 1);

            for (char alphabet : alphabets) {
                String combination = start + alphabet + end;
                if (wordsTable.get(combination) != null)
                    possibleWords.add(combination);
            }
        }
        return possibleWords;
    }

    public static void main(String[] args) throws IOException {
        Autocorrect ob = new Autocorrect();

        String input = "";
        Scanner keyboard = new Scanner(System.in);

        while (true) {
            System.out.print("Enter phrase (type -1 to quit): ");
            input = keyboard.nextLine();

            if (input.equals("-1"))
                break;
            else {
                ob.suggest(input);
                System.out.println("\n");
            }
        }
        keyboard.close();
    }
}