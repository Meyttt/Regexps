import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class PosGreedyMatchFormatter {
    int rootStartPos;
    int rootEndPos;
    List<Integer> rootPoints = new ArrayList<>();

    String inputText;
    Pattern pattern;

    public PosGreedyMatchFormatter(String inputText, String pattern) {
        this.inputText = inputText;
        this.pattern = Pattern.compile(pattern);
        rootStartPos = 0;
        rootEndPos = inputText.length();
    }
    public PosGreedyMatchFormatter(String inputText, String pattern, int flags) {
        this.inputText = inputText;
        this.pattern = Pattern.compile(pattern, flags);
        rootStartPos = 0;
        rootEndPos = inputText.length();
    }

    public String format(){
        if(!pattern.matcher(inputText).find()) return "[]";
        while (rootStartPos < inputText.length() && pattern.matcher(inputText).region(rootStartPos,rootEndPos).find()) {
            findRootStartPos();
            findRootEndPos();
            if(validRoot() && rootStartPos < inputText.length()) {
                rootPoints.add(rootStartPos);
                rootPoints.add(rootEndPos);

            }
            rootStartPos = rootEndPos + 1;
            rootEndPos = inputText.length();
        }
        return formatResult();
    }

    private boolean validRoot() {
        int prevIndex = rootPoints.size() == 0 ? 0 : rootPoints.get(rootPoints.size()-1);
        int nextIndex = rootEndPos == inputText.length() ? rootEndPos : rootEndPos + 1;
        boolean valid = pattern.matcher(inputText).region(prevIndex, nextIndex).find();
        if(rootStartPos != prevIndex){
            valid &= !pattern.matcher(inputText).region(prevIndex, rootEndPos).matches();
        }
        if(rootEndPos != nextIndex){
            valid &= !pattern.matcher(inputText).region(rootStartPos,nextIndex).matches();
        }
        return valid;
    }

    private void findRootStartPos() {
        while (!startMatches() && rootStartPos < inputText.length()){
            rootStartPos++;
        }
    }

    private boolean startMatches(){
        return pattern.matcher(inputText).region(rootStartPos, rootEndPos).lookingAt();
    }

    private void findRootEndPos() {
        if(rootStartPos == inputText.length()){
            rootEndPos = rootStartPos;
            return;
        }
        rootEndPos = inputText.length();
        int rootEnd = rootEndPos;
        boolean nowMatches = false;
        while (rootStartPos < rootEndPos - 1){
            if(!fullMatch()){
                nowMatches = false;
                rootEndPos--;
                continue;
            }
            if(nowMatches){
                rootEndPos--;
                continue;
            }
            rootEnd = rootEndPos;
            nowMatches = true;
            rootEndPos--;
        }
        rootEndPos = rootEnd;

//        while (!fullMatch() && rootEndPos < inputText.length()){
//            rootEndPos ++;
//        }
//        if(rootEndPos == inputText.length()){
//            return;
//        }
//        while (fullMatch() && rootEndPos < inputText.length()){
//            rootEndPos ++;
//        }
//        if(!fullMatch()){
//            rootEndPos--;
//        }
//        while (!fullMatch() && rootEndPos <= inputText.length()){
//            rootEndPos++;
//        }
//        while (rootEndPos < inputText.length()){
//            rootEndPos++;
//        }
//        if(!fullMatch()){
//            rootEndPos--;
//        }
    }

    private boolean fullMatch() {
        return pattern.matcher(inputText).region(rootStartPos, rootEndPos).matches();
    }

    private String formatResult() {
        if (rootPoints.size() % 2 != 0){
            throw new RuntimeException();
        }
        StringBuilder sb = new StringBuilder();
        int previousRootEnd = 0;
        for(int i = 0; i< rootPoints.size(); i++){
            sb.append(inputText.substring(previousRootEnd, rootPoints.get(i)));
            sb.append("[");
            sb.append(inputText.substring(rootPoints.get(i), rootPoints.get(++i)));
            sb.append("]");
            previousRootEnd = rootPoints.get(i);
        }
        sb.append(inputText.substring(rootPoints.get(rootPoints.size()-1)));
        return sb.toString();
    }


}
