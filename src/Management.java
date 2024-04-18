import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Management {
    HashedDictionary<String,Document> dataBase = new HashedDictionary();
    
    public void initialize(String whichHashFunction, String whichCollisionHandling) throws Exception {

        final String DELIMITERS = "[-+=" +
                " " +        //space
                "\r\n " +    //carriage return line fit
                "1234567890" + //numbers
                "â€™'\"" +       // apostrophe
                "(){}<>\\[\\]" + // brackets
                ":" +        // colon
                "," +        // comma
                "â€’â€“â€”â€•" +     // dashes
                "â€¦" +        // ellipsis
                "!" +        // exclamation mark
                "." +        // full stop/period
                "Â«Â»" +       // guillemets
                "-â€�" +       // hyphen
                "?" +        // question mark
                "â€˜â€™â€œâ€�" +     // quotation marks
                ";" +        // semicolon
                "/" +        // slash/stroke
                "â�„" +        // solidus
                "â� " +        // space?
                "Â·" +        // interpunct
                "&" +        // ampersand
                "@" +        // at sign
                "*" +        // asterisk
                "\\" +       // backslash
                "â€¢" +        // bullet
                "^" +        // caret
                "Â¤Â¢$â‚¬Â£Â¥â‚©â‚ª" + // currency
                "â€ â€¡" +       // dagger
                "Â°" +        // degree
                "Â¡" +        // inverted exclamation point
                "Â¿" +        // inverted question mark
                "Â¬" +        // negation
                "#" +        // number sign (hashtag)
                "â„–" +        // numero sign ()
                "%â€°â€±" +      // percent and related signs
                "Â¶" +        // pilcrow
                "â€²" +        // prime
                "Â§" +        // section sign
                "~" +        // tilde/swung dash
                "Â¨" +        // umlaut/diaeresis
                "_" +        // underscore/understrike
                "|Â¦" +       // vertical/pipe/broken bar
                "â�‚" +        // asterism
                "â˜�" +        // index/fist
                "âˆ´" +        // therefore sign
                "â€½" +        // interrobang
                "â€»" +          // reference mark
                "]";

        try {
            String stopWordsFilePath = "C:\\Users\\Fırat Cem Arslan\\Desktop\\Projects\\Txt-Search-Engine\\files\\stop_words_en.txt";
            List<String> stopWords = readFromFile(stopWordsFilePath);

            String filePath = "C:\\Users\\Fırat Cem Arslan\\Desktop\\Projects\\Txt-Search-Engine\\files\\sport\\";

            for (int i = 1; i <= 100; i++) {
                String finalFilePath = filePath.concat(String.format("%03d", i).concat(".txt"));
                File file = new File(finalFilePath);

                BufferedReader br = new BufferedReader(new FileReader(file));

                // Declaring a string variable
                String line;
                // Condition holds true till
                // there is character in a string
                while ((line = br.readLine()) != null) {

                    String[] splittedLine = line.split(DELIMITERS);
                    List<String> finalWordsList = new ArrayList<>();

                    for (String word : splittedLine) {
                        if (!stopWords.contains(word.toLowerCase(Locale.ENGLISH))) {
                            finalWordsList.add(word.toLowerCase(Locale.ENGLISH));
                        }
                    }

                    for (String indexWord : finalWordsList) {
                        String fileName = String.format("%03d", i);
                        Document document = new Document(indexWord, fileName);

                        try {
                            dataBase.add(indexWord, document, whichHashFunction, whichCollisionHandling);
                        } catch (Exception e) {
                            throw new Exception();
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("Something went wrong!");
        }
    }

    public void run() throws Exception {
    	
    	// only using this commanded part when calculating Index time for report
    	
    	/* long sum = 0;
           for(int i=0;i<10;i++) {
            long curr = System.currentTimeMillis();
            initialize("SSF","DH");
            long difference = System.currentTimeMillis() - curr;
            sum+=difference;
            dataBase = new HashedDictionary<>();
        }
        System.out.println("Index time for report " + sum/10 + " ms");
        
        
       */
        //Initilize
        long curr = System.currentTimeMillis();
        initialize("PAF","DH"); //choosing hashfunction and collision handling to initialize as required
        long difference = System.currentTimeMillis() - curr;
        System.out.println("Initialize time : " + difference + " ms");
        
        String searchWordsFilePath = "C:\\Users\\Fırat Cem Arslan\\Desktop\\Projects\\Txt-Search-Engine\\files\\search.txt";
        List<String> searchWordsList = readFromFile(searchWordsFilePath);

        long minSearchTime = 0;
        long maxSearchTime = 0;
        long avgSearchTime = 0;
        long sumSearchTime = 0;

        long beginSearchTime = System.nanoTime();
        dataBase.search(searchWordsList.get(0));
        long endSearchTime = System.nanoTime() - beginSearchTime;

        minSearchTime=endSearchTime;
        maxSearchTime=endSearchTime;

        for (int i=1;i<searchWordsList.size();i++) {
            beginSearchTime = System.nanoTime();
            dataBase.search(searchWordsList.get(i));
            endSearchTime = System.nanoTime() - beginSearchTime;

            if (endSearchTime < minSearchTime) {
                minSearchTime = endSearchTime;
            }
            if (endSearchTime > maxSearchTime) {
                maxSearchTime = endSearchTime;
            }
            sumSearchTime+=endSearchTime;
        }
        avgSearchTime = sumSearchTime/1000;
        
        //Collision count
        System.out.println("Collision count : " + dataBase.COLLISION_COUNT);

        System.out.println("Min search time : " + minSearchTime + " ns");
        System.out.println("Max search time : " + maxSearchTime + " ns");
        System.out.println("Avg search time : " + avgSearchTime + " ns");

        System.out.println("If you want to exit, please enter 1");
        boolean isExit = false;
        while(!isExit) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.print("Please enter your search words with putting blank/space between them -> ");
                String searchWords = sc.nextLine();
                if (searchWords == null || searchWords.equals("")) {
                    System.out.println("Your query is empty!");
                } else {
                    calculateRelevancyAndDisplay(dataBase.search(searchWords.trim().toLowerCase()));
                }
                if(searchWords.equals("1")) {
                    isExit = true;
                }
            } catch (Exception e) {
                throw new Exception("Something went wrong!");
            }
        }
    }
    public int calculateRelevancyAndDisplay(List<List<Document>> wordList){
        if(wordList.size()==0) {
            System.out.println("The word is not in the table");
            return -1;
        }
        boolean isCommonFound = false;
        List<Document> resultWordList = new ArrayList<>();
        List<Document> firstDocumentList = wordList.get(0);
        for (Document firstDocument : firstDocumentList) {
            for (int i=1;i<wordList.size();i++) {
                for (Document comparingDocument : wordList.get(i)) {
                    if (firstDocument.getDocumentName().equals(comparingDocument.getDocumentName())) {
                        resultWordList.add(firstDocument);
                        resultWordList.add(comparingDocument);
                    }
                }
            }
        }
        for (Document secondDocument : wordList.get(1)) {
            for (Document comparingDocument : wordList.get(2)) {
                if (secondDocument.getDocumentName().equals(comparingDocument.getDocumentName())) {
                    if (resultWordList.contains(secondDocument)) {
                        // Situation of having 3 words in same file
                        System.out.println("Most Relevant Document is " + comparingDocument.getDocumentName() + ".txt");
                        isCommonFound = true;
                        return 1;
                    }
                }
            }
        }
        if(isCommonFound==false) {
            //Sorting most repeated word
            Collections.sort(resultWordList);
            System.out.println("Most Relevant Document is -> " + resultWordList.get(0).getDocumentName() + ".txt");
            return 1;
        }
        return -1;
    }
    public List<String> readFromFile(String filePath) throws IOException {
        List<String> resultList = new ArrayList();
        try{
            File file = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            // Declaring a string variable
            String line;
            // Condition holds true till
            // there is character in a string
            while ((line = br.readLine()) != null) {
                if (line != "") {
                    resultList.add(line);
                }
            }
        } catch (IOException e) {
            throw new IOException("An error occurred while reading file!");
        }
        return resultList;
    }

    public void display(HashedDictionary<String, Document> dataBase) {
        Iterator<String> keyIterator = dataBase.getKeyIterator();
        Iterator<List<Document>> valueIterator = dataBase.getValueIterator();
        while (keyIterator.hasNext()) {
            System.out.print("Key: " + keyIterator.next());
            for (Document document : valueIterator.next()) {
                System.out.print(" | Value Document Name : " + document.getDocumentName());
                System.out.println(" | Value Occurences : " + document.getWordCount());
            }
        }
    }
}
