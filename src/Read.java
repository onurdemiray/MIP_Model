import org.apache.poi.xssf.usermodel.XSSFWorkbook;
        import org.apache.poi.xssf.usermodel.XSSFSheet;
        import org.apache.poi.ss.usermodel.Workbook;
        import org.apache.poi.ss.usermodel.Cell;
        import org.apache.poi.ss.usermodel.Row;

        import java.io.FileNotFoundException;

        import java.io.FileInputStream;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.Iterator;
        import java.io.File;


public class Read {

    /*
    This class is responsible for
    reading the problem instance
    given path
     */

    String excelFilePath;
    FileInputStream inputStream;
    Workbook workbook;

    int n_task = 0;  // will be updated later
    int n_team = 0;  // will be updated later
    int n_skill = 0; // will be updated later

    public Read(String path) {

        /*
        Constructor Method

        @param: path, 'path of the xlsx file being read'
         */
        excelFilePath = path;
        try {
            inputStream = new FileInputStream(new File(excelFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (inputStream != null) {
                workbook = new XSSFWorkbook(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read_info() {

        /*
        Reads the number of
        --> Tasks
        --> Teams
        --> Skills
        and then creates tasks
        and teams arrays
         */

        XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0); // which sheet in excel
        Iterator<Row> iterator = sheet.iterator(); // create row iterator
        for (int which_row = 0; iterator.hasNext(); which_row++) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator(); // create cell
            for (int which_col = 0; cellIterator.hasNext(); which_col++) {
                Cell cell = cellIterator.next();
                if (which_col == 1) {

                    switch (which_row) {
                        case 0:
                            n_task = (int) cell.getNumericCellValue();
                            ;
                            break;
                        case 1:
                            n_team = (int) cell.getNumericCellValue();
                            ;
                            break;
                        case 2:
                            n_skill = (int) cell.getNumericCellValue();
                            ;
                            break;
                    }
                }
            }
        }

    }

    public ArrayList<Task> read_tasks() {

        /*
        Read and creates tasks
         */
        ArrayList<Task> tasks = new ArrayList<Task>();

        XSSFSheet sheet = (XSSFSheet) workbook.getSheet("tasks");
        Iterator<Row> iterator = sheet.iterator();
        for (int which_row = 0; iterator.hasNext(); which_row++) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();

            if (which_row > 0) {
                int id = -1;
                int p = -1;
                int w = -1;
                int a = -1;

                for (int which_col = 0; cellIterator.hasNext(); which_col++) {
                    Cell cell = cellIterator.next();
                    if (which_col == 0) {
                        id = (int) cell.getNumericCellValue();
                    } else if (which_col == 1) {
                        p = (int) cell.getNumericCellValue();
                    } else if (which_col == 2) {
                        w = (int) cell.getNumericCellValue();
                    } else {
                        a = (int) cell.getNumericCellValue();
                    }
                }

                Task a_task = new Task(id, p, a, w);
                tasks.add(a_task);
            }
        }

        // now let's read the skill part

        sheet = (XSSFSheet) workbook.getSheet("task-skill");
        iterator = sheet.iterator();
        for (int which_row = 0; iterator.hasNext(); which_row++) {
            Row nextRow = iterator.next();
            if (which_row == 0) {
                continue;
            }
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            int[] current_skill = new int[n_skill];
            for (int which_col = 0; cellIterator.hasNext(); which_col++) {
                Cell cell = cellIterator.next();
                if (which_col == 0) {
                    continue;
                }
                current_skill[which_col - 1] = (int) cell.getNumericCellValue();
            }
            tasks.get(which_row - 1).task_required_skills = current_skill;
        }
        return tasks;
    }

    public ArrayList<Team> read_teams() {

        /*
        Read and creates teams
         */
        ArrayList<Team> teams = new ArrayList<Team>();

        XSSFSheet sheet = (XSSFSheet) workbook.getSheet("crew-skill");
        Iterator<Row> iterator = sheet.iterator();

        for (int which_row = 0; iterator.hasNext(); which_row++) {
            Row nextRow = iterator.next();
            if (which_row == 0) {
                continue;
            }
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            int[] current_skill = new int[n_skill];
            for (int which_col = 0; cellIterator.hasNext(); which_col++) {
                Cell cell = cellIterator.next();
                if (which_col == 0) {
                    continue;
                }
                current_skill[which_col - 1] = (int) cell.getNumericCellValue();
            }
            int teamID = which_row;
            teams.add(new Team(teamID, current_skill));
        }
        return teams;
    }

    public int[][] read_travel_times(){
        int[][] travel_times = new int[n_task][n_task];
        XSSFSheet sheet = (XSSFSheet) workbook.getSheet("travel_time"); // which sheet in excel
        Iterator<Row> iterator = sheet.iterator(); // create row iterator
        for (int i = 0; iterator.hasNext(); i++) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator(); // create cell
            if(i>=1){
                for (int j = 0; cellIterator.hasNext(); j++) {
                    Cell cell = cellIterator.next();
                    if(j>=1){
                        travel_times[i-1][j-1]=(int)cell.getNumericCellValue();
                    }
                }
            }
        }
        return travel_times;
    }
}
