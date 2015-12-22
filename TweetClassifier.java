import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Muthiah & Kamala on 11/11/2015.
 * This class is used to read unlabeled tweets from the given excel and classify them using the trained classifier
 */
public class TweetClassifier {
    public static void main(String [] args)
    {
        //Initializing the classification controller
        ClassificationController classificationController = new ClassificationController();
        DataCleanser dataCleanser = new DataCleanser();
        //System.out.println(classificationController.ClassifyTweet("Obama will win for sure"));
        int rowNum = 0;

        try
        {
            //Initializing the work book to read tweets from and another workbook to write the results
            Workbook readWorkbook = Workbook.getWorkbook(new File("E:\\Data and Text Mining\\Project 2\\Test_Tweets.xls"));
            WritableWorkbook wWorkbook = Workbook.createWorkbook(new File("E:\\Data and Text Mining\\Project 2\\Analysis_Excel.xls"));
            Sheet readSheet = readWorkbook.getSheet("Obama");
            WritableSheet wSheet = wWorkbook.createSheet("Obama", 1);
            /*wSheet.addCell(new Label(0, 0, "Hello"));
            wSheet.addCell(new Label(1, 0, "Hello1"));
            wSheet.addCell(new Label(2, 0, "Hello2"));
            wWorkbook.write();
            wWorkbook.close();*/
            rowNum = readSheet.getRows();
            System.out.println();
            String contents = null;
            String tweetClass = null;
            String trainerClass = null;
            int j = 1;

            //This loop is to check the results with the corresponding labels given for each tweet
            for (int i = 1; i < rowNum; i++)
            {
                contents = readSheet.getCell(0, i).getContents();
                trainerClass = readSheet.getCell(1, i).getContents().toString();
                if(contents != null)
                {
                    switch(trainerClass)
                    {
                        case "1":
                            //tweetClass = classificationController.ClassifyTweet(Jsoup.parse(contents).text());
                            tweetClass = classificationController.ClassifyTweet(dataCleanser.CleanTweet(contents));
                            wSheet.addCell(new Label(0, j, contents));
                            wSheet.addCell(new Label(1, j, trainerClass));
                            wSheet.addCell(new Label(2, j, tweetClass));
                            j++;
                            break;
                        case "-1":
                            //tweetClass = classificationController.ClassifyTweet(Jsoup.parse(contents).text());
                            tweetClass = classificationController.ClassifyTweet(dataCleanser.CleanTweet(contents));
                            wSheet.addCell(new Label(0, j, contents));
                            wSheet.addCell(new Label(1, j, trainerClass));
                            wSheet.addCell(new Label(2, j, tweetClass));
                            j++;
                            break;
                        case "0":
                            //tweetClass = classificationController.ClassifyTweet(Jsoup.parse(contents).text());
                            tweetClass = classificationController.ClassifyTweet(dataCleanser.CleanTweet(contents));
                            wSheet.addCell(new Label(0, j, contents));
                            wSheet.addCell(new Label(1, j, trainerClass));
                            wSheet.addCell(new Label(2, j, tweetClass));
                            j++;
                            break;
                        default:
                            break;
                    }

                }
            }
            wWorkbook.write();
            wWorkbook.close();
            readWorkbook.close();

            //This function call is to calculate the accuracy of classification
            CalculateEfficiency();
            //dataCleanser.CleanTweet(" Second <a>president</a>ial Debate: Hollywood Reacts to <e>Obama</e> vs. <e>Romney</e> Round Two http://t.co/zFNm0cjo");

        }
        catch (IOException e)
        {
            System.out.println("IO Exception");
        }
        catch (jxl.read.biff.BiffException e)
        {
            System.out.println("Excel read exception");
        }
        /*catch (ClassNotFoundException e)
        {
            System.out.println("Class not found exception");
        }*/
        catch (WriteException e)
        {
            System.out.println("Class not found exception");
        }
    }

