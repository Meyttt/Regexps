import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LazyMatchFormatter implements MatchFormatter{

    List<String> resultParts;
    String currentText;
    String currentPrefix;
    String currentRoot;
    Pattern pattern;

    public LazyMatchFormatter(String inputText, String pattern, int flags) {
        this.currentText = inputText;
        this.pattern = Pattern.compile(pattern, flags);
        this.resultParts = new ArrayList<>();
    }
    public LazyMatchFormatter(String inputText, String pattern) {
        this.currentText = inputText;
        this.pattern = Pattern.compile(pattern);
        this.resultParts = new ArrayList<>();
    }
    public String format(){
        if(currentText == null || currentText.isEmpty() || !pattern.matcher(currentText).find()){
            return "[]";
        }
        parse();
        return toString();
    }

    private void parse() {
        while (currentTextHasSymbols()){
            getCurrentPrefix();
            getCurrentRoot();
            pushPartsToList();
        }
    }

    private boolean currentTextHasSymbols() {
        return currentText.length() > 0;
    }
    private void getCurrentPrefix() {
        clearCurrentPrefix();
        while (!startMatches(currentText) && currentTextHasSymbols()){
            appendMainTextFirstCharToPrefix();
            removeCurrentTextFirstChar();
        }
    }

    private void clearCurrentPrefix() {
        currentPrefix = "";
    }

    private void appendMainTextFirstCharToPrefix() {
        this.currentPrefix += currentText.charAt(0);
    }

    private boolean startMatches(String currentText) {
        return pattern.matcher(currentText).lookingAt();
    }

    private void getCurrentRoot() {
        clearCurrentRoot();
        while (!startMatches(currentRoot) && currentTextHasSymbols()){
            appendMainTextFirstCharToRoot();
            removeCurrentTextFirstChar();
        }
    }

    private void clearCurrentRoot() {
        currentRoot = "";
    }


    private void appendMainTextFirstCharToRoot() {
        this.currentRoot += currentText.charAt(0);
    }

    private void removeCurrentTextFirstChar() {
        currentText = currentText.substring(1);
    }

    private void pushPartsToList() {
        resultParts.add(currentPrefix);
        resultParts.add(wrapRoot());
    }

    private String wrapRoot(){
        if (currentRoot.length() == 0) return "";
        return "[" + currentRoot + "]";
    }

    @Override
    public String toString() {
        return resultParts.stream().collect(Collectors.joining());
    }
}
