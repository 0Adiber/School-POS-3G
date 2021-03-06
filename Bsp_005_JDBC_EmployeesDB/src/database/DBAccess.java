package database;

import beans.Department;
import beans.DepartmentManager;
import beans.Employee;
import beans.GehaltHistory;
import beans.HirePeriod;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBAccess {
    
    private static DBAccess instance = null;
    private Database db;
    
    private ResultSet empress = null;
        
    public static DBAccess getInstance() throws ClassNotFoundException, SQLException {
        if(instance == null)
            instance = new DBAccess();
        return instance;
    }
    
    public void connect() throws SQLException {
        db.connect();
    }
    
    public void disconnect() throws SQLException {
        db.disconnect();
    }
    
    public boolean isConnected() throws SQLException {
        return db.isConnected();
    }
    
    private DBAccess() throws ClassNotFoundException, SQLException {
        db = new Database();
        connect();
    }

    public List<Department> getAllDepartments() throws SQLException {      
        List<Department> d = new ArrayList<>();
        String query = SQLStrings.GETALLDEPT;
        Statement stat = db.getStatement();
        ResultSet rs = stat.executeQuery(query);
        while(rs.next()) {
            d.add(new Department(rs));
        }
        db.releaseStatement(stat);
        
        return d;
    }
        
    public Employee getScrollEmployee() throws SQLException {
        if(empress == null || empress.isClosed())
            return null;
        if(empress.next())
            return new Employee(empress);
        return null;
    }
    
    public List<Employee> getAllEmployeesBy(String deptNo, String date, String ...gender) throws SQLException {
        List<Employee> emps = new ArrayList<>();
        
        String query = SQLStrings.GETALLEMPBY.replace("(department)", "'"+deptNo+"'").replace("(gender1)", "'"+gender[0]+"'").replace("(gender2)", "'"+gender[1]+"'").replace("(birth)", "'"+date+"'");
                
        Statement stat = db.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        
        if(empress != null)
            empress.close();
        
        empress = stat.executeQuery(query);
        
        int count = 0;
        while(empress.next() && count++<50) {
            emps.add(new Employee(empress));
        }
        
        return emps;    
    }
    
    public List<DepartmentManager> getDeptMan(String deptNo) throws SQLException {
        List<DepartmentManager> mans = new ArrayList<>();
        
        String query = SQLStrings.GETDEPTMAN.replace("(department)", "'" + deptNo + "'");
        
        Statement stat = db.getStatement();
        ResultSet rs = stat.executeQuery(query);
        while(rs.next()) {
            mans.add(new DepartmentManager(rs));
        }
        db.releaseStatement(stat);
        return mans;
    }
    
    public List<GehaltHistory> getHistoryForEmp(int empno) throws SQLException {
        List<GehaltHistory> history = new ArrayList<>();
        
        String query = SQLStrings.GETSAlFOREMP.replace("(employee)",""+empno);
        
        Statement stat = db.getStatement();
        ResultSet rs = stat.executeQuery(query);
        while(rs.next()) {
            history.add(new GehaltHistory(rs));
        }
        db.releaseStatement(stat);
        return history;
    }
    
    public boolean updateEmployee(int row, Employee emp) throws SQLException {
        if(empress.absolute(++row)) {
            empress.updateString("first_name", emp.getFirstname());
            empress.updateString("last_name", emp.getLastname());
            empress.updateDate("hire_date", java.sql.Date.valueOf(emp.getHiredate()));
            
            empress.updateRow();
            empress.moveToCurrentRow();
            return true;
        } 
        return false;
    }
    
    public List<HirePeriod> getAngestellte(String deptNo) throws SQLException {
        List<HirePeriod> hires = new ArrayList<>();
        
        String query = SQLStrings.GETEMPDEPTCOUNT.replace("(department)","'"+deptNo+"'");
        
        Statement stat = db.getStatement();
        ResultSet rs = stat.executeQuery(query);
        while(rs.next()) {
            hires.add(new HirePeriod(rs));
        }
        db.releaseStatement(stat);
        return hires;
    }
    
}
