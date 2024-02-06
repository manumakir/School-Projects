
package fi.tuni.prog3.sisu;

import java.io.IOException;
import com.google.gson.JsonObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.io.File;
import java.io.FileWriter;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.FileReader;

/**
 * Class depicting the user of the application.
 * 
 * @author mqlapa
 */
public class User {
    
    /**
     * Variables for the user information.
     */
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty studentNumber = new SimpleStringProperty();
    
    public User() {
        /**
         * Constructor for the User class.
         * Initializes the values of the name and student number as empty.
         */
        name.setValue("");
        studentNumber.setValue("");
    }

    
    public StringProperty getName() {
        /**
         * Method to get the user's name. 
         * 
         * @return the user's name
         */    
        return name;
    }
    
    public StringProperty getStudentNumber() {
        /**
         * Method for returning the user's student number.
         * 
         * @return the user's student number
         */
        return studentNumber;
    }
    
    public String getDegreePrograms() {
        /**
         * Method for getting the name of the user-selected
         * degree programme.
         * 
         * @return the selected degree programme name
         */
        String selectedDegreeProgramme = Sisu.DegreeProgramme.getName()
                .replace("\"", "");
        
        return selectedDegreeProgramme;
    }

    public void makeJSON() {
        /**
         * Method for appending the user data into a .json file.
         */
        try {
        // Get the absolute path to the "data" directory
        String dataDirPath = new File("data").getAbsolutePath();

        // Create a new file object in the "data" directory
        File file = new File(dataDirPath + "/userData.json");

        // Create the file if it does not already exist
        file.createNewFile();
        
        // Read the existing JSON file
        JsonElement jsonElement = new JsonParser().parse(new FileReader(file));

        // Get the JSON object from the JSON element
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        // Update the JSON object with the new user data
        jsonObject.addProperty("name", name.get());
        jsonObject.addProperty("studentNumber", studentNumber.get());

        // Write the updated JSON object back to the file
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(jsonObject.toString());
        fileWriter.close();
        } catch (IOException e) {
        System.out.println("Error writing to userData.json: " + e.getMessage());
    }
}   
        
    JsonArray getModuleArray(DegreeModule module) {
        /**
         * Method for recursively fetching the information about the degree.
         * 
         * @param module : instance of the abstract base class DegreeModule
         */
                       
        // Create a new JSON array for the current module
        JsonArray moduleArray = new JsonArray();

        // Create a new JSON object for the current module
        JsonObject moduleObject = new JsonObject();

        // Add the module's properties to the JSON object
        moduleObject.addProperty("moduleName", module.getName());
        moduleObject.addProperty("moduleType", module.getType());
        moduleObject.addProperty("minCredits", module.getMinCredits());
        moduleObject.addProperty("isActive", module.getIsActive());
        moduleObject.addProperty("doneCredits", module.getDoneCredits());

        // Add the JSON object to the parent JSON array
        moduleArray.add(moduleObject);
        
        if (module.getChildModules() != null) {
            // Iterate over the child modules of the current module
        for (DegreeModule childModule : module.getChildModules()) {
            // Recursively generate a JSON array for each child module
            JsonArray childModuleArray = getModuleArray(childModule);
            // Add the child module's JSON array to the parent JSON array
            moduleArray.add(childModuleArray);
        }
        }
        // Return the parent JSON array
        return moduleArray;
    }


    
    public void saveDegreeToJSON(DegreeModule module) {
        /**
         * Saves the degree that the user has selected into 
         * the userData.json file.
         * 
         * @param module : instance of the abstract DegreeModule class
         */
        try {
        // Get the absolute path to the "data" directory
        String dataDirPath = new File("data").getAbsolutePath();

        // Create a new file object in the "data" directory
        File file = new File(dataDirPath + "/userData.json");
        
        // Create a JSON object to write into the file
        JsonObject jsonObject = new JsonParser().parse(new FileReader(file))
                                                .getAsJsonObject();

        // Update the JSON object with the selected degree
        jsonObject.addProperty("degreePrograms", 
                          getModuleArray(module).toString());

        // Write the updated JSON object back to the file
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(jsonObject.toString());
        fileWriter.close();
        
    } catch (IOException e) {
        System.out.println("Error writing to userData.json: " + e.getMessage());
    }
    }
    
}

    
