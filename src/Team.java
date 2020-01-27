import org.apache.poi.xssf.usermodel.XSSFSheet;

public class Team {

    /*
    Defines Team class
     */

    int teamID;
    int[] team_skill_cap;

    public Team(int teamID, int[] team_skill_cap){

        /*
        Constructor method

        @param: teamID, 'ID of the team'
        @param: team_skill_cap, 'arrival time of the task'

        Example:
        --------
        Let's say there are 5 different skill levels.
        If our team is capable of achieving just the
        first and second ones, then
        team_skill_cap = [1, 1, 0, 0, 0]
         */

        this.teamID = teamID;
        this.team_skill_cap = team_skill_cap;
    }
}
