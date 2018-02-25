package project1;

import java.io.*;

/**
 * Author: baojianfeng
 * Date: 2018-02-19
 */
public class ReadFileTest {

    public static void main(String[] args) {
        String fileName = "/Users/jeffinbao/Documents/USA/UTD/18Spring/CS4348/Assignment/Proj1/examples/test.txt";
        try {
            FileInputStream fis = new FileInputStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line = br.readLine();
            while (line != null) {
                if (line.startsWith(".")) {
                    // jump to specific address
                    System.out.println("jump to specific address");
                } else if (!line.isEmpty() && !line.startsWith(" ")) {
                    // read instructions
                    System.out.println("read instructions");
                }

                line = br.readLine();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
