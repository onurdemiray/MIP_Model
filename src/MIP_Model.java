import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;

import static java.lang.System.out;

/*
Defines Mixed Integer Programming
Model
 */


public class MIP_Model {

    // ranges

    int n_task;
    int n_team;
    int n_skill;
    int n_node;

    IloCplex cplex;

    // decision variables

    IloNumVar[][][] X; // whether j is visited immediately after i by team k
    IloNumVar[][] Y;  // whether specific node is assigned to particular crew or not
    IloNumVar[][] S;  // continuous variable indicating arrival time of the corresponding team to the task
    IloNumVar[] B;  // continuous variable indicating the starting time of the corresponding task
    IloNumVar[] O;  // binary variable indicating whether corresponding task is outsourced or not

    public MIP_Model(int n_task, int n_team, int n_skill) throws IloException {

        this.n_task = n_task;
        this.n_team = n_team;
        this.n_skill = n_skill;
        this.n_node = n_task + 1;  // 1 is for central depot

        try {
            cplex = new IloCplex();
        } catch (IloException e) {
            e.printStackTrace();
        }

        X = new IloNumVar[n_node][n_node][n_team]; // n_nodes x n_nodes x n_team
        Y = new IloNumVar[n_node][n_team]; // n_nodes x n_team
        S = new IloNumVar[n_node][n_team]; // n_nodes x n_team
        B = new IloNumVar[n_node]; // 0 will be excluded for this variable
        O = new IloNumVar[n_node]; // 0 will be excluded for this variable

        for (int i = 0; i < n_node; i++) {
            for (int j = 0; j < n_node; j++) {
                for (int k = 0; k < n_team; k++) {
                    X[i][j][k] = cplex.numVar(0, 1, IloNumVarType.Bool, "X" + "_" + i + "_" + j + "_" + k);
                }
            }
        }

        for (int i = 0; i < n_node; i++) {
            for (int k = 0; k < n_team; k++) {
                Y[i][k] = cplex.numVar(0, 1, IloNumVarType.Bool, "Y" + "_" + i + "_" + k);
            }
        }

        for (int i = 0; i < n_node; i++) {
            for (int k = 0; k < n_team; k++) {
                S[i][k] = cplex.numVar(0, 540, IloNumVarType.Float, "S" + "_" + i + "_" + k);
            }
        }

        for (int i = 0; i < n_node; i++) {
            B[i] = cplex.numVar(0, 540, IloNumVarType.Float, "B" + "_" + i);
        }

        for (int i = 0; i < n_node; i++) {
            O[i] = cplex.numVar(0, 1, IloNumVarType.Bool, "O" + "_" + i);
        }

    }

    public void set_objective(int[] priority) throws IloException {
        IloLinearNumExpr obj = cplex.linearNumExpr(); // sum w_i * B_i where w represents priority
        for (int i = 0; i < n_node; i++) {
            if (i > 0) {
                obj.addTerm(priority[i], B[i]);
            }
        }
        cplex.addMinimize(obj);
    }

    public void assignment_constraint() throws IloException {

        /*
        (1) O_i + sum(k) Y_ik >= 1, for each i in {1..N}

        (2) sum(k) Y_ik <= |K|(1-O_i) for each i in {1..N}
         */
        for (int i = 0; i < n_node; i++) {
            if (i > 0) {
                IloLinearNumExpr sum_y = cplex.linearNumExpr();  // sum y_ik over k
                for (int k = 0; k < n_team; k++) {
                    sum_y.addTerm(1, Y[i][k]);
                }

                cplex.addGe(cplex.sum(O[i], sum_y), 1); // (1)

                cplex.addLe(sum_y, cplex.prod(n_team, cplex.sum(1, cplex.prod(-1, O[i]))));  // (2)
            }
        }
    }

    public void all_teams_to_depot() throws IloException {
        /*
        (3) Y_0k = 1, for each k in {1..K}
         */
        for (int k = 0; k < n_team; k++) {
            cplex.addEq(Y[0][k], 1);
        }
    }

    public void X_Y_relation() throws IloException {
        /*
        (4) sum(j in N':i!=j) X_ijk = Y_ik, for each i in N, for each k in K
         */

        for (int i = 0; i < n_node; i++) {
            if (i > 0) {
                for (int k = 0; k < n_team; k++) {
                    IloLinearNumExpr sum_x = cplex.linearNumExpr();
                    for (int j = 0; j < n_node; j++) {
                        if (j != i) {
                            sum_x.addTerm(X[i][j][k], 1);
                        }
                    }
                    cplex.addEq(sum_x, Y[i][k]);  // (4)
                }
            }
        }
    }

    public void team_starts(int[] starting_nodes) throws IloException {

        /*
        (5) sum(j in N': j!=sn_k) x_(sn_k)jk = 1 for each k in K where K is sn_k represents the starting node of team k
         */

        for (int k = 0; k < n_team; k++) {
            IloLinearNumExpr sum_x = cplex.linearNumExpr();
            for (int j = 0; j < n_node; j++) {
                if (j != starting_nodes[k]) {
                    sum_x.addTerm(1, X[starting_nodes[k]][j][k]); // (5)
                }
            }
            cplex.addEq(sum_x, 1);
        }
    }

    public void team_finishes() throws IloException {

        /*
        sum(i in N)X_i0k = 1, for each k in K
         */

        for (int k = 0; k < n_team; k++) {
            IloLinearNumExpr sum_x = cplex.linearNumExpr();
            for (int i = 1; i < n_node; i++) {
                sum_x.addTerm(1, X[i][0][k]);
            }
            cplex.addEq(sum_x, 1);  // (6)
        }
    }

    public void flow_balance(int[] starting_nodes) throws IloException {

        /*
        (7) sum(i in N:i!=j) X_ijk - sum(h in N':h!=j) X_jhk = 0, for each k in K, j in N-{sn_k}
         */
        for (int k = 0; k < n_team; k++) {
            for (int j = 1; j < n_node; j++) {
                if (j != starting_nodes[k]) {
                    IloLinearNumExpr inflow = cplex.linearNumExpr();
                    IloLinearNumExpr outflow = cplex.linearNumExpr();
                    for (int i = 1; i < n_node; i++) {  // exclude depot node
                        if (i != j) {
                            inflow.addTerm(1, X[i][j][k]);
                        }
                    }
                    for (int h = 0; h < n_node; h++) {   // do not exclude depot node
                        if (h != j) {
                            outflow.addTerm(1, X[j][h][k]);
                        }
                    }
                    cplex.addEq(inflow, outflow); // (7)
                }
            }
        }

    }

    public void run(int[] priority, int[] process_time, int[] arrival_time, int[] starting_nodes) throws IloException {
        set_objective(priority);
        assignment_constraint(); // (1)-(2)
        all_teams_to_depot();  // (3)
        X_Y_relation(); // (4)
        team_starts(starting_nodes); // (5)
        team_finishes(); // (6)
        flow_balance(starting_nodes);  // (7)
        if (cplex.solve()) {
            out.println(cplex.getObjValue());
        }else{
            out.print("Sorry, infeasible solution");
        }
    }

}
