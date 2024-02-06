package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class for extracting data from the Sisu API.
 *
 * @author qrmama
 */
public class API_Reader {

    /**
     * Reads URL and creates string from its contents.
     *
     * @param urlString:
     * @return Srting of URL's content.
     * @throws Exception
     */
    private static String readFromUrl(String urlString) throws Exception {

        URL url = new URL(urlString);

        try ( InputStream input = url.openStream()) {

            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder json = new StringBuilder();

            int c;
            while ((c = reader.read()) != -1) {
                json.append((char) c);
            }
            return json.toString();
        }
    }

    /**
     * Recursively reads array of rules that module have and collects data from
     * childmodules.
     *
     * @param rules: JsonArray of rules (JsonObjects).
     * @param childModuleIds: Data from allready collected child modules.
     * @return TreeMap with child modules id as a key and type as a value.
     */
    private TreeMap<String, String> readRules(JsonArray rules,
            TreeMap<String, String> childModuleIds) {

        for (JsonElement ruleElement : rules) {
            JsonObject ruleObject = ruleElement.getAsJsonObject();

            String type = ruleObject.get("type").toString().replace("\"", "");

            if (type.equals("ModuleRule")) {
                String moduleGroupId = ruleObject.get("moduleGroupId")
                        .toString().replace("\"", "");
                childModuleIds.put(moduleGroupId, "groupID");
            } else if (type.equals("CourseUnitRule")) {
                String courseUnitGroupId = ruleObject.get("courseUnitGroupId")
                        .toString().replace("\"", "");
                childModuleIds.put(courseUnitGroupId, "courseID");
            } else if (type.equals("AnyModuleRule")) {
                childModuleIds.put("AnyModuleRule", "AnyModuleRule");
            } else if (type.equals("AnyCourseUnitRule")) {
                childModuleIds.put("AnyCourseUnitRule", "AnyCourseUnitRule");
            } // If rule have subrule read it.
            else if (ruleObject.has("rule")) {
                childModuleIds = this.readRule(ruleObject.get("rule")
                        .getAsJsonObject(), childModuleIds);
            } // If rule have subrules read them.
            else if (ruleObject.has("rules")) {
                JsonArray newRules = ruleObject.get("rules").getAsJsonArray();
                childModuleIds = this.readRules(newRules, childModuleIds);
            }
        }
        return childModuleIds;
    }

    /**
     * Recursivelly reads every rule that module have and collects data from
     * childmodules.
     *
     * @param rule: Parent modules rule as a JsonObject.
     * @param childModuleIds: Data from allready collected child modules.
     * @return TreeMap with child modules id as a key and type as a value.
     */
    private TreeMap<String, String> readRule(JsonObject rule,
            TreeMap<String, String> childModuleIds) {
        String type = rule.get("type").toString().replace("\"", "");

        switch (type) {
            case "ModuleRule":
                String moduleGroupId = rule.get("moduleGroupId")
                        .toString().replace("\"", "");
                childModuleIds.put(moduleGroupId, "groupId");
                break;

            case "CourseUnitRule":
                String courseUnitGroupId = rule.get("courseUnitGroupId")
                        .toString().replace("\"", "");
                childModuleIds.put(courseUnitGroupId, "courseID");
                break;

            case "AnyModuleRule":
                childModuleIds.put("AnyModuleRule", "AnyModuleRule");
                break;

            case "AnyCourseUnitRule":
                childModuleIds.put("AnyCourseUnitRule", "AnyCourseUnitRule");
                break;

            default:
                // If rule have subrule read it.
                if (rule.has("rule")) {
                    JsonObject childRule = rule.get("rule").getAsJsonObject();
                    readRule(childRule, childModuleIds);
                } // If rule have subrules read them.
                else if (rule.has("rules")) {
                    JsonArray rules = rule.get("rules").getAsJsonArray();
                    childModuleIds = this.readRules(rules, childModuleIds);
                }
                break;
        }

        return childModuleIds;
    }

    /**
     * A private method to get modules childs.
     *
     * @param moduleData: JsonObject with data from parent module.
     * @param module: Parent DegreeModule.
     * @return ArrayList of child modules.
     * @throws Exception
     */
    private ArrayList<DegreeModule> createChildModuleHierarchy(
            JsonObject moduleData, DegreeModule module) throws Exception {

        ArrayList<DegreeModule> childModules = new ArrayList<>();
        // Read parent modules rule
        if (moduleData.has("rule")) {
            JsonObject rule = moduleData.get("rule").getAsJsonObject();
            TreeMap<String, String> childModuleIds = readRule(rule,
                    new TreeMap<>());

            // Search data from each child and convert them to StudyModule
            // objects.
            for (Map.Entry<String, String> entry
                    : childModuleIds.entrySet()) {
                // Set info that parent module can have any module or any
                // course unit as child.
                if (entry.getKey().equals("AnyCourseUnitRule")
                        || entry.getKey().equals("AnyModuleRule")) {
                    module.setAddAny(true);
                    break;
                }
                DegreeModule childModule = readModuleData(entry.getKey(),
                        entry.getValue());
                childModules.add(childModule);
            }
        }

        return childModules;
    }

