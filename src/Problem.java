import java.util.StringJoiner;
import java.util.ArrayList;

import static java.lang.System.out;

public class Problem {

    // todo: read, get and display team code

    Read _in_;

    int n_task;
    int n_team;
    int n_skill;

    ArrayList<Task> tasks;

    public Problem(String file) {

        _in_ = new Read(file);
        _in_.read_info();
        n_task = _in_.n_task;
        n_team = _in_.n_team;
        n_skill = _in_.n_skill;
        tasks = _in_.read_tasks();
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void display_tasks() {

        for (int i = 0; i < n_task; i++) {
            Task t = tasks.get(i);
            StringJoiner joiner = new StringJoiner("");
            for (int q = 0; q < n_skill; q++) {
                String temp = String.valueOf(t.task_required_skills[q]);
                if (q < n_skill - 1) {
                    joiner.add(temp + "-");
                } else {
                    joiner.add(temp);
                }
            }
            String skills = joiner.toString();
            out.print("Task ID: " + t.taskID + "  Process Time: " + t.process_time + "  Arrival Time: " + t.arrival_time
                    + "  Priority: " + t.priority + "  Skills: " + skills);
            out.println();
        }
    }

}
