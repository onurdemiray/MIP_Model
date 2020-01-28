import ilog.concert.IloException;

import java.util.ArrayList;

public class _Test {

    public static void main (String args[]) throws IloException {

        Problem prob = new Problem("test_data.xlsx");
        int n_task = prob.n_task;
        int n_team = prob.n_team;
        int n_skill = prob.n_skill;
        ArrayList<Task> tasks = prob.tasks;
        ArrayList<Team> teams = prob.teams;
        int[][] travel_times = prob.travel_times;



        int n_node = n_task + 1;
        int[] priority = new int[n_node];
        int[] process_time = new int[n_node];
        int[] arrival_time = new int[n_node];
        int[] starting_nodes = {1, 2, 3, 4};
        priority[0] = 0;
        process_time[0] = 0;
        arrival_time[0] = 0;
        for(int i=0;i<tasks.size();i++){
            Task t = tasks.get(i);
            priority[i+1] = t.priority;
            process_time[i+1] = t.process_time;
            arrival_time[i+1] = t.arrival_time;
        }

        MIP_Model mip = new MIP_Model(n_task, n_team, n_skill);
        mip.run(priority, process_time, arrival_time, starting_nodes);
        mip.cplex.exportModel("model.lp");

    }
}