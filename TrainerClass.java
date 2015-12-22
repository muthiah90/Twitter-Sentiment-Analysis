import com.aliasi.classify.*;
import com.aliasi.corpus.ObjectHandler;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Compilable;
import com.aliasi.util.Files;

import java.io.File;
import java.io.IOException;

/**
 * Created by Muthiah & Kamala on 11/7/2015.
 * This class is for training the classifier based on the given labeled example
 */
public class TrainerClass {

    public void Train() throws IOException, ClassNotFoundException
    {
        File trainDirectory;
        String[] classificationCategories;
        LMClassifier lmClassifier;

        //Location of the excel containing the labeled examples
        trainDirectory = new File("E:\\Data and Text Mining\\Project 2\\Test Directory");
        classificationCategories = trainDirectory.list();
        int nGram = 12; //the nGram level, any value between 7 and 12 works

        //Classifier that is being used for training the custom classifier
        lmClassifier = DynamicLMClassifier.createNGramProcess(classificationCategories, nGram);
        //lmClassifier = NaiveBayesClassifier.createNGramProcess(classificationCategories, nGram);
        //lmClassifier = BinaryLMClassifier.createNGramProcess(classificationCategories, nGram);

        //Data read from the excel is put into separate text files based on the class labels. (Positive, Negative and Neutral)
        //This loops iterates through the available directories which are the classes involved in classification and then reads
        //all the text documents in them to analyze the word frequencies for each of the word in the given class
        for (int i = 0; i < classificationCategories.length; ++i)
        {
            String category = classificationCategories[i];
            Classification classification = new Classification(category);
            File file = new File(trainDirectory, classificationCategories[i]);
            File[] trainFiles = file.listFiles();
            for (int j = 0; j < trainFiles.length; ++j)
            {
                File trainFile = trainFiles[j];
                String review = Files.readFromFile(trainFile, "ISO-8859-1");
                Classified classified = new Classified(review, classification);
                ((ObjectHandler)lmClassifier).handle(classified);
            }
        }

        //The trained classifier in then written into a external file for use. This prevents the need of training the classifier every time, as the trained
        //is readily available for use
        AbstractExternalizable.compileTo((Compilable) lmClassifier, new File("E:\\Data and Text Mining\\Project 2\\Classifier_Reference\\reference.txt"));
    }
}
