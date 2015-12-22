import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.LMClassifier;
import com.aliasi.util.AbstractExternalizable;

import java.io.File;
import java.io.IOException;

/**
 * Created by Muthiah & Kamala on 11/7/2015.
 * This file is used to read the trained classifier that was saved.
 * It then uses this trained classifier to predict the class of the unlabeled tweets
 */
public class ClassificationController
{
    String[] classificationCategories;
    LMClassifier lmClassifier;

    public ClassificationController()
    {
        try
        {
            //Reading the file that has the trained classifier
            lmClassifier = (LMClassifier) AbstractExternalizable.readObject(new File("E:\\Data and Text Mining\\Project 2\\Classifier_Reference\\reference.txt"));
            classificationCategories = lmClassifier.categories();
        }

        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Method to classify an unlabeled tweet based on the trained classifier
     * @param text
     * @return
     */
    public String ClassifyTweet(String text)
    {
        ConditionalClassification classification = lmClassifier.classify(text);
        return classification.bestCategory();
    }
}