    /**
     * A private method to create DegreeModule object from JsonObject.
     *
     * @param moduleData: JsonObject with data from modules or courses.
     * @return DegreeModule object with data from modules or courses.
     * @throws Exception
     */
    private DegreeModule createModuleFromJson(
            JsonObject moduleData) throws Exception {
        // Get modules/courses name
        JsonObject nameData = moduleData.get("name").getAsJsonObject();
        String name;
        if (nameData.has("fi")) {
            name = nameData.get("fi").toString();
        } else {
            name = nameData.get("en").toString();
        }
        // ...credits
        int minCredits = 0;
        if (moduleData.has("credits")
                || moduleData.has("targetCredits")) {
            JsonObject creditData;
            try {
                creditData = moduleData.get("targetCredits")
                        .getAsJsonObject();
            } catch (Exception e) {
                creditData = moduleData.get("credits")
                        .getAsJsonObject();
            }
            minCredits = creditData.get("min").getAsInt();
        }
        // Create a right kind of DegreeModule based on type.
        DegreeModule module;
        if (moduleData.has("type")) {
            switch (moduleData.get("type").toString().replace("\"", "")) {
                case "GroupingModule":
                    module = new GroupingModule(name, minCredits);
                    break;
                case "StudyModule":
                    module = new StudyModule(name, minCredits);
                    break;
                default:
                    module = new DegreeProgramme(name, minCredits);
                    break;
            }
        } else {
            module = new CourseUnit(name, minCredits);
        }
        // Get modules child modules.
        module.setChildModules(this.createChildModuleHierarchy(moduleData,
                module));

        return module;
    }

    /**
     * A public method to recursively store data from Sisu-API for a given
     * module or course and for all its child modules or courses.
     *
     * @param idToSearch: Modules/Courses id or group id.
     * @param idType: Type of the idToSearch (ID, groupID or courseID)
     * @return A DegreeModule object with data from API
     * @throws Exception
     */
    public DegreeModule readModuleData(String idToSearch, String idType
    ) throws Exception {

        String jsonString;

        if (null == idType) {
            jsonString = readFromUrl("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId="
                    + idToSearch
                    + "&universityId=tuni-university-root-id");
            jsonString = jsonString.substring(1, jsonString.length() - 1);
        } else {
            switch (idType) {
                // If ids type is ID...
                case "ID":
                    jsonString = readFromUrl("https://sis-tuni.funidata.fi/kori/api/modules/"
                            + idToSearch);
                    break;
                // If ids type is groupID...
                case "groupID":
                    jsonString = readFromUrl("https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId="
                            + idToSearch
                            + "&universityId=tuni-university-root-id");
                    jsonString = jsonString.substring(1, jsonString.length() - 1);
                    break;
                // If ids type is courseID
                default:
                    jsonString = readFromUrl("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId="
                            + idToSearch
                            + "&universityId=tuni-university-root-id");
                    jsonString = jsonString.substring(1, jsonString.length() - 1);
                    break;
            }
        }

        JsonObject moduleData = new Gson().fromJson(jsonString, JsonObject.class);

        // Create DegreeModule from JsonObject and return it.
        return createModuleFromJson(moduleData);
    }

    /**
     * A public method to read and store data from Sisu-API for all
     * DegreeProgramme.
     *
     * @return a TreeMap containing DegreeProgrammes name as a key and id as a
     * value.
     * @throws Exception
     */
    public TreeMap<String, String> getDegreeProgrammes() throws Exception {

        // Read URL to get acces for DegreeProgramme data.
        String jsonString = readFromUrl("https://sis-tuni.funidata.fi/kori/api/"
                + "module-search?curriculumPeriodId=uta-lvv-2021&universityId="
                + "tuni-university-root-id&moduleType=DegreeProgramme&limit=1000");

        JsonObject json = new Gson().fromJson(jsonString, JsonObject.class);

        TreeMap<String, String> degreeProgIDs = new TreeMap<>();

        JsonArray jsonArr = json.getAsJsonArray("searchResults");

        // Go through each degreeprogram's data
        for (JsonElement jsonElement : jsonArr) {
            JsonObject jsonObj = jsonElement.getAsJsonObject();

            String id = jsonObj.get("id").toString();
            String name = jsonObj.get("name").toString();
            id = id.replace("\"", "");
            name = name.replace("\"", "");

            degreeProgIDs.put(name, id);
        }

        return degreeProgIDs;
    }

}
