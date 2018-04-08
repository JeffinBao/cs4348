package project2;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

/**
 * Author: baojianfeng
 * Date: 2018-03-14
 * Description: Main class to execute doctor-patient process
 */
public class Project2 {
    private static Semaphore patientReg = new Semaphore(0);
    private static Semaphore register = new Semaphore(0);
    private static Semaphore nurseCall = new Semaphore(0);
    private static Semaphore patientSit = new Semaphore(0);
    private static Semaphore patientReady = new Semaphore(0);
    private static Semaphore patientCome = new Semaphore(0);
    private static Semaphore advices = new Semaphore(0);
    private static Semaphore mutexReg = new Semaphore(1);
    private static Semaphore mutexWait = new Semaphore(1);
    private static Queue<Patient> regQueue = new LinkedList<>();
    private static Queue<Patient> waitQueue = new LinkedList<>();
    private static Semaphore receptionistAva = new Semaphore(1);
    private static Semaphore[] finished;

    public static void main(String[] args) {
        int patientCount = 0;
        int docCount = 0;
        Scanner scanner = new Scanner(System.in);
        while (patientCount <= 0 || patientCount > 30) {
            System.out.print("Please input patient quantity: ");
            String patient = scanner.nextLine();
            patientCount = Integer.valueOf(patient);
        }
        while (docCount <= 0 || docCount > 3) {
            System.out.print("Please input doctor quantity: ");
            String doctor = scanner.nextLine();
            docCount = Integer.valueOf(doctor);
        }

        finished = new Semaphore[patientCount];
        for (int i = 0; i < patientCount; i++) {
            finished[i] = new Semaphore(0);
        }
        Semaphore maxDoc = new Semaphore(docCount);

        Patient[] patients = new Patient[patientCount];
        DocAndNurse[] docAndNurses = new DocAndNurse[docCount];

        Thread[] patientThr = new Thread[patientCount];
        Thread[] docThr = new Thread[docCount];
        System.out.println("Run with " + patientCount + " patients, " + docCount + " nurses, " + docCount + " doctors");

        // create threads
        for (int i = 0; i < docCount; i++) {
            docAndNurses[i] = new DocAndNurse(i, maxDoc, patientReady, nurseCall, patientCome,
                    advices, receptionistAva, mutexWait, waitQueue, finished);
            docThr[i] = new Thread(docAndNurses[i]);
            docThr[i].start();
        }
        Receptionist receptionist = new Receptionist(patientReg, register, patientSit, patientReady,
                mutexReg, mutexWait, regQueue, waitQueue);
        Thread receptionThr = new Thread(receptionist);
        receptionThr.start();
        for (int i = 0; i < patientCount; i++) {
            patients[i] = new Patient(i, patientReg, register, patientSit, nurseCall,
                    patientCome, advices, mutexReg, receptionistAva, regQueue, finished);
            patientThr[i] = new Thread(patients[i]);
            patientThr[i].start();
        }

        // release threads
        for (int i = 0; i < patientCount; i++) {
            try {
                patientThr[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.exit(0);

    }
}
