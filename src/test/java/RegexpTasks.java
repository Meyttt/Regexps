import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public void ignoreCaseRegex(){
        String result = getResult("Check", "ch", Pattern.CASE_INSENSITIVE);
        Assert.assertEquals(result, "[Ch]eck");
    }

    @Test
    public void newRowTest() throws IOException, ClassNotFoundException {
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
        Assert.assertEquals("[ben.msf@test.mail.com]",getResult("ben.msf@test.mail.com",pattern));
        Assert.assertEquals(".[msf@test.mail.com]",getResult(".msf@test.mail.com",pattern));
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
    public void notGreedy(){
        String greedyPattern = ".*";
        String pattern = "(.*)?";
        System.out.println(getResult("test", greedyPattern));
        System.out.println(getResult("test", pattern));
    }



    private String readTextFromFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
    private String getResult(String input, String regExp){
        GreedyMatchFormatter formatter = new GreedyMatchFormatter(regExp, input);
        return formatter.format();
    }
    private String getResult(String input, String regExp, int flags){
        GreedyMatchFormatter formatter = new GreedyMatchFormatter(regExp, input, flags);
        return formatter.format();
    }
    private boolean isEmptyResult(String result){
        return "[]".equals(result);
    }
}
