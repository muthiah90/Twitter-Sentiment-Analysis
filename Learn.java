import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.jsoup.Jsoup;

/**
 * Created by Muthiah & Kamala on 11/7/2015.
 * This class reads all tweets from the given excel and pushes them into the corresponding text file based on the class label.
 * The text file created from this method is used to train the classifier
 */
public class Learn {

    public static void main(String [] args)
    {
        TrainerClass trainerClass = new TrainerClass();
        DataCleanser dataCleanser = new DataCleanser();
        int rowNum = 0;

        try
        {
            //Initializing the workbook and the text files for pushing the tweets according to their class label
            Workbook workbook = Workbook.getWorkbook(new File("E:\\Data and Text Mining\\Project 2\\Tweets.xls"));
            File positiveFile = new File("E:\\Data and Text Mining\\Project 2\\Test Directory\\Positive\\Positive.txt");
            File negativeFile = new File("E:\\Data and Text Mining\\Project 2\\Test Directory\\Negative\\Negative.txt");
            File neutralFile = new File("E:\\Data and Text Mining\\Project 2\\Test Directory\\Neutral\\Neutral.txt");
            FileWriter posWriter = new FileWriter(positiveFile);
            FileWriter negWriter = new FileWriter(negativeFile);
            FileWriter nuWriter = new FileWriter(neutralFile);
            BufferedWriter posBuffWriter = new BufferedWriter(posWriter);
            BufferedWriter negBuffWriter = new BufferedWriter(negWriter);
            BufferedWriter nuBuffWriter = new BufferedWriter(nuWriter);
            posBuffWriter.flush();
            negBuffWriter.flush();

            //Reading the tweets from the desired sheet in the workbook
            Sheet sheet = workbook.getSheet("Obama");
            rowNum = sheet.getRows();
            System.out.println(rowNum);
            String contents = null;
            int j = 0;

            //Iterating through the rows in the given sheet with tweets
            for (int i = 2; i < rowNum; i++)
            {
                contents = sheet.getCell(3, i).getContents();
                if(contents != null)
                {
                    switch(sheet.getCell(4, i).getContents().toString())
                    {
                        //Positive case
                        case "1":
                            //posBuffWriter.write(Jsoup.parse(contents).text());
                            posBuffWriter.write(dataCleanser.CleanTweet(contents));
                            posBuffWriter.newLine();
                            break;
                        //Negative case
                        case "-1":
                            //negBuffWriter.write(Jsoup.parse(contents).text());
                            negBuffWriter.write(dataCleanser.CleanTweet(contents));
                            negBuffWriter.newLine();
                            break;
                        //Neutral Case
                        case "0":
                            //negBuffWriter.write(Jsoup.parse(contents).text());
                            nuBuffWriter.write(dataCleanser.CleanTweet(contents));
                            nuBuffWriter.newLine();
                            break;
                        default:
                            break;
                    }

                }
            }

            //Closing all file readers and writers
            posBuffWriter.close();
            posWriter.close();
            negBuffWriter.close();
            negWriter.close();
            nuBuffWriter.close();
            nuWriter.close();
            //System.out.println(Jsoup.parse("<html> This is my html page </html> </br> <p> This api is working perfectly </p> <trr> </td").text());

            //Call the classifier trainer on the tweets that was extracted from the excel
            trainerClass.Train();
            System.out.println("Completed Learning");
            /*ClassificationController classificationController = new ClassificationController();
            System.out.println(classificationController.ClassifyTweet("Obama will win for sure"));*/

        }
        catch (IOException e)
        {
            System.out.println("IO Exception");
        }
        catch (jxl.read.biff.BiffException e)
        {
            System.out.println("Excel read exception");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Class not found exception");
        }
        /*catch (WriteException e)
        {
            System.out.println("Class not found exception");
        }*/

    }
}
