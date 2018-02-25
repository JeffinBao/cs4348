package project1;

import java.util.Scanner;

/**
 * Author: baojianfeng
 * Date: 2018-02-19
 * Description: sample code provided by the professor
 */
public class Getab {

    public static void main(String[] args) {
        int a = 5;
        int b = 10;

        Scanner sc = new Scanner(System.in);

        String s = sc.nextLine();

        while (s.compareTo("X") != 0)
        {
            if (s.compareTo("A") == 0)
                System.out.println(a);
            else if (s.compareTo("B") == 0)
                System.out.println(b);

            s = sc.nextLine();
        }
    }
}
