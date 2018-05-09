package project3;

import java.io.*;
import java.util.*;

/**
 * Author: baojianfeng
 * Date: 2018-04-08
 * Description: implementation of 6 scheduling algorithms:
 * 1. First Come First Serve (FCFS)
 * 2. Round Robin (RR)
 * 3. Shortest Process Next (SPN)
 * 4. Shortest Remaining Time (SRT)
 * 5. Highest Response Ratio Next (HRRN)
 * 6. Feedback (FB)
 */
public class ScheduleAlgorithms {
    private int[][] taskTimes;      // arrival and service time array
    private int size;               // total number of tasks
    private List<String> taskNames; // task names

    public ScheduleAlgorithms(Queue<Integer> queue, List<String> taskNames) {
        this.size = queue.size();
        this.taskNames = taskNames;

        initTaskTimes(queue);
    }

    /**
     * initialize the arrival and service time array
     * @param queue the arrival and service time queue
     * @return task time array
     */
    private void initTaskTimes(Queue<Integer> queue) {
        taskTimes = new int[size / 2][2];

        for (int i = 0; i < size / 2; i++) {
            taskTimes[i][0] = queue.poll();
            taskTimes[i][1] = queue.poll();
        }

    }

    /**
     * First Come First Serve algorithm
     * return a scheduling time intervals map related to each task
     * @return map
     */
    public Map<String, Queue<Integer>> firstComeFirstServe() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>(); // store task scheduling time intervals

        int curPos = taskTimes[0][0];
        for (int i = 0; i < taskTimes.length; i++) {
            String taskName = taskNames.get(i);
            Queue<Integer> timeIntervals = new LinkedList<>();
            timeIntervals.add(curPos);
            timeIntervals.add(curPos + taskTimes[i][1]);
            resultMap.put(taskName, timeIntervals);

            if (i != taskTimes.length - 1)
                // compare the current task's finish time with the next task's arrival time
                // if next task's arrival time is larger, update curPos to the next task's arrival time
                // otherwise, update curPos to the current task's finish time
                curPos = taskTimes[i + 1][0] > curPos + taskTimes[i][1] ? taskTimes[i + 1][0] : curPos + taskTimes[i][1];
        }

