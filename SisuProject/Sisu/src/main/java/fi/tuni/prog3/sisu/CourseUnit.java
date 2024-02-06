/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fi.tuni.prog3.sisu;

/**
 * Class for storing information on Course Unit. Extends DegreeModule
 *
 * @author qrmama
 */
public class CourseUnit extends DegreeModule {

    // Set new course as unactive
    private boolean isActive = false;

    // Type of module
    private final String type = "CourseUnit";

    /**
     * A constructor for initializing the member variables.
     *
     * @param name: Name of the Course Unit.
     * @param minCredits: Minimum credits of the Course Unit.
     */
    public CourseUnit(String name, int minCredits) {
        super(name, minCredits);
    }

    /**
     * Return courses activity.
     *
     * @return Courses activity
     */
    @Override
    public boolean getIsActive() {
        return this.isActive;
    }

    /**
     * Returns Modules type (CourseUnit)
     *
     * @return Modules type
     */
    @Override
    public String getType() {
        return this.type;
    }

    /**
     * Change courses activity.
     *
     * @param newIsActive: New activity
     */
    @Override
    public void setIsActive(boolean newIsActive) {
        this.isActive = newIsActive;
    }

}
