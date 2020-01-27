public class Task {

    /*
    Defines Task class
     */

    int taskID;
    int arrival_time;
    int process_time;
    int priority;
    int[] task_required_skills;

    public Task(int taskID, int process_time, int arrival_time, int priority, int[] task_required_skills){

        /*
        Constructor method

        @param: taskID, 'ID of the task'
        @param: process_time, 'process time of the task'
        @param: arrival_time, 'arrival time of the task'
        @param: priority, 'priority or weight of the task'
        @param: task_required_skills, 'required skills to complete the task'

        Example:
        -------
        Let's say there are 5 different skill levels.
        Furthermore, assume that our task requires the
        third and fourth skills in order to be completed.
        Then,
        task_required_skills = [0, 0, 1, 1, 0]
         */

        this.taskID = taskID;
        this.arrival_time = arrival_time;
        this.process_time = process_time;
        this.priority = priority;
        this.task_required_skills = task_required_skills;
    }

    public Task(int taskID, int process_time, int arrival_time, int priority){

        /*
        Another Constructor method
        without skills

         */

        this.taskID = taskID;
        this.arrival_time = arrival_time;
        this.process_time = process_time;
        this.priority = priority;
    }

}
