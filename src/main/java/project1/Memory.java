package project1;

import java.io.*;
import java.util.Scanner;

/**
 * Author: baojianfeng
 * Date: 2018-02-19
 * Description: Memory part has functions like loading instructions from a file into stack, reading and writing values at certain address
 */
public class Memory {
    private int[] instructions; // instruction stack: 0 - 999(user mode), 1000 - 1999(kernel mode)

    public Memory() {
        instructions = new int[2000];
    }

    /**
     * load instructions from a file
     * @param fileName path of the file
     */
    public void readInsFromFile(String fileName) {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line = br.readLine();
            int i = 0; // address pointer
            while (line != null) {
                if (line.startsWith(".")) {
                    // jump to specific address
                    String[] strArr = line.split(" ", 2);
                    i = Integer.parseInt(strArr[0].substring(1)); // change address pointer to specific value
                } else if (!line.isEmpty() && !line.startsWith(" ")) {
                    // read instructions
                    String[] strArr = line.split(" ", 2);
                    instructions[i++] = Integer.parseInt(strArr[0]);
                }

                line = br.readLine();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * write instruction into a specific address
     * @param addr address where to write into value
     * @param value value
     */
    public void write(int addr, int value) {
        if (addr >= 2000)
            throw new IndexOutOfBoundsException("instruction address can not exceed 2000");

        instructions[addr] = value;
    }

    /**
     * read instruction in a specific address
     * @param addr address where to read the instruction
     * @return a instruction
     */
    public int read(int addr) {
        if (addr >= 2000)
            throw new IndexOutOfBoundsException("instruction address can not exceed 2000");

        return instructions[addr];
    }

    public static void main(String[] args) {
        // if executing from the command line, should pass args[0] argument as filePath
        String filePath = "/Users/jeffinbao/Documents/sample5.txt";
        Memory memory = new Memory();
        memory.readInsFromFile(filePath);

        Scanner sc = new Scanner(System.in);
        String cmdFromCpu = sc.nextLine();
        // if the instruction is 50, exit while loop
        while (!cmdFromCpu.equals("50")) {
            String[] cmdArr = cmdFromCpu.split(" ");
            if (cmdArr[0].equals(Cpu.OPS_READ)) {
                System.out.println(memory.read(Integer.parseInt(cmdArr[1])));
            } else if (cmdArr[0].equals(Cpu.OPS_WRITE)) {
                memory.write(Integer.parseInt(cmdArr[1]), Integer.parseInt(cmdArr[2]));
            }
            cmdFromCpu = sc.nextLine();
        }
    }
}
