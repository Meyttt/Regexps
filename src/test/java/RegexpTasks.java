import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpTasks {

    @Test(groups = "smoke")
    public void simplePositiveTest(){
        String result = getResult("abctuwey", ".b");
        Assert.assertEquals(result, "[ab]ctuwey");
    }

    @Test (groups = "smoke")
    public void simpleSuffixPositiveTest(){
        String result = getResult("219abctuwey", ".b");
        Assert.assertEquals(result, "219[ab]ctuwey");
    }

    @Test (groups = "smoke")
    public void simpleNegativeTest(){
        String result = getResult("abctuwey", ".bte");
        Assert.assertEquals(result, "[]");
    }

    @Test (groups = "smoke")
    public void pointTest(){
        String result = getResult("test", ".est");
        Assert.assertEquals(result, "[test]");
        String result2 = getResult("test", "\\.est");
        Assert.assertEquals(result2, "[]");
    }

    @Test
    public void ignoreCaseRegexTest(){
        String result = getResult("Check", "ch", Pattern.CASE_INSENSITIVE);
        Assert.assertEquals(result, "[Ch]eck");
    }

    @Test
    public void multiRowsTest1() throws IOException, ClassNotFoundException {
        String result = getResult(readTextFromFile("src/test/resources/multiRows.txt"),
                "\r\n\r\n", Pattern.CASE_INSENSITIVE);
        Assert.assertEquals(result, "checkMe[\r\n" +
                "\r\n" +
                "]\r\n" +
                "checkMe-2");
    }

    @Test
    public void emailsTest(){
        String pattern = "[\\w]{1,}[\\w\\.]*\\w+@\\w+[\\w\\.]*\\.\\w+";
        Assert.assertEquals(getResult("ben@mail.com",pattern), "[ben@mail.com]");
        Assert.assertEquals("[ben.msf@mail.com]",getResult("ben.msf@mail.com",pattern));
        Assert.assertEquals("[ben.msf@test.mail].",getResult("ben.msf@test.mail.",pattern));
        Assert.assertEquals("[ben.msf@test.mail].com",getResult("ben.msf@test.mail.com",pattern));
        Assert.assertEquals(".[msf@test.mail].com",getResult(".msf@test.mail.com",pattern));
        Assert.assertTrue(isEmptyResult(getResult("ben.@mail.com",pattern)));
        Assert.assertTrue(isEmptyResult(getResult("ben@.mail.com",pattern)));
    }

    @Test
    public void siteTest(){
        String pattern = "https?://\\w+[\\w.]+";
        Assert.assertEquals(getResult("http://ya.ru",pattern), "[http://ya.ru]");
        Assert.assertEquals(getResult("http://ya.test.ru",pattern), "[http://ya.test.ru]");
        Assert.assertEquals(getResult("https://ya.test.ru",pattern), "[https://ya.test.ru]");
        Assert.assertTrue(isEmptyResult(getResult("httpss://ya.ru",pattern)));
        Assert.assertTrue(isEmptyResult(getResult("https://*ya.ru",pattern)));
        Assert.assertTrue(isEmptyResult(getResult("https:/*ya.ru",pattern)));
        Assert.assertTrue(isEmptyResult(getResult("https:/.*ya.ru",pattern)));
    }

    @Test
    public void dateTest1(){
        String pattern = "[\\d]{1,2}[-/.]{1}[\\d]{1,2}[-/.]{1}[\\d]{4}";
        Assert.assertEquals(getResult("12-12-2012",pattern), "[12-12-2012]");
        Assert.assertEquals(getResult("12/12/2012",pattern), "[12/12/2012]");
        Assert.assertEquals(getResult("6/6/1995",pattern), "[6/6/1995]");
        Assert.assertTrue(isEmptyResult(getResult("12:12.2012",pattern)));
        Assert.assertTrue(isEmptyResult(getResult("12..12.2012",pattern)));
    }

    @Test
    public void notGreedyTest(){
        String pattern = "<[Aa]>.*?</[Aa]>";
        String inputText = "<a>test</a>swll<a>2</a>";
        LazyMatchFormatter matchResultFormatter = new LazyMatchFormatter(inputText, pattern);
        Assert.assertEquals(matchResultFormatter.format(), "[<a>test</a>]swll[<a>2</a>]");
        pattern = "<[Aa]>.*</[Aa]>";
        GreedyMatchFormatter greedyMatchFormatter = new GreedyMatchFormatter(inputText, pattern);
        Assert.assertEquals(greedyMatchFormatter.format(), "[<a>test</a>swll<a>2</a>]");
    }
    // ^ - начало в таком случае текста, а не строки
    // $ - аналогично
    @Test
    public void multiRowsTest2(){
        String pattern = "(?m).*$";
        String inputText = "test\ntest1\ntest2\n";
        Assert.assertEquals(getResult(inputText, pattern), "[test]\n[test1]\n[test2]\n");
        pattern = ".*$";
        Assert.assertEquals(getResult(inputText, pattern), "test\ntest1\n[test2]\n");
        pattern = "(?m)^test1.*$";
        Assert.assertEquals(getResult(inputText, pattern), "test\n[test1]\ntest2\n");
        pattern = "^test1.*";
        Assert.assertTrue(isEmptyResult(getResult(inputText, pattern)));
        pattern = "^test1*$";
        Assert.assertTrue(isEmptyResult(getResult(inputText, pattern)));
    }

    @Test
    public void dateTest2(){
        String pattern = "([\\d]{2}-){2}[\\d]{4}";
        String inputText = "12-12-2012";
        Assert.assertEquals(getResult(inputText, pattern), "[12-12-2012]");
        pattern = "(([\\d]{2}-){2}(19|20)[\\d]{2})|(([\\d]{2}\\.){2}(19|20)[\\d]{2})|(([\\d]{2}/){2}(19|20)[\\d]{2})";
        inputText = "12-12-2012";
        Assert.assertEquals(getResult(inputText, pattern), "[12-12-2012]");
        inputText = "12.12.2012";
        Assert.assertEquals(getResult(inputText, pattern), "[12.12.2012]");
        inputText = "12/12/2012";
        Assert.assertEquals(getResult(inputText, pattern), "[12/12/2012]");
        inputText = "12.12-2012";
        Assert.assertTrue(isEmptyResult(getResult(inputText, pattern)));
        inputText = "12-12-1812";
        Assert.assertTrue(isEmptyResult(getResult(inputText, pattern)));
    }

    //следует писать по убыванию строгости выражений
    //дабы при поиске не потерять часть информации
    @Test
    public void ipTest(){
        String ipMask = "((25[0-5])|(2[0-4]\\d])|(1\\d{2})|(\\d{2})|(\\d))";
        String pattern = "("+ipMask+"\\.){3}"+ipMask;
        String inputText = "8.8.8.8";
        Assert.assertEquals(getResult(inputText, pattern), "[8.8.8.8]");
        inputText = "255.255.255.120";
        Assert.assertEquals(getResult(inputText, pattern), "[255.255.255.120]");
        inputText = "255.256.255.0";
        Assert.assertTrue(isEmptyResult(getResult(inputText, pattern)));
    }

    @Test
    public void wordTest(){
        String inputText = "i love cats and subcatalogs";
        String pattern = "\\bcat";
        Assert.assertEquals(getResult(inputText, pattern), "i love [cat]s and subcatalogs");
        pattern = "\\bcat\\b";
        Assert.assertTrue(isEmptyResult(getResult(inputText, pattern)));
    }

    @Test
    public void linksTest(){
        String inputText = "i love cats and cats and horses";
        String pattern = "(\\b\\w{2,}\\b).*\\1";
        Assert.assertEquals(getResult(inputText, pattern), "i love [cats and cats] and horses");
    }


    @Test
    public void linksTest2(){
        String inputText = "<h1>Hello!</h1>" +
                "<p>This is my site<p>" +
                "<h2>News</h2>" +
                "<h3>Java 13 is now released</h4>";
        String pattern = "<([hH][1-6])>.*</\\1>";
        LazyMatchFormatter lazyMatchFormatter = new LazyMatchFormatter(inputText, pattern);
        Assert.assertEquals(lazyMatchFormatter.format(),
                "[<h1>Hello!</h1>]" +
                        "<p>This is my site<p>" +
                        "[<h2>News</h2>]" +
                        "<h3>Java 13 is now released</h4>");
    }


    @Test
    public void posMatcherTest(){
        String input = "I paid $50 for 100 apples and 10$ paid later";
        String pattern = "\\b(?<!\\$)\\d+(?!\\$)\\b";
        PosGreedyMatchFormatter posGreedyMatchFormatter = new PosGreedyMatchFormatter(input, pattern);
        System.out.println(posGreedyMatchFormatter.format());
        pattern = "\\bpaid\\b";
        posGreedyMatchFormatter = new PosGreedyMatchFormatter(input, pattern);
        System.out.println(posGreedyMatchFormatter.format());


    }



    private String readTextFromFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
    private String getResult(String input, String regExp){
        PosGreedyMatchFormatter formatter = new PosGreedyMatchFormatter(input, regExp);
        return formatter.format();
    }
    private String getResult(String input, String regExp, int flags){
        PosGreedyMatchFormatter formatter = new PosGreedyMatchFormatter(input, regExp, flags);
        return formatter.format();
    }
    private boolean isEmptyResult(String result){
        return "[]".equals(result);
    }
}
