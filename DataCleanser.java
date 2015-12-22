import com.aliasi.spell.CompiledSpellChecker;
import com.aliasi.spell.SpellChecker;
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Muthiah & Kamala on 11/11/2015.
 * This class does the data cleansing of each tweets before its used for training and also for classification
 */
public class DataCleanser
{
    //Stop word list
    String[] stopwords = {"a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "amoungst", "amount", "an", "and", "another", "any", "anyhow", "anyone", "anything", "anyway", "anywhere", "are", "around", "as", "at", "back", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom", "but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven", "else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own", "part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};

    /**
     * This method takes the given tweet and filters all the unwanted texts and special characters from the tweet.
     * It also calls other methods for cleaning the tweets
     * @param tweet
     * @return
     */
    public String CleanTweet(String tweet)
    {
        //Jsoup API to remove the HTML tags from the tweets
        tweet = Jsoup.parse(tweet).text();

        //Removing all the words starting with @
        tweet =	tweet.replaceAll("[@][a-zA-Z0-9_]*","");
        //tweet = tweet.replaceAll("[#][a-zA-Z0-9_]*", "");

        //This is the remove all the special characters
        tweet = tweet.replaceAll("[><\\^\\|\\&\\*\\-\\+\\=!\\?;:,.()\\[\\]\\\"]", "");

        //This expression is to remove all the http links in the tweets
        tweet = tweet.replaceAll("http\\S+\\s?","");

        //This is to remove all the numbers in the tweets
        tweet = tweet.replaceAll("[_0-9_]", "");

        //This function call is to remove all the emoticons in the tweet
        tweet = removeEmojiAndSymbolFromString(tweet);
        tweet = tweet.replaceAll("[_0-9_]", "");

        //This is to convert all the tweets to lower case letters
        tweet = tweet.toLowerCase();

        //This is to remove any characters other than alphabets and numbers
        tweet = tweet.replaceAll("[^a-zA-Z']+"," ");
        //String t = "heeeello";

        //This is to replace the specific typing error where a same letter is typed continuously
        Pattern pattern = Pattern.compile("([a-z])\\1{3,}", Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(tweet);
        m.replaceAll("$1");
        // tweet = tweet.replaceAll("[%><\\^\\|\\&\\*\\-\\+\\=!\\?;:,.()\\[\\]\\\"]", "");

        //This function call is for correcting the spelling of the words used in the tweet
        JazzySpellChecker jazzySpellChecker = new JazzySpellChecker();
        tweet = jazzySpellChecker.getCorrectedLine(tweet);
        // System.out.print(tweet);

        //Splitting the tweet based on the space, so that we get them in individual words
        String newTweet = "";
        String [] splitTweet = tweet.split(" ");
        int i = 0;

        //This loop is to remove all the words that are of less than 2 characters
        for(i=0; i < splitTweet.length;  i++)
        {
            if(splitTweet[i].length() <= 2)
            {
                splitTweet[i] = "";
            }
        }

        //This is to process apostrophe words in the tweet
        for(i = 0; i < splitTweet.length; i++)
        {
            if(splitTweet[i].contains("'"))
            {
                ArrayList temp;
                String expand = isNotNullOrEmpty(temp = processApostrophe(splitTweet[i])) ? (String) temp.get(0) : null;

                if(expand!=null){
                    splitTweet[i]=expand;
                    splitTweet[i].replaceAll("'","");

                }
            }
            //System.out.println(splitTweet[i]);
            //This is to check and remove stop words from tweets
            /*if(Arrays.asList(stopwords).contains(splitTweet[i]))
            {
                //System.out.println("Removed: " + splitTweet[i]);
                splitTweet[i] = null;
            }*/
        }


        /*ArrayList<String> stemmedwords = stemWords(splitTweet);
        for(int j = 0; j < stemmedwords.size(); j++)
        {
            //System.out.println(splitTweet[j]);
            if(stemmedwords.get(j) != null)
            {
                //String word = sp.findMostSimilar("sevanty");
                //System.out.println(word);
                newTweet = newTweet + " " + stemmedwords.get(j);
            }
        }*/

        //This loop is get the stem word for every word in the tweet
        for(int j = 0; j < splitTweet.length; j++)
        {
            //System.out.println(splitTweet[j]);
            if(splitTweet[j] != null)
            {
                newTweet = newTweet + " " + PorterStemmerTokenizerFactory.stem(splitTweet[j]);
            }
            //newTweet = newTweet + " " + PorterStemmerTokenizerFactory.stem(splitTweet[j]);
        }

        //newTweet = splitTweet.toString();
        /*System.out.println(newTweet);
        System.out.println(tweet);
        System.out.println(splitTweet.length);*/
        //System.out.println(tweet);
        return newTweet;
    }

    /**
     * This function is to find the stem word for the given word
     * @param allword
     * @return
     */
    private ArrayList<String> stemWords(String allword[]) {
        ArrayList<String> stemmedwords = new ArrayList<String>();
        int k=0;
        char[] wordCharArray;
        Stemmer s = new Stemmer();
        for(int j=0; j<allword.length;j++){
            if(allword[j]!=null){
                String word = allword[j];
                wordCharArray = word.toCharArray();

                for (int i = 0; i < word.length(); i++) {
                    s.add(wordCharArray[i]);
                }
                s.stem(); // call to "stem" the word
                stemmedwords.add(s.toString());
            }
        }
        return stemmedwords;
    }

    /**
     * This method is check for stop words and removing the same
     * @param fileName
     * @return
     * @throws IOException
     */
    static String processStopWords(String fileName) throws IOException {
        String stopWords;
        FileInputStream inputStream = new FileInputStream(fileName);
        try {
            stopWords = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }
        return stopWords;

    }

    /**
     * This function is to check and remove emoticons from the tweets
     * @param content
     * @return
     */
    String removeEmojiAndSymbolFromString(String content)
    {
        String utf8tweet = "";
        try {
            byte[] utf8Bytes = content.getBytes("UTF-8");
            utf8tweet = new String(utf8Bytes, "UTF-8");
        } catch (
                UnsupportedEncodingException e
                ) {
            e.printStackTrace();
        }
        Pattern unicodeOutliers =
                Pattern.compile(
                        "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                        Pattern.UNICODE_CASE |
                                Pattern.CANON_EQ |
                                Pattern.CASE_INSENSITIVE
                );
        Matcher unicodeOutlierMatcher =	unicodeOutliers.matcher(utf8tweet);

        utf8tweet =	unicodeOutlierMatcher.replaceAll(" ");
        return utf8tweet;
    }

    /**
     * This method is to check for apostrophe and have the same removed from the tweets
     * @param givenWord
     * @return
     */
    public static ArrayList processApostrophe(String givenWord)
    {  givenWord = givenWord.trim();
        int length = givenWord.length();
        if(length>1){
            char firstLetter = givenWord.charAt(0);
            if (firstLetter == '\'' && length >= 2)
                firstLetter = givenWord.charAt(1);
            int apostrophePosition = givenWord.indexOf('\'');
            if (apostrophePosition < 0) return null; // no apostrophe
            ArrayList result = new ArrayList(2);
            String expandedForm = givenWord;
            String description = "U";  // default is Unknown
            if (length <= 2)
            {  result.add(expandedForm);
                result.add(description);
                return result;
            }

            String word = givenWord.toLowerCase();

            // Check for initial "'t":
            if (word.substring(0,2).equals("'t"))
            {  if (word.equals("'til"))
            {  if (Character.isLowerCase(firstLetter))
                expandedForm = "until";
            else
                expandedForm = "Until";
                description = "C";
                result.add(expandedForm);
                result.add(description);
                return result;
            }
                // The initial "'t" is a contraction for "it".
                // Expand it first:
                if (Character.isLowerCase(firstLetter))
                {  word = "it " + word.substring(2);
                    firstLetter = 'i';
                }
                else
                {  word = "It " + word.substring(2);
                    firstLetter = 'I';
                }
                // Check for special case of 't's or 'T's:
                if (givenWord.equalsIgnoreCase("'t's"))
                {  expandedForm = firstLetter + "t is";
                    description = "C";
                    result.add(expandedForm);
                    result.add(description);
                    return result;
                }
                // Now see if there is another apostrophe:
                apostrophePosition = word.indexOf('\'');
                if (apostrophePosition < 0)  // no other apostrophe
                {  expandedForm = word;
                    description = "C";
                    result.add(expandedForm);
                    result.add(description);
                    return result;
                }
                length = word.length();
            }

            if (apostrophePosition == length-1) // final apostrophe
            {  if (word.substring(length-2).equals("s'"))
            {  expandedForm = firstLetter + word.substring(1, apostrophePosition);
                // + " APOSTROPHE";
                description = "P";
            }
            else if (word.substring(length-3).equals("in'"))
            {  expandedForm = firstLetter + word.substring(1, length-1) + "g";
                description = "C";
            }
            else  // Unknown form
            {  expandedForm = givenWord;
                description = "U";
            }
            }
            else if (apostrophePosition == length-2) // penultimate apostrophe
            {  if (word.substring(length-1).equals("s"))
            {  String beforeApostrophe
                    = firstLetter + word.substring(1, apostrophePosition);
                if (length <= 3)
                {  expandedForm = beforeApostrophe ;//+ " APOSTROPHEs";
                    description = "CLP";  // e.g. x's
                }
                else if (word.equals("he's") || word.equals("she's")
                        || word.equals("it's"))
                {  expandedForm = beforeApostrophe + " is";
                    description = "C";
                }
                else
                {  expandedForm = beforeApostrophe ;//+" APOSTROPHEs";
                    description = "CP";
                }
            }
            else if (word.equals("i'm"))
            {  expandedForm = firstLetter + " am";
                description = "C";
            }
            else
            {  description = "C";
                if (word.substring(length-3).equals("n't"))
                {  if (word.equals("can't"))
                    expandedForm = firstLetter + "annot";
                else
                    expandedForm
                            = firstLetter + word.substring(1, length-3) + " not";
                }
                else if (word.substring(length-2).equals("'d"))
                    expandedForm
                            = firstLetter + word.substring(1, length-2) + " 'd";
                else
                {  expandedForm = givenWord;
                    description = "U";
                }
            }
            }
            else if (apostrophePosition == length-3) // antepenultimate apostrophe
            {  String lastThree = word.substring(length-3);
                String allButLastThree;
                if(length>3){
                    allButLastThree
                            = firstLetter + word.substring(1, length-3);
                }
                else
                    allButLastThree = "";

                description = "C";
                if (lastThree.equals("'re"))
                    expandedForm = allButLastThree + " are";
                else if (lastThree.equals("'ld"))
                    expandedForm = allButLastThree + " would";
                else if (lastThree.equals("'ll"))
                    expandedForm = allButLastThree + " will";
                else
                {  expandedForm = givenWord;
                    description = "U";
                }
            }
            else                          // unknown apostrophe form
            {  expandedForm = givenWord;
                description = "U";
            }

            result.add(expandedForm);
            result.add(description);
            return result;
        }
        else return null;
    }  // end of processApostrophe

    static boolean isNotNullOrEmpty(ArrayList arrayList){
        return (arrayList != null && !arrayList.isEmpty());
    }
}
