package fi.tuni.prog3.sisu;

import java.util.ArrayList;

/**
 * An abstract class for storing information on Modules and Courses.
 *
 * @author qrmama
 */
public abstract class DegreeModule {

    // Permission to add any courses or modules to this module
    private boolean addAny = false;

    // List of child modules
    private ArrayList<DegreeModule> childModules;

    // Done credits to compare to minCredits
    private int doneCredits;

    // Minimum credits to pass the module
    private final int minCredits;

    // Modules name
    private final String name;

    // Type of the module (StudyModule, GroupingModule, 
    // DegreeProgramme, CourseUnit)
    private final String type = "DegreeModule";

    /**
     * A constructor for initializing the member variables.
     *
     * @param name: Name of the Module or Course.
     * @param minCredits: Minimum credits of the Module or Course.
     */
    public DegreeModule(String name, int minCredits) {
        this.name = name;
        this.minCredits = minCredits;
    }
    
    /**
     * Returns list of child modules of the Module.
     *
     * @return List of child modules of the Module
     */
    public ArrayList<DegreeModule> getChildModules() {
        return childModules;
    }
    
    /**
     * Returns done credits.
     *
     * @return Ammount of the credits done to the Module.
     */
    public int getDoneCredits() {
        return doneCredits;
    }

    /**
     * Method mainly for CourseUnit objects. Returns information if course is
     * set active or not.
     *
     * @return Information of courses activity.
     */
    public boolean getIsActive() {
        // Returns allways truen unless object is CourseUnit object.
        return true;
    }

    /**
     * Returns the minimum credits of the Module or Course.
     *
     * @return Minimum credits of the Module or Course.
     */
    public int getMinCredits() {
        return this.minCredits;
    }

    /**
     * Returns the name of the Module or Course.
     *
     * @return Name of the Module or Course.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return type of the DegreeModule object.
     *
     * @return Type of the DegreeModule object (DegreeProgramme, StudyModule,
     * GroupingModule or CourseUnit)
     */
    public String getType() {
        return this.type;
    }
    
    /**
     * Returns true if Module can have any Module as child module and false if
     * not.
     *
     * @return Permission to add any courses or modules to this module
     */
    public boolean isAddAny() {
        return addAny;
    }
    
    /**
     * Change permission to add any courses or modules to this module active or
     * unactive.
     *
     * @param addAny: Permission to add any courses or modules to this module.
     */
    public void setAddAny(boolean addAny) {
        this.addAny = addAny;
    }
    
    /**
     * Set new list of child modules to the object.
     *
     * @param childModules: New list of child modules
     */
    public void setChildModules(ArrayList<DegreeModule> childModules) {
        this.childModules = childModules;
    }
    
    /**
     * Update done credits.
     *
     * @param doneCredits: Ammount of the credits done to the Module.
     */
    public void setDoneCredits(int doneCredits) {
        this.doneCredits = doneCredits;
    }
    
    /**
     * Set CourseUnit to active or unactive.
     *
     * @param newIsActive: Boolean of new
     */
    public void setIsActive(boolean newIsActive) {
        // This method do nothing unless type is CourseUnit.
    }
}
