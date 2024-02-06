package fi.tuni.prog3.sisu;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import java.util.Optional;
import java.util.Map;
import java.util.TreeMap;
import javafx.geometry.VPos;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * GUI that interacts with backend code
 *
 * @author mktusa
 */
public class Sisu extends Application {

    // Create a User object to store the user's information
    private User user = new User();

    // Create a TextField for the user's name
    TextField nameField = new TextField();

    // Create a TextField for the user's student number
    TextField studentNumberField = new TextField();

    // Create a ComboBox for the user's degree programs
    ComboBox<String> degreePrograms = new ComboBox<>();

    // Create a VBox for list of modules
    private HBox degreeProgrammeBox = new HBox();

    Stage stage_;

    private TreeMap<String, String> DegreeProgIDs;
    public static DegreeModule DegreeProgramme;

    // Constructor
    public Sisu() {

        // Initialize the modules_ field
        this.DegreeProgIDs = new TreeMap<>();
    }
    
    /**
     * Continue button for the beginning screen. When the user presses Continue
     * ->, the study management view is opened, and the userData.json file is
     * appended with the user's name and student number.
     *
     * @return continue button
     */
    private Button getContinueButton() { 
        
        //Creating a button to continue from starting screen.
        Button continueButton = new Button("Continue ->");
        
        // When the button is pressed, create scene for study management
        // and update userData.json with user's name and student number
        continueButton.setOnAction((ActionEvent event) -> {

            this.createSceneForStudyManagement();

            user = new User();

            // Set the user's name and student number
            user.getName().setValue(nameField.getText());
            user.getStudentNumber().setValue(studentNumberField.getText());

            // Call the makeJSON method on the User object
            user.makeJSON();
        });
        return continueButton;
    }
    
    /**
     * Creates the tree from degree contents by recursion
     *
     * @param module current level of the module tree
     * @return tree with children
     */
    private TreeItem createModuleSubTree(DegreeModule module) {

        TreeItem moduleTree = new TreeItem(module.getName() + ": (" +
                module.getMinCredits() + " | " + module.getDoneCredits() + ")");
        if (module.getChildModules() != null) {
            for (DegreeModule child : module.getChildModules()) {
                moduleTree.getChildren().add(createModuleSubTree(child));
            }
        }
        return moduleTree;
    }
    
    /**
     * Creates the tree selection structure wherein the user can 
     * explore their degree programme.
     */
    private void createModuleTree() {
        

        TreeItem moduleTree = createModuleSubTree(this.DegreeProgramme);
        TreeView treeRoot = new TreeView();
        treeRoot.setRoot(moduleTree);
        treeRoot.setMinWidth(860);
        this.degreeProgrammeBox.getChildren().add(treeRoot);
    }
    
    /**
     * ComboBox element for the user to select the degree module.
     * Calls the API_reader class, which fetches the modules from Sisu API.
     */
    private HBox getDegreeModuleSelection() {
        
        HBox selectionBox = new HBox();
        ComboBox degreeModuleSelection = new ComboBox();
        degreeModuleSelection.setMinWidth(880);

        degreeModuleSelection.setPromptText("Select degree module");
        
        for (Map.Entry<String, String> entry
                : this.DegreeProgIDs.entrySet()) {
            degreeModuleSelection.getItems().add(entry.getKey());
        }

        degreeModuleSelection.setOnAction((e) -> {

            String selected = degreeModuleSelection.
                    getSelectionModel().getSelectedItem().toString();
            this.degreeProgrammeBox.getChildren().clear();

            for (Map.Entry<String, String> entry
                    : this.DegreeProgIDs.entrySet()) {
                if (entry.getKey().equals(selected)) {
                    API_Reader apiReader = new API_Reader();
                    try {
                        this.DegreeProgramme = apiReader.readModuleData(entry.getValue(),
                                "ID");
                        createModuleTree();
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            }

        });

        selectionBox.getChildren().addAll(degreeModuleSelection);

        return selectionBox;

    }
    
    /**
     * Creating a button to terminate the application
     *
     * @return the Quit -button
     */
    private Button getQuitButton() {
        
        Button quitButton = new Button("Quit");

        quitButton.setOnAction((ActionEvent event) -> {
            Platform.exit();
        });

        return quitButton;
    }
    
    /**
     * Creating a button for saving the information about the degree 
     * the user selects to the userData.json file.
     * 
     * @return the Save degree -button
     */
    private Button getSaveDegreeButton() {
        
        Button saveDegreeButton = new Button("Save degree");
        saveDegreeButton.setOnAction((ActionEvent event) -> {
            user = new User();
            user.saveDegreeToJSON(DegreeProgramme);
        });
        
        return saveDegreeButton;
    }
    
    /**
     * Scene for displaying information about the user.
     * The user can edit their name and student number;
     * as they click "Ok", these are updated in the userData.json file.
     */
    private void createUserProfileScene() {
        
        
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("User profile");

        ButtonType okButtonType = new ButtonType("Save changes", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        // Display the already set name and student number
        String userNameCurr = "Name: " + user.getName().getValue();
        String studentNumberCurr = "Student number: " + user.getStudentNumber().getValue();
        //studentNumberCurr.setFont(Font.font("Verdana", FontWeight.NORMAL, 12, FontPosture.ITALIC));
        dialog.setContentText(userNameCurr);
        dialog.setContentText(studentNumberCurr);
        
        Label nameLabel = new Label(userNameCurr);
        gridPane.add(nameLabel, 0, 0, 1, 1);
        Label studentNumberLabel = new Label(studentNumberCurr);
        gridPane.add(studentNumberLabel, 0, 1, 1, 1);
        
        // Create TextFields to edit informations
        TextField studentNameNew = new TextField();
        studentNameNew.setPromptText("Name");
        TextField studentNumberNew = new TextField();
        studentNumberNew.setPromptText("Student number");
        
        // Add above TextFields for the user to edit their information to gridPane
        gridPane.add(studentNameNew, 2, 0);
        gridPane.add(new Label("Edit name:"), 1, 0);
        gridPane.add(studentNumberNew, 2, 1);
        gridPane.add(new Label("Edit student number: "), 1, 1);
       
        dialog.setContentText(userNameCurr);
        dialog.setContentText(studentNumberCurr);

        dialog.getDialogPane().setContent(gridPane);
        
        user = new User();

        // Set the user's name and student number
        user.getName().setValue(studentNameNew.getText());
        user.getStudentNumber().setValue(studentNumberNew.getText());

        // Call the makeJSON method on the User object
        user.makeJSON();
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                user.getName().setValue(studentNameNew.getText());
                user.getStudentNumber().setValue(studentNumberNew.getText());
                return new Pair<>(studentNameNew.getText(), studentNumberNew.getText());
            }
            return null;
        });
        
        Optional<Pair<String, String>> result = dialog.showAndWait();

        // If a result was returned, update the user's information
        result.ifPresent(pair -> {
            System.out.println("Name=" + pair.getKey() + ", Student number=" + pair.getValue());
        });
        
    }
    
