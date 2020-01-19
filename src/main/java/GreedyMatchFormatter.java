import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GreedyMatchFormatter implements MatchFormatter{
    private Pattern pattern;


    private String prefix;
    private String root;
    private String suffix;

    public GreedyMatchFormatter(String regExp, String input, int flags) {
        this.pattern = Pattern.compile(regExp, flags);

        prefix = "";
        root = input;
        suffix = "";
    }

    public GreedyMatchFormatter(String regExp, String input) {
        this.pattern = Pattern.compile(regExp);

        prefix = "";
        root = input;
        suffix = "";
    }

    public String format(){
        if(root == null || root.isEmpty() || !pattern.matcher(root).find()){
            root = "";
        }else {
            getPrefix();
            getSuffix();
        }
        return this.toString();
    }

    private void getPrefix(){
        while (true){
            if(root.length() <=1)
                break;
            if(startMatches()) break;
            appendPrefix();
            deleteFirst();
        }
        return;
    }

    private boolean startMatches(){
        return pattern.matcher(root).lookingAt();
    }

    private void appendPrefix(){
        prefix += root.charAt(0);
    }

    private void deleteFirst(){
        root = root.substring(1);
    }


    private void getSuffix(){
        while (true){
            if(root.length() <= 1)
                break;
            if(fullMatches())
                break;
            appendSuffix();
            deleteLast();
        }
    }
    private boolean fullMatches(){
        return pattern.matcher(root).matches();
    }
    private void appendSuffix(){
        suffix = root.charAt(root.length()-1) + suffix;
    }
    private void deleteLast(){
        root = root.substring(0, root.length()-1);
    }
    @Override
    public String toString() {
        return prefix + "["+ root + "]" + suffix;
    }


}
