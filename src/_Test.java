import java.util.ArrayList;

public class _Test {

    public static void main (String args[]) {

        Problem prob = new Problem("test_data.xlsx");
        int n_task = prob.n_task;
        int n_team = prob.n_team;
        int n_skill = prob.n_skill;
        ArrayList<Task> tasks = prob.tasks;
        ArrayList<Team> teams = prob.teams;
        int[][] travel_times = prob.travel_times;
    }
}