package fi.tuni.prog3.sisu;

/**
 * Class for storing information on Study Modules. Extends DegreeModule
 *
 * @author qrmama
 */
public class StudyModule extends DegreeModule {

    // Modules type
    private final String type = "StudyModule";

    /**
     * A constructor for initializing the member variables.
     *
     * @param name: Name of the Study Module.
     * @param minCredits: Minimum credits of the Study Module.
     */
    public StudyModule(String name, int minCredits) {
        super(name, minCredits);
    }

    /**
     * Returns Modules type (StudyModule)
     *
     * @return Modules type
     */
    @Override
    public String getType() {
        return this.type;
    }

}