        return resultMap;
    }

    /**
     * Round Robin algorithm
     * return a scheduling time intervals map related to each task
     * here the quantum is 1
     * @return map
     */
    public Map<String, Queue<Integer>> roundRobin() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>();
        List<Integer> arrivalTimeList = new ArrayList<>();
        List<Integer> remainTimeList = new ArrayList<>();
        for (int i = 0; i < taskNames.size(); i++) {
            arrivalTimeList.add(taskTimes[i][0]);
            remainTimeList.add(taskTimes[i][1]);
        }

        String selectedTask = taskNames.get(0);
        int selectedTaskIdx = 0;
        int selectedTaskStartT = taskTimes[0][0];
        int time = taskTimes[0][0]; // time starts from the first task's arrival time
        int taskFinished = 0;

        // add the first task into task queue
        Queue<String> taskQueue = new LinkedList<>();
        taskQueue.add(selectedTask);
        int idx = selectedTaskIdx;
        // in case there are multiple tasks arrive at the same time
        while (true) {
            idx++;
            if (idx < taskNames.size() && arrivalTimeList.get(idx).equals(time)) {
                String task1 = taskNames.get(idx);
                taskQueue.add(task1);
            } else {
                break;
            }
        }

        while (true) {
            String task = taskQueue.poll();
            if (task == null) { // this is when the previous task finished, but the next task doesn't arrive
                time++;
                if (arrivalTimeList.contains(time)) {
                    int index = arrivalTimeList.indexOf(time);
                    String nextTask = taskNames.get(index);
                    taskQueue.add(nextTask);  // first add the new arrival task, then add the current task back and the task should not be completed
                    // in case there are multiple tasks arrive at the same time
                    while (true) {
                        index++;
                        if (index < taskNames.size() && arrivalTimeList.get(index).equals(time)) {
                            String task1 = taskNames.get(index);
                            taskQueue.add(task1);
                        } else {
                            break;
                        }
                    }
                }
            } else {
                if (!task.equals(selectedTask)) {
                    if (remainTimeList.get(selectedTaskIdx) != 0) { // only not finished task's status will be saved here
                        Queue<Integer> queue = resultMap.get(selectedTask);
                        if (queue == null)
                            queue = new LinkedList<>();

                        // save previous task's time intervals
                        queue.add(selectedTaskStartT);
                        queue.add(time);
                        resultMap.put(selectedTask, queue);
                    }

                    // update new selected task information
                    selectedTask = task;
                    selectedTaskIdx = taskNames.indexOf(selectedTask);
                    selectedTaskStartT = time;
                }

                // update remaining time list
                int remainT = remainTimeList.get(selectedTaskIdx);
                remainTimeList.set(selectedTaskIdx, remainT - 1);
                time++; // time moves forward

                if (arrivalTimeList.contains(time)) {
                    int index = arrivalTimeList.indexOf(time);
                    String nextTask = taskNames.get(index);
                    taskQueue.add(nextTask);  // first add the new arrival task, then add the current task back and the task should not be completed
                    // in case there are multiple tasks arrive at the same time
                    while (true) {
                        index++;
                        if (index < taskNames.size() && arrivalTimeList.get(index).equals(time)) {
                            String task1 = taskNames.get(index);
                            taskQueue.add(task1);
                        } else {
                            break;
                        }
                    }
                }

                if (remainT - 1 == 0) {
                    // finished task's status will be saved here
                    Queue<Integer> queue = resultMap.get(selectedTask);
                    if (queue == null)
                        queue = new LinkedList<>();

                    queue.add(selectedTaskStartT);
                    queue.add(time);
                    resultMap.put(selectedTask, queue);

                    taskFinished++;
                    if (taskFinished == taskNames.size())
                        break;
                } else {
                    taskQueue.add(selectedTask); // if the task doesn't complete, add back to the queue
                }
            }

        }

        return resultMap;
    }

    /**
     * Shortest Process Next algorithm (Non-Preemptive)
     * return a scheduling time intervals map related to each task
     * @return map
     */
    public Map<String, Queue<Integer>> shortestProcessNext() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>(); // store task scheduling time intervals
        boolean[] taskFinArr = new boolean[taskNames.size()];

        Queue<Integer> taskAQueue = new LinkedList<>();
        taskAQueue.add(taskTimes[0][0]);
        taskAQueue.add(taskTimes[0][0] + taskTimes[0][1]);
        resultMap.put(taskNames.get(0), taskAQueue);
        int curPos = taskTimes[0][0] + taskTimes[0][1];
        taskFinArr[0] = true;
        int taskFinished = 1;
        while (taskFinished < taskNames.size()) {
            String selectedTask = chooseTaskSPN(taskFinArr, curPos);
            int index = taskNames.indexOf(selectedTask);
            if (taskTimes[index][0] > curPos)
                curPos = taskTimes[index][0];
            Queue<Integer> taskQueue = new LinkedList<>();
            taskQueue.add(curPos);
            taskQueue.add(curPos + taskTimes[index][1]);
            resultMap.put(selectedTask, taskQueue);
            curPos += taskTimes[index][1];
            taskFinArr[index] = true;
            taskFinished++;
        }

        return resultMap;
    }

    /**
     * choose the next task according to shortest process time algorithm
     * @param taskFinArr task finished boolean array
     * @param curPos current time stamp position
     * @return selected task
     */
    private String chooseTaskSPN(boolean[] taskFinArr, int curPos) {
        String selectedTask = "";
        int shortestServiceTime = Integer.MAX_VALUE;

        for (int i = 0; i < taskFinArr.length; i++) {
            if (!taskFinArr[i] && curPos >= taskTimes[i][0]) {
                if (taskTimes[i][1] < shortestServiceTime) {
                    shortestServiceTime = taskTimes[i][1];
                    selectedTask = taskNames.get(i);
                }
            } else if (!taskFinArr[i] && curPos < taskTimes[i][0]) {
                if (selectedTask.isEmpty())
                    selectedTask = taskNames.get(i);
                break;
            }
        }

        return selectedTask;
    }

    /**
     * Shortest Remaining Time algorithm (Preemptive)
     * return a scheduling time intervals map related to each task
     * @return map
     */
    public Map<String, Queue<Integer>> shortestRemainingTime() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>();

        List<Integer> arrivalTimeList = new ArrayList<>();
        List<Integer> remainTimeList = new ArrayList<>();
        for (int i = 0; i < taskTimes.length; i++) {
            arrivalTimeList.add(taskTimes[i][0]);
            remainTimeList.add(taskTimes[i][1]);
        }

        String selectedTask = taskNames.get(0);
        int selectedTaskIdx = 0;
        int selectedTaskStartT = taskTimes[0][0];
        int time = taskTimes[0][0];
        int taskFinished = 0;
        while (true) {
            if (arrivalTimeList.contains(time)) {
                int newIndex = chooseTaskSRT(remainTimeList, time);
                if (selectedTaskIdx == -1) {
                    // update selected task info: the new task has finally come
                    selectedTaskIdx = newIndex;
                    selectedTask = taskNames.get(selectedTaskIdx);
                    selectedTaskStartT = time;
                } else if (newIndex != selectedTaskIdx) {
                    Queue<Integer> queue = resultMap.get(selectedTask);
                    if (queue == null)
                        queue = new LinkedList<>();

                    // save previous task's status
                    queue.add(selectedTaskStartT);
                    queue.add(time);
                    resultMap.put(selectedTask, queue);

                    // update selected task info
                    selectedTaskIdx = newIndex;
                    selectedTask = taskNames.get(selectedTaskIdx);
                    selectedTaskStartT = time;
                }
            }

            time++; // time moves one forward

            if (selectedTaskIdx == -1) // TODO write into report, need to consider the scenario when previous task finished, but later task doesn't arrive yet.
                continue;

            int remainT = remainTimeList.get(selectedTaskIdx);
            remainTimeList.set(selectedTaskIdx, remainT - 1);
            if (remainT - 1 == 0) {
                // finished task's status will be saved here
                Queue<Integer> queue = resultMap.get(selectedTask);
                if (queue == null) {
                    queue = new LinkedList<>();
                }

                // save previous task's status
                queue.add(selectedTaskStartT);
                queue.add(time);
                resultMap.put(selectedTask, queue);

                taskFinished++;
                // if all task finished, jump out of while loop
                if (taskFinished == taskNames.size())
                    break;

                selectedTaskIdx = chooseTaskSRT(remainTimeList, time);
                if (selectedTaskIdx != -1) {
                    selectedTask = taskNames.get(selectedTaskIdx);
                    selectedTaskStartT = time;
                }
            }
        }

        return resultMap;
    }

    /**
     * get the next selected task's index
     * @param remainingTimeList remaining time list
     * @param curTime current time
     * @return index
     */
    private int chooseTaskSRT(List<Integer> remainingTimeList, int curTime) {
        int shortestRemainTime = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < remainingTimeList.size(); i++) {
            int remainT = remainingTimeList.get(i);
            if (remainT > 0 && remainT < shortestRemainTime && curTime >= taskTimes[i][0]) {
                shortestRemainTime = remainT;
                index = i;
            }
        }

        return index;
    }

    /**
     * Highest Response Ratio Next algorithm (Non-Preemptive)
     * return a scheduling time intervals map related to each task
     * @return map
     */
    public Map<String, Queue<Integer>> highestResponseRatioNext() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>();  // store task scheduling time intervals
        boolean[] taskFinArr = new boolean[taskNames.size()];

        Queue<Integer> taskAQueue = new LinkedList<>();
        taskAQueue.add(taskTimes[0][0]);
        taskAQueue.add(taskTimes[0][0] + taskTimes[0][1]);
        resultMap.put(taskNames.get(0), taskAQueue);
        taskFinArr[0] = true;
        int curPos = taskTimes[0][0] + taskTimes[0][1];
        int taskFinished = 1; // the first task is finished
        while (taskFinished < taskNames.size()) {
            String selectedTask = chooseTaskHRRN(taskFinArr, curPos);
            int index = taskNames.indexOf(selectedTask);
            if (taskTimes[index][0] > curPos)
                curPos = taskTimes[index][0];
            Queue<Integer> taskQueue = new LinkedList<>();
            taskQueue.add(curPos);
            taskQueue.add(curPos + taskTimes[index][1]);
            resultMap.put(selectedTask, taskQueue);
            taskFinArr[index] = true;
            curPos += taskTimes[index][1]; // update current time stamp position
            taskFinished++; // one more task is finished
        }

        return resultMap;
    }

    /**
     * get the next task name
     * @param taskFinArr finished task boolean array
     * @param curPos current time stamp position
     * @return selected task name
     */
    private String chooseTaskHRRN(boolean[] taskFinArr, int curPos) {
        String selectedTask = "";
        double highestRatio = Double.MIN_VALUE;
        for (int i = 0 ; i < taskFinArr.length; i++) {
            // if the task has not been executed and it already arrived, then calculate the ratio
            if (!taskFinArr[i] && curPos >= taskTimes[i][0]) {
                double ratio = (curPos - taskTimes[i][0] + taskTimes[i][1]) / (double) taskTimes[i][1];
                if (ratio > highestRatio) {
                    highestRatio = ratio;
                    selectedTask = taskNames.get(i);
                }
            } else if (!taskFinArr[i] && curPos < taskTimes[i][0]) {
                if (selectedTask.isEmpty())
                    selectedTask = taskNames.get(i);
                break;
            }
        }

        return selectedTask;
    }

    /**
     * Feedback algorithm
     * return a scheduling time intervals map related to each task
     * @return map
     */
    public Map<String, Queue<Integer>> feedback() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>();
        Map<Integer, Queue<String>> priorityTaskQueueMap = new HashMap<>();
        Queue<String> priorTaskQueue1 = new LinkedList<>();
        Queue<String> priorTaskQueue2 = new LinkedList<>();
        Queue<String> priorTaskQueue3 = new LinkedList<>();
        priorityTaskQueueMap.put(1, priorTaskQueue1);
        priorityTaskQueueMap.put(2, priorTaskQueue2);
        priorityTaskQueueMap.put(3, priorTaskQueue3);
        Map<String, Integer> taskPriorityLevel = new HashMap<>();

        List<Integer> arrivalTimeList = new ArrayList<>();
        List<Integer> remainTimeList = new ArrayList<>();
        for (int i = 0; i < taskNames.size(); i++) {
            arrivalTimeList.add(taskTimes[i][0]);
            remainTimeList.add(taskTimes[i][1]);
            taskPriorityLevel.put(taskNames.get(i), 1); // initialize task priority level map
        }

        int time = taskTimes[0][0];
        String selectedTask = taskNames.get(0);
        int selectedTaskIdx = 0;
        int selectedTaskStartT = taskTimes[0][0];
        int taskFinished = 0;

        while (true) {
            if (arrivalTimeList.contains(time)) {
                int index = arrivalTimeList.indexOf(time);
                String task = taskNames.get(index);
                priorTaskQueue1.add(task);
                // TODO write into summary
                // in case there are multiple tasks arrive at the same time
                while (true) {
                    index++;
                    if (index < taskNames.size() && arrivalTimeList.get(index).equals(time)) {
                        String task1 = taskNames.get(index);
                        priorTaskQueue1.add(task1);
                    } else
                        break;
                }
            }

            String newTask = "";
            if (!priorTaskQueue1.isEmpty()) {
                newTask = priorTaskQueue1.poll();
            } else if (!priorTaskQueue2.isEmpty()) {
                newTask = priorTaskQueue2.poll();
            } else if (!priorTaskQueue3.isEmpty()) {
                newTask = priorTaskQueue3.poll();
            }

            if (!newTask.isEmpty() && !newTask.equals(selectedTask)) {
                // TODO write into summary: every time the selected task is preempted, move the selected to the next level queue
                if (remainTimeList.get(selectedTaskIdx) > 0) {
                    Queue<Integer> queue = resultMap.get(selectedTask);
                    if (queue == null)
                        queue = new LinkedList<>();

                    // save unfinished task's status information
                    queue.add(selectedTaskStartT);
                    queue.add(time);
                    resultMap.put(selectedTask, queue);

                    int taskLevel = taskPriorityLevel.get(selectedTask);
                    if (taskLevel < 3) {
                        taskLevel++;
                        taskPriorityLevel.put(selectedTask, taskLevel);
                    }
                    Queue<String> taskQueue = priorityTaskQueueMap.get(taskLevel);
                    taskQueue.add(selectedTask);
                }

                selectedTask = newTask;
                selectedTaskIdx = taskNames.indexOf(selectedTask);
                selectedTaskStartT = time;

            }

            int remainT = remainTimeList.get(selectedTaskIdx);
            time++;
            if (remainT == 0) // if current task has finished, but next task doesn't come, continue the next loop
                continue;

            remainTimeList.set(selectedTaskIdx, remainT - 1);
            if (remainT - 1 == 0) {
                Queue<Integer> queue = resultMap.get(selectedTask);
                if (queue == null)
                    queue = new LinkedList<>();

                // save finished task's status information
                queue.add(selectedTaskStartT);
                queue.add(time);
                resultMap.put(selectedTask, queue);

                taskFinished++;
                if (taskFinished == taskNames.size())
                    break;
            }
        }

        return resultMap;
    }

    public static void main(String[] args) {
        String filePath = args[0];
        String command = args[1]; // command can be FCFS/RR/SPN/SRT/HRRN/FB/ALL

        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            List<String> taskNames = new ArrayList<>();
            Queue<Integer> queue = new LinkedList<>();
            String line = br.readLine();
            while (line != null) {
                String[] split = line.split("\\t");
                taskNames.add(split[0]);
                // TODO consider start time and service time will be float type???
                int arrivalTime = Integer.valueOf(split[1]);
                int serviceTime = Integer.valueOf(split[2]);
                queue.add(arrivalTime);
                queue.add(serviceTime);

                line = br.readLine();
            }

            ScheduleAlgorithms sa = new ScheduleAlgorithms(queue, taskNames);
            Map<String, Map<String, Queue<Integer>>> result = new HashMap<>();
            switch (command) {
                case "FCFS": {
                    Map<String, Queue<Integer>> resultMap = sa.firstComeFirstServe();
                    result.put("FCFS", resultMap);
                    break;
                }
                case "RR": {
                    Map<String, Queue<Integer>> resultMap = sa.roundRobin();
                    result.put("RR", resultMap);
                    break;
                }
                case "SPN": {
                    Map<String, Queue<Integer>> resultMap = sa.shortestProcessNext();
                    result.put("SPN", resultMap);
                    break;
                }
                case "SRT": {
                    Map<String, Queue<Integer>> resultMap = sa.shortestRemainingTime();
                    result.put("SRT", resultMap);
                    break;
                }
                case "HRRN": {
                    Map<String, Queue<Integer>> resultMap = sa.highestResponseRatioNext();
                    result.put("HRRN", resultMap);
                    break;
                }
                case "FB": {
                    Map<String, Queue<Integer>> resultMap = sa.feedback();
                    result.put("FB", resultMap);
                    break;
                }
                case "ALL": {
                    Map<String, Queue<Integer>> resultMapFCFS = sa.firstComeFirstServe();
                    result.put("FCFS", resultMapFCFS);
                    Map<String, Queue<Integer>> resultMapRR = sa.roundRobin();
                    result.put("RR", resultMapRR);
                    Map<String, Queue<Integer>> resultMapsSPN = sa.shortestProcessNext();
                    result.put("SPN", resultMapsSPN);
                    Map<String, Queue<Integer>> resultMapSRT = sa.shortestRemainingTime();
                    result.put("SRT", resultMapSRT);
                    Map<String, Queue<Integer>> resultMapHRRN = sa.highestResponseRatioNext();
                    result.put("HRRN", resultMapHRRN);
                    Map<String, Queue<Integer>> resultMapFB = sa.feedback();
                    result.put("FB", resultMapFB);
                    break;
                }
            }

            Set<String> keySet = result.keySet();
            for (String algo : keySet) {
                System.out.println("The scheduling of " + algo + " is:");
                Map<String, Queue<Integer>> scheduleMap = result.get(algo);

                for (int i = 0; i < scheduleMap.size(); i++) {
                    String taskName = taskNames.get(i);
                    System.out.print(taskName + " ");
                    Queue<Integer> timeIntervals = scheduleMap.get(taskName);
                    int prevTimeStamp = 0;
                    while (!timeIntervals.isEmpty()) {
                        int start = timeIntervals.poll();
                        int end = timeIntervals.poll();

                        // print spaces, mean the task is not executed
                        for (int j = 0; j < start - prevTimeStamp; j++)
                            System.out.print(" ");
                        // print X, means the task executed
                        for (int j = 0; j < end - start; j++)
                            System.out.print("X");
                        prevTimeStamp = end; // TODO previous forgot to update prevTimeStamp, write into summary bug 1
                    }
                    System.out.println();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
