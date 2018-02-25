package project1;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

/**
 * Author: baojianfeng
 * Date: 2018-02-19
 * Description: Developing basic CPU functions: implementing PC, SP, etc; fetching instructions from Memory; writing PC and SP into stack in the memory.
 */
public class Cpu {
    private Scanner sc;
    private PrintWriter pw;
    private int PC = 0; // program counter
    private int SP = USER_STACK; // stack pointer
    private int IR = 0; // instruction register
    private int AC = 0; // accumulator
    private int X  = 0; // X variable
    private int Y  = 0; // Y variable
    private static final int SYSTEM_CALL = 1500;
    private static final int TIMER = 1000;
    private static final int SYSTEM_STACK = 2000;
    private static final int USER_STACK = 1000;
    public static final String OPS_READ = "0";
    public static final String OPS_WRITE = "1";
    private static final String MODE_USER = "0";
    private static final String MODE_KERNEL = "1";
    private String mode = "0"; // have two options: user_mode and kernel_mode
    private boolean interruptFlag = false;

    public Cpu(Scanner sc, PrintWriter pw) {
        this.sc = sc;
        this.pw = pw;
    }

    /**
     * execute input instruction according to specific rules
     * @param instruction instruction fetched from memory
     */
    public void execute(int instruction) {
        switch (instruction) {
            case 1: {
                // load the value into the AC
                IR = readValByPC();
                AC = IR;
                break;
            }
            case 2: {
                // load the value at the address into the AC
                int addr = readValByPC();
                IR = readValByAddr(addr);
                AC = IR;
                break;
            }
            case 3: {
                // load the value from the address found in the given address into the AC
                int addr1 = readValByPC();
                int addr2 = readValByAddr(addr1);
                IR = readValByAddr(addr2);
                AC = IR;
                break;
            }
            case 4: {
                // Load the value at (address+X) into the AC
                int addr = readValByPC();
                IR = readValByAddr(addr + X);
                AC = IR;
                break;
            }
            case 5: {
                // Load the value at (address+Y) into the AC
                int addr = readValByPC();
                IR = readValByAddr(addr + Y);
                AC = IR;
                break;
            }
            case 6: {
                // Load from (Sp+X) into the AC
                IR = readValByAddr(SP + X);
                AC = IR;
                break;
            }
            case 7: {
                // Store the value in the AC into the address
                IR = readValByPC();
                writeValByAddr(IR, AC);
                break;
            }
            case 8: {
                // Gets a random int from 1 to 100 into the AC
                AC = generateRandom();
                break;
            }
            case 9: {
                // If port=1, writes AC as an int to the screen
                // If port=2, writes AC as a char to the screen
                IR = readValByPC();
                int port = IR;
                if (port == 1) {
                    System.out.print(AC);
                } else if (port == 2) {
                    char temp = (char) AC;
                    System.out.print(temp);
                }
                break;
            }
            case 10: {
                // Add the value in X to the AC
                AC += X;
                break;
            }
            case 11: {
                // Add the value in Y to the AC
                AC += Y;
                break;
            }
            case 12: {
                // Subtract the value in X from the AC
                AC -= X;
                break;
            }
            case 13: {
                // Subtract the value in Y from the AC
                AC -= Y;
                break;
            }
            case 14: {
                // Copy the value in the AC to X
                X = AC;
                break;
            }
            case 15: {
                // Copy the value in X to the AC
                AC = X;
                break;
            }
            case 16: {
                // Copy the value in the AC to Y
                Y = AC;
                break;
            }
            case 17: {
                // Copy the value in Y to the AC
                AC = Y;
                break;
            }
            case 18: {
                // Copy the value in AC to the SP
                SP = AC;
                break;
            }
            case 19: {
                // Copy the value in SP to the AC
                AC = SP;
                break;
            }
            case 20: {
                // Jump to the address
                IR = readValByPC();
                PC = IR;
                break;
            }
            case 21: {
                // Jump to the address only if the value in the AC is zero
                // IR = readValByPC() should be outside if statement,
                // because no matter it will jump to specific address or not,
                // the next instruction should be read first
                IR = readValByPC();
                if (AC == 0) {
                    PC = IR;
                }
                break;
            }
            case 22: {
                // Jump to the address only if the value in the AC is not zero
                // IR = readValByPC() should be outside if statement,
                // because no matter it will jump to specific address or not,
                // the next instruction should be read first
                IR = readValByPC();
                if (AC != 0) {
                    PC = IR;
                }
                break;
            }
            case 23: {
                // Push return address onto stack, jump to the address
                SP--;
                writeValByAddr(SP, PC + 1);
                IR = readValByPC();
                PC = IR;
                break;
            }
            case 24: {
                // Pop return address from the stack, jump to the address
                IR = readValByAddr(SP);
                SP++;
                PC = IR;
                break;
            }
            case 25: {
                // Increment the value in X
                X++;
                break;
            }
            case 26: {
                // Decrement the value in X
                X--;
                break;
            }
            case 27: {
                // Push AC onto stack
                pushToStack(AC);
                break;
            }
            case 28: {
                // Pop from stack into AC
                AC = popFromStack();
                break;
            }
            case 29: {
                // Perform system call
                // set mode to mode_kernel before executing push
                mode = MODE_KERNEL;
                interruptFlag = true;
                int spCur = SP;
                SP = SYSTEM_STACK;
                pushToStack(spCur);
                pushToStack(PC);
                PC = SYSTEM_CALL;
                break;
            }
            case 30: {
                // Return from system call
                // after pop, set mode back to mode_user
                PC = popFromStack();
                SP = popFromStack();
                mode = MODE_USER;
                interruptFlag = false;
                break;
            }
        }
    }

