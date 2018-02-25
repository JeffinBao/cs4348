package project1;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Author: baojianfeng
 * Date: 2018-02-19
 * Description: sample code provided by the professor
 */
public class GetabMain {

    public static void main(String[] args) {
        try {
            int x;
            Runtime rt = Runtime.getRuntime();

            Process proc = rt.exec("java -classpath target/classes project1.Getab");

            InputStream is = proc.getInputStream();
            OutputStream os = proc.getOutputStream();

            Scanner sc = new Scanner(is);
            PrintWriter pw = new PrintWriter(os);

            Scanner input = new Scanner(System.in);

            System.out.println("Enter A or B (or X to exit): ");
            String s = input.nextLine();

            while (s.compareTo("X") != 0)
            {
                pw.println(s);
                pw.flush();

                String line = sc.nextLine();
                System.out.println(line);

                System.out.println("Enter A or B (or X to exit): ");
                s = input.nextLine();
            }

            pw.println(s);
            pw.flush();

            proc.waitFor();

            int exitVal = proc.exitValue();

            System.out.println("Process exited: " + exitVal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
