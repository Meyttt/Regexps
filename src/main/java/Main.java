public class Main {
    public static void main(String[] args) {
//        NotGreedyMatchFormatter matchResultFormatter = new NotGreedyMatchFormatter( "1test322",".t.");
        LazyMatchFormatter matchResultFormatter = new LazyMatchFormatter( "<a>test</a>swll<a>2</a>","<[Aa]>.*</[Aa]>");
//        NotGreedyMatchFormatter matchResultFormatter = new NotGreedyMatchFormatter( "1test322",".t.");
        matchResultFormatter.format();
        System.out.println(matchResultFormatter);
    }


}
