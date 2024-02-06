package fi.tuni.prog3.sisu;

/**
 * Class for storing information on Grouping Modules. Extends DegreeModule
 *
 * @author qrmama
 */
public class GroupingModule extends DegreeModule {

    // Modules typw
    private final String type = "GroupingModule";

    /**
     * A constructor for initializing the member variables.
     *
     * @param name: Name of the Grouping Module.
     * @param minCredits: Minimum credits of the Grouping Module.
     */
    public GroupingModule(String name, int minCredits) {
        super(name, minCredits);
    }

    /**
     * Returns Modules type (GroupingModule)
     *
     * @return Modules type
     */
    @Override
    public String getType() {
        return this.type;
    }

}
