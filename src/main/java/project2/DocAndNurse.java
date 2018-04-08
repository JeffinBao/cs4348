package project2;

import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * Author: baojianfeng
 * Date: 2018-03-14
 * Description: Doctor and nurse class, all doctor and nurse related actions are in this class
 */
public class DocAndNurse implements Runnable {
    private int docNum;
    private Semaphore maxDoc, patientReady, nurseCall, patientCome, advices, receptionistAva;
    private Semaphore mutexWait;// TODO difference between new Semaphore(0, true) and new Semaphore(0)???. Answer: In this project, there's no difference
    private Queue<Patient> waitQueue;
    private Semaphore[] finished;

    public DocAndNurse(int docNum, Semaphore maxDoc, Semaphore patientReady,
                       Semaphore nurseCall, Semaphore patientCome, Semaphore advices, Semaphore receptionistAva,
                       Semaphore mutexWait, Queue<Patient> waitQueue, Semaphore[] finished) {
        this.docNum = docNum;
        this.maxDoc = maxDoc;
        this.patientReady = patientReady;
        this.nurseCall = nurseCall;
        this.patientCome = patientCome;
        this.advices = advices;
        this.receptionistAva = receptionistAva;
        this.mutexWait = mutexWait;
        this.waitQueue = waitQueue;
        this.finished = finished;
    }

    @Override
    public void run() {
        while (true) {
            try {
                SemUtil.wait(patientReady);
                SemUtil.wait(maxDoc);
                SemUtil.signal(receptionistAva);

                SemUtil.wait(mutexWait);
                Patient patient = waitQueue.poll();
                int patientNum = patient.getPatientNum();
                patient.setDocNum(docNum);
                System.out.println("Nurse " + docNum + " takes patient " + patientNum + " to doctor's office");
                Thread.sleep(1000);
                SemUtil.signal(mutexWait);

                SemUtil.signal(nurseCall);
                SemUtil.wait(patientCome);
                System.out.println("Doctor " + docNum + " listens to symptoms from patient " + patientNum);
                Thread.sleep(1000);
                SemUtil.signal(advices);

                SemUtil.wait(finished[patientNum]);
                SemUtil.signal(maxDoc);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