    /**
     * Create a button for showing the user profile menu on click.
     * 
     * @return User profile -button
     */
    private Button getUserProfileButton() {
        
        Button userProfileButton = new Button("View profile");

        userProfileButton.setOnAction((ActionEvent event) -> {
            createUserProfileScene();
        });

        return userProfileButton;
    }
    
    /**
     * Create a scene for the main part of the program,
     * that is, the window where the user can manage their studies.
     */
    private void createSceneForStudyManagement() {
        
        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(10, 10, 10, 10));
        
        // Setup the top of the scene
        root.add(this.getUserProfileButton(),0,0,2,1);
        root.add(this.getDegreeModuleSelection(),2,0,2,1);
        root.add(this.getSaveDegreeButton(),4,0,2,1);
        
        // Setup a box with tree with contents of the degree
        root.add(this.degreeProgrammeBox,2,1,2,1);
        this.degreeProgrammeBox.setPadding(new Insets(10,10,10,10));
        this.degreeProgrammeBox.setMinHeight(800);
        
        // Setup and correctly alligne quit button
        root.add(this.getQuitButton(),0,2,2,1);
        GridPane.setValignment(this.getQuitButton(), VPos.BOTTOM);
        
        Scene studyManagementScene = new Scene(root, 1115, 900);
        this.stage_.setScene(studyManagementScene);
    }
    
    /**
     * Creates the text field for the user to enter their name
     * as they first open the program.
     * 
     * @return updated nameField object
     */
    public TextField getNameTextField() {
        
        this.nameField = new TextField();
        nameField.setFocusTraversable(false);
        nameField.setPromptText("Enter name...");

        return nameField;
    }
    
    /**
     * Creates the text field for the user to enter their student number
     * as they first open the program.
     *
     * @return updated studentNumberField object
     */
    public TextField getStudentNumberTextField() {
        
        this.studentNumberField = new TextField();
        studentNumberField.setFocusTraversable(false);
        studentNumberField.setPromptText("Enter student number...");

        return studentNumberField;
    }
    
    /**
     * Creates the drop-down menus wherefrom the user can select the
     * intended timeframe of their studies.
     * 
     * @return the studying years
     */
    private HBox getStudyingYears() {
        
        HBox studyingYears = new HBox(10);

        ComboBox startYearSelection = new ComboBox();
        startYearSelection.setFocusTraversable(false);
        startYearSelection.setPromptText("Starting year");

        for (int year = 2017; year <= 2030; year++) {
            startYearSelection.getItems().add(year);
        }
        ComboBox endYearSelection = new ComboBox();
        endYearSelection.setFocusTraversable(false);
        endYearSelection.setPromptText("Predicted ending year");
        
        // Listener for making sure the user can only enter ending years
        // that come after the selected starting year
        startYearSelection.setOnAction((e) -> {
            int startYear = (Integer) startYearSelection.getSelectionModel().getSelectedItem();
            endYearSelection.getItems().clear();
            for (int year = startYear; year <= startYear + 10; year++) {
                endYearSelection.getItems().add(year);
            }
        });

        studyingYears.getChildren().addAll(startYearSelection, endYearSelection);

        return studyingYears;
    }
    
    /**
     * Get the student number of the user to the studentNumberField object
     *
     * @return updated studentNumberField object
     */
    private VBox createStudentDataBox() {
        
        VBox studentDataBox = new VBox(10);

        studentDataBox.getChildren().addAll(getNameTextField(),
                getStudentNumberTextField(), getStudyingYears(),
                getContinueButton());

        return studentDataBox;
    }
    
    @Override
    public void start(Stage stage) {

        this.stage_ = stage;

        API_Reader apiReader = new API_Reader();
        try {
            this.DegreeProgIDs = apiReader.getDegreeProgrammes();
        } catch (Exception ex) {
            System.out.println(ex);
        }

        //Creating a new BorderPane.
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(30, 30, 30, 30));

        //Adding HBox to the center of the BorderPane.
        root.setCenter(createStudentDataBox());

        //Adding button to the BorderPane and aligning it to the right.
        var quitButton = getQuitButton();

        BorderPane.setMargin(quitButton, new Insets(10, 10, 0, 10));
        root.setBottom(quitButton);
        BorderPane.setAlignment(quitButton, Pos.TOP_LEFT);

        Scene scene = new Scene(root, 480, 270);
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("SisuGUI");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    
}
