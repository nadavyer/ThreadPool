package bgu.spl.a2.sim.JsonFiles;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonActs {

    @SerializedName("Action")
    @Expose
    private String actionName;

    @SerializedName("Department")
    @Expose
    private String departmentName;

    @SerializedName("Course")
    @Expose
    private String courseName;

    @SerializedName("Space")
    @Expose
    private Integer initialSpace;

    @SerializedName("Prerequisites")
    @Expose
    private List<String> prerequisites;

    @SerializedName("Student")
    @Expose
    private String studentId;

    @SerializedName("Students")
    @Expose
    private List<String> students;

    @SerializedName("Grade")
    @Expose
    private List<String> grade;

    @SerializedName("Conditions")
    @Expose
    private List<String> conditions;

    @SerializedName("Computer")
    @Expose
    private String computerType;

    @SerializedName("Number")
    @Expose
    private String placesToAdd;

    @SerializedName("Preferences")
    @Expose
    private List<String> preferences;



    //setters and getters for jsonInput

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getSpace() {
        return initialSpace;
    }

    public void setSpace(Integer space) {
        this.initialSpace = space;
    }

    public List<String> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<String> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public List<String> getGrade() {
        return grade;
    }

    public void setGrade(List<String> grade) {
        this.grade = grade;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }

    public String getComputerType() {
        return computerType;
    }

    public void setComputerType(String computerType) {
        this.computerType = computerType;
    }

    public Integer getInitialSpace() {
        return initialSpace;
    }

    public void setInitialSpace(Integer initialSpace) {
        this.initialSpace = initialSpace;
    }

    public String getPlacesToAdd() {
        return placesToAdd;
    }

    public void setPlacesToAdd(String placesToAdd) {
        this.placesToAdd = placesToAdd;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }
}
