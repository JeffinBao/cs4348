# Operating System Concepts Course Projects

## Project1 -- CPU & Memory
### Basic Information
  - This is a project aims to simulate CPU and Memory interaction.The instruction used in this project is not complicated, and the stack size is only 2000. Hence, it can not support complex tasks.
  - **Cpu** supports functions like fetching instructions from **Memory**, executing instructions, pushing values to **Memory** stack and poping values from **Memory** stack. 
  - **Memory** supports functions like loading instructions from a file, passing instructions to **Cpu** and updating values on the stack.
### Problem Records
  - First, since I used Intellij IDEA for developing and debugging, I need to set classpath for the Memory execution file. Then I found the path of Memory execution file and used the path to initialize Memory “process”. In this way, I can debug my program step by step in Intellij IDEA.

  - Second, implementing a timer was really a mess in the first place. Therefore, I first skipped the timer part and developed other functions first. After that, I added a timer into the program. However, when testing the sample3, which both have system call and timer, the program ran into some errors, since system call and timer together may cause nested interrupt execution and this should not be allowed according to the project requirement. Therefore, I added an “interruptFlag” to disable nested interrupt execution if currently an interrupt was running.

  - Third, for the memory protection part, I created two modes: “MODE_USER” and “MODE_KERNEL” and checked the input address to see whether it violated the rule. In this way, if current mode is “MODE_USER” and the address is greater than 1000, it will cause an exception and stop the program.

  - In addition, for the “read instruction” and “write value into stack” operation, I used two verification marks: “OPS_READ” and “OPS_WRITE”. In this way, Memory process can distinguish from read and write, and execute related operations.

  - What’s more, at first, I found my program can not stop running after executing all the instructions. After checking the code, I found that I didn’t pass the exit instruction, which was 50, to the Memory process and the Cpu process was waiting for the Memory to exit first. Therefore, I added “pw.println(instruction)” and “pw.flush()” and passed the exit instruction to the Memory to exit the program. 





