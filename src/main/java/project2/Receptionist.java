package project2;

import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * Author: baojianfeng
 * Date: 2018-03-14
 * Description: Receptionist class, all receptionist related actions are in this class
 */
public class Receptionist implements Runnable {
    private Semaphore patientReg, register, patientSit, patientReady;
    private Queue<Patient> regQueue, waitQueue;
    private Semaphore mutexReg, mutexWait;

    public Receptionist(Semaphore patientReg, Semaphore register, Semaphore patientSit,
                        Semaphore patientReady, Semaphore mutexReg, Semaphore mutexWait,
                        Queue<Patient> regQueue, Queue<Patient> waitQueue) {
        this.patientReg = patientReg;
        this.register = register;
        this.patientSit = patientSit;
        this.patientReady = patientReady;
        this.mutexReg = mutexReg;
        this.mutexWait = mutexWait;
        this.regQueue = regQueue;
        this.waitQueue = waitQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                SemUtil.wait(patientReg);
                SemUtil.wait(mutexReg);
                Patient patient = regQueue.poll();
                int patientNum = patient.getPatientNum();
                System.out.println("Receptionist registers patient " + patientNum);
                Thread.sleep(1000);
                SemUtil.signal(mutexReg);
                SemUtil.signal(register);
                SemUtil.wait(patientSit);

                SemUtil.wait(mutexWait);
                waitQueue.add(patient);
                SemUtil.signal(mutexWait);
                SemUtil.signal(patientReady);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
