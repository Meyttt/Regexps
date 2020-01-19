import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PosGreedyMatchFormatter {
    int rootStartPos;
    int rootEndPos;

    String inputText;
    Pattern pattern;

    public PosGreedyMatchFormatter(String inputText, String pattern) {
        this.inputText = inputText;
        this.pattern = Pattern.compile(pattern);
        rootStartPos = 0;
        rootEndPos = inputText.length();
    }

    public String format(){
        findRootStartPos();
        findRootEndPos();
        return inputText.substring(0, rootStartPos)+"["+inputText.substring(rootStartPos,rootEndPos)+"]"+inputText.substring(rootEndPos);
    }


    private void findRootStartPos() {
        while (!startMatches()){
            rootStartPos++;
        }
    }

    private boolean startMatches(){
        return pattern.matcher(inputText).region(rootStartPos, rootEndPos).lookingAt();
    }

    private void findRootEndPos() {
        while (!fullMatch()){
            rootEndPos--;
        }
    }

    private boolean fullMatch() {
        return pattern.matcher(inputText).region(rootStartPos, rootEndPos).matches();
    }


}
