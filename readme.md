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

## Project2 -- Semaphore
### Design
  First, I’d like to discuss about the design of the simulation. The procedures of a patient coming to see a doctor are described as follows: 
  
  - The patient enters into the receptionist room, waiting to be registered. If the only receptionist is available, the patient will be registered, otherwise, he or she needs to wait in a queue(regQueue in the program) for the receptionist to be available.
  - Once the receptionist is available,  the receptionist will get the patient from the regQueue and register that patient. After finishing registration, the receptionist will signal the patient, and the patient will leave the receptionist and sit in the waiting room. After that, the receptionist will signal the nurse and tell him/her that a patient is ready.
  - Then, if at least one doctor is available, the nurse will take one patient to the doctor’s room. After receiving the signal from nurse, the patient will come to the doctor’s room. The doctor will wait until the patient arrive at his/her room. Next, the doctor will listen to symptoms told by the patient and give advices to the patient.
  - At last, the patient will leave the room and the doctor is free to serve the next patient.
### Problem Records
  Second, I’d like to talk about the problems I was facing when doing this project. I am listing all of them as follows:
  
  - One of the requirement of this project is “each thread should only print its own activities”. This means the patient and doctor information should be shared between different threads. This leads to a problem, how can I transmit patient information from patient thread to receptionist thread and finally connect one patient with a specific doctor. My solution is to implement two queues(regQueue, waitQueue). The regQueue is used for storing patients in a queue and waiting to be registered by the receptionist. The waitQueue is used for storing registered patient in a queue and waiting to be called by the nurse. At first, I only passed the number(Integer) of a patient to the queue. However, in this way, I can not link the patient with a specific doctor. Then, I stored the patient object into queues. After the patient goes to see a doctor, the setDocNum method in patient thread will connect the patient with a specific doctor. Hence, the information of the doctor and the patient can be shared in different threads and each thread can print their own activities.
  - The second difficulty was I didn’t recognize that there’s only one receptionist. This requirement means only one patient at a time can be registered by the receptionist. Therefore, a semaphore for the receptionist should be introduced to control the registration of patients. 
  - The third difficulty was the print out order. At first, my print outs were all in sequential order without interleaving. After asking the professor about this problem, I figured out that I had put a semaphore in the receptionist thread to wait for one patient until he/she leaves the hospital. Then, the receptionist can register the next patient. This was absolutely wrong, as the receptionist should be available as soon as the receptionist finishes registration of a patient. Hence, I removed the semaphore, which waits until the patient leaves, and the printouts are all interleaved finally. 





