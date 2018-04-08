package project2;

import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * Author: baojianfeng
 * Date: 2018-03-14
 * Description: Patient class, all patient related actions are written in this class
 */
public class Patient implements Runnable {
    private int patientNum, docNum;
    private Semaphore patientReg, register, patientSit;
    private Semaphore nurseCall, patientCome, advices;
    private Semaphore mutexReg;
    private Semaphore receptionistAva;
    private Queue<Patient> regQueue;
    private Semaphore[] finished;

    public Patient(int patientNum, Semaphore patientReg, Semaphore register, Semaphore patientSit,
                   Semaphore nurseCall, Semaphore patientCome, Semaphore advices, Semaphore mutexReg,
                   Semaphore receptionistAva, Queue<Patient> regQueue, Semaphore[] finished) {
        this.patientNum = patientNum;
        this.patientReg = patientReg;
        this.register = register;
        this.patientSit = patientSit;
        this.nurseCall = nurseCall;
        this.patientCome = patientCome;
        this.advices = advices;
        this.mutexReg = mutexReg;
        this.receptionistAva = receptionistAva;
        this.regQueue = regQueue;
        this.finished = finished;
    }

    /**
     * get patient number
     * @return patient number
     */
    public int getPatientNum() {
        return this.patientNum;
    }

    /**
     * set doctor number
     * @param docNum doctor number
     */
    public void setDocNum(int docNum) {
        this.docNum = docNum;
    }

    /**
     * get doctor number
     * @return doctor number
     */
    public int getDocNum() {
        return docNum;
    }

    /**
     * thread run method
     */
    @Override
    public void run() {
        try {
            System.out.println("Patient " + patientNum + " enters waiting room, waits for receptionist");
            Thread.sleep(1000);
            SemUtil.wait(receptionistAva);
            SemUtil.wait(mutexReg);
            regQueue.add(this);
            SemUtil.signal(mutexReg);
            SemUtil.signal(patientReg);

            SemUtil.wait(register);
            System.out.println("Patient " + patientNum + " leaves receptionist and sits in waiting room");
            Thread.sleep(1000);
            SemUtil.signal(patientSit);

            SemUtil.wait(nurseCall);
            System.out.println("Patient " + patientNum + " enters doctor " + this.getDocNum() + "'s office");
            Thread.sleep(1000);
            SemUtil.signal(patientCome);

            SemUtil.wait(advices);
            System.out.println("Patient " + patientNum + " receives advice from doctor " + this.getDocNum());
            Thread.sleep(1000);
            System.out.println("Patient " + patientNum + " leaves");
            Thread.sleep(1000);
            SemUtil.signal(finished[patientNum]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