    /**
     * This method is to calculate the accuracy of the classifier.
     */
    public static void CalculateEfficiency()
    {
        int rowNum = 0;
        String classifiedClass = null;
        String actualClass = null;
        float precision, recall, fscore;

        int correctClassification = 0;

        int correctPositive = 0;
        int correctNegative = 0;
        int correctNeutral = 0;

        int actualPositive = 0;
        int actualNegative = 0;
        int actualNeutral = 0;

        int classifiedPositive = 0;
        int classifiedNegative = 0;
        int classifiedNeutral = 0;
        try
        {
            Workbook readWorkbook = Workbook.getWorkbook(new File("E:\\Data and Text Mining\\Project 2\\Analysis_Excel.xls"));
            Sheet readSheet = readWorkbook.getSheet("Obama");
            rowNum = readSheet.getRows();
            System.out.println("Total Number of Tweets: " + rowNum);
            String contents = null;
            for (int i = 1; i < rowNum; i++)
            {
                contents = readSheet.getCell(0, i).getContents();
                if(contents != null)
                {
                    actualClass = readSheet.getCell(1, i).getContents().toString();
                    classifiedClass = readSheet.getCell(2, i).getContents().toString();
                    switch(classifiedClass)
                    {
                        case "Positive":
                            classifiedPositive++;
                            break;
                        case "Negative":
                            classifiedNegative++;
                            break;
                        case "Neutral":
                            classifiedNeutral++;
                            break;
                        default:
                            break;
                    }

                    switch(actualClass)
                    {
                        case "1":
                            actualPositive++;
                            if(classifiedClass.equalsIgnoreCase("Positive"))
                            {
                                correctPositive++;
                                correctClassification++;
                            }
                            break;
                        case "-1":
                            actualNegative++;
                            if(classifiedClass.equalsIgnoreCase("Negative"))
                            {
                                correctNegative++;
                                correctClassification++;
                            }
                            break;
                        case "0":
                            actualNeutral++;
                            if(classifiedClass.equalsIgnoreCase("Neutral"))
                            {
                                correctNeutral++;
                                correctClassification++;
                            }
                    }


                    /*switch(readSheet.getCell(1, i).getContents().toString())
                    {
                        case "1":
                            if(readSheet.getCell(2, i).getContents().toString().compareToIgnoreCase("Positive") == 0)
                            {
                                correctClassification++;
                            }
                            break;
                        case "-1":
                            if(readSheet.getCell(2, i).getContents().toString().compareToIgnoreCase("Negative") == 0)
                            {
                                correctClassification++;
                            }
                            break;
                        case "0":
                            if(readSheet.getCell(2, i).getContents().toString().compareToIgnoreCase("Neutral") == 0)
                            {
                                correctClassification++;
                            }
                            break;
                        default:
                            break;
                    }*/
                }
            }

            readWorkbook.close();

            FileWriter fileWriter = new FileWriter("E:\\Data and Text Mining\\Project 2\\result_obama.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.flush();
            bufferedWriter.write("-------------------------- Obama Tweets Classification Results --------------------------------");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            System.out.println("Correctly classified tweets: " + correctClassification);
            System.out.println("Accuracy: " + (float)correctClassification/(float)(rowNum - 1));

            System.out.println(" ");
            System.out.println("Positive Class");
            System.out.println("Precision: " + (float)correctPositive/(float)classifiedPositive);
            System.out.println("Recall : " + (float)correctPositive/((float)actualPositive));
            precision = (float)correctPositive/(float)classifiedPositive;
            recall = (float)correctPositive/(float)actualPositive;
            fscore = (2*precision*recall)/(precision + recall);
            System.out.println("F-Score : " + fscore);
            bufferedWriter.write("Positive Class");
            bufferedWriter.newLine();
            bufferedWriter.write("Precision: " + precision*100 + "%");
            bufferedWriter.newLine();
            bufferedWriter.write("Recall: " + recall*100 + "%");
            bufferedWriter.newLine();
            bufferedWriter.write("F-Score: " + fscore*100 + "%");
            bufferedWriter.newLine();
            bufferedWriter.newLine();

            System.out.println(" ");
            System.out.println("Negative Class");
            System.out.println("Precision: " + (float)correctNegative/(float)classifiedNegative);
            System.out.println("Recall : " + (float)correctNegative/((float)actualNegative));
            precision = (float)correctNegative/(float)classifiedNegative;
            recall = (float)correctNegative/(float)actualNegative;
            fscore = (2*precision*recall)/(precision + recall);
            System.out.println("F-Score : " + fscore);
            bufferedWriter.write("Negative Class");
            bufferedWriter.newLine();
            bufferedWriter.write("Precision: " + precision*100 + "%");
            bufferedWriter.newLine();
            bufferedWriter.write("Recall: " + recall*100 + "%");
            bufferedWriter.newLine();
            bufferedWriter.write("F-Score: " + fscore*100 + "%");
            bufferedWriter.newLine();
            bufferedWriter.newLine();

            System.out.println(" ");
            System.out.println("Neutral Class");
            System.out.println("Precision: " + (float)correctNeutral/(float)classifiedNeutral);
            System.out.println("Recall : " + (float)correctNeutral/((float)actualNeutral));
            precision = (float)correctNeutral/(float)classifiedNeutral;
            recall = (float)correctNeutral/(float)actualNeutral;
            fscore = (2*precision*recall)/(precision + recall);
            System.out.println("F-Score : " + fscore);
            bufferedWriter.write("Neutral Class");
            bufferedWriter.newLine();
            bufferedWriter.write("Precision: " + precision*100 + "%");
            bufferedWriter.newLine();
            bufferedWriter.write("Recall: " + recall*100 + "%");
            bufferedWriter.newLine();
            bufferedWriter.write("F-Score: " + fscore*100 + "%");

            bufferedWriter.close();
            fileWriter.close();
        }
        catch (BiffException e)
        {
            System.out.println("IO Biff Exception");
        }
        catch (IOException e)
        {
            System.out.println("IOException");
        }
    }
}