    /**
     * start a timer
     */
    public void startTimer() {
        mode = MODE_KERNEL;
        interruptFlag = true;
        int spCur = SP;
        PC--; // store previous PC value, since every readValByPC method will increment PC
        SP = SYSTEM_STACK;
        pushToStack(spCur);
        pushToStack(PC);
        PC = TIMER;
    }

    /**
     * read value from memory according to the PC(program counter) address
     * @return value read from memory
     */
    private int readValByPC() {
        return readValByAddr(PC++);
    }

    /**
     * read value from memory according to the address
     * @param addr address
     * @return value read from memory
     */
    private int readValByAddr(int addr) {
        if (mode.equals(MODE_USER) && addr >= USER_STACK)
            throw new RuntimeException("now in user mode, can not read from system memory");

        String cmd = OPS_READ + " " + addr;
        pw.println(cmd);
        pw.flush();

        if (sc.hasNext()) {
            String ins = sc.next();
            if (!ins.isEmpty())
                return Integer.parseInt(ins);
        }

        return -1;

    }

    /**
     * send address and value to memory for updating the value at the specific address
     * @param addr address
     * @param val new value
     */
    private void writeValByAddr(int addr, int val) {
        if (mode.equals(MODE_USER) && addr >= USER_STACK)
            throw new RuntimeException("now in user mode, can not write into system memory");
        pw.println(OPS_WRITE + " " + addr + " " + val);
        pw.flush();
    }

    /**
     * push value to stack
     * @param val val to be pushed to the stack
     */
    private void pushToStack(int val) {
        SP--;
        writeValByAddr(SP, val);
    }

    /**
     * pop value from stack
     * @return value at stack pointer address
     */
    private int popFromStack() {
        IR = readValByAddr(SP);
        SP++;

        return IR;
    }

    /**
     * generate random number from 1 - 100
     * @return random integer
     */
    private int generateRandom() {
        Random random = new Random();
        return 1 + random.nextInt(100);
    }

    public static void main(String[] args) {
        // if executing from the command line, should pass args[0] argument as filePath
        String filePath = "/Users/jeffinbao/Documents/USA/UTD/18Spring/CS4348/Assignment/Proj1/sample1.txt";
        int timerCount = 20;
        int count = timerCount;
        try {
            Runtime rt = Runtime.getRuntime();
            // set classpath in order to find the .class file
            Process proc = rt.exec("java -classpath target/classes project1.Memory " + filePath);

            InputStream is = proc.getInputStream();
            OutputStream os = proc.getOutputStream();

            Scanner scFromMemory = new Scanner(is);
            PrintWriter pwToMemory = new PrintWriter(os);

            Cpu cpu = new Cpu(scFromMemory, pwToMemory);
            int instruction = cpu.readValByPC();
            while (instruction != 50) {
                if (count == 0) {
                    if (!cpu.interruptFlag) { // interruptFlag is used for preventing nested interrupt
                        cpu.startTimer();
                        instruction = cpu.readValByPC();
                    }

                    count = timerCount; // reset the timer count
                    continue;
                }
                cpu.execute(instruction);
                count--;
                instruction = cpu.readValByPC();
            }

            // send message to memory in order to shut down the memory
            pwToMemory.println(instruction);
            pwToMemory.flush();

            proc.waitFor();
            int exitVal = proc.exitValue();
            System.out.println("Process exited: " + exitVal);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
