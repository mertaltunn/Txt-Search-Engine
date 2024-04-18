public class Document implements Comparable<Document> {

    private String wordName;
    private String documentName;
    private Integer wordCount;
    public Document(String wordName, String documentName) {
        this.wordName = wordName;
        this.documentName = documentName;
        this.wordCount = 1;
    }

    public String getWordName() {
        return wordName;
    }

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }


    @Override
    public int compareTo(Document other) {
        return other.wordCount.compareTo(wordCount);
    }
}
