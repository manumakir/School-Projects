package fi.tuni.prog3.sisu;

/**
 * Class for storing information on Degree Programmes. Extends DegreeModule
 *
 * @author qrmama
 */
public class DegreeProgramme extends DegreeModule {

    // Modules type
    private final String type = "DegreeProgramme";

    /**
     * A constructor for initializing the member variables.
     *
     * @param name: Name of the Degree Programme.
     * @param minCredits: Minimum credits of the Degree Programme.
     */
    public DegreeProgramme(String name, int minCredits) {
        super(name, minCredits);
    }

    /**
     * Returns Modules type (DegreeProgramme)
     *
     * @return Modules type
     */
    @Override
    public String getType() {
        return this.type;
    }
}
