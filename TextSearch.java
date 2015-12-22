import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by User on 11/29/2015.
 */
public class TextSearch {
    public  static void main(int args[])
    {
        try
        {
            FileReader fileReader = new FileReader("E:\\Data and Text Mining\\Demo Day\\test-data\\large-data-2\\data2.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String newLine = null;

            while((newLine = bufferedReader.readLine()) != null)
            {

            }
        }
        catch (IOException e)
        {
            System.out.println(e);
        }

    }
}
