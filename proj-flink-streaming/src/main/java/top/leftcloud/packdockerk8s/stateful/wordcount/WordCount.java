package top.leftcloud.packdockerk8s.stateful.wordcount;


public class WordCount {

    private String word;

    private Integer count;

    public WordCount(String word, Integer count) {
        this.word = word;
        this.count = count;
    }

    public WordCount() {
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "WordCount{" +
                "word='" + word + '\'' +
                ", count=" + count +
                '}';
    }
}
