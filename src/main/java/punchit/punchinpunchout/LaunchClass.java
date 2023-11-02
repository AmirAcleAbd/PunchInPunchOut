package punchit.punchinpunchout;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import punchit.punchinpunchout.QueryCenter.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class LaunchClass extends Application {

    public static Scene sceneMain = null;
    private VBox rootHome = null;
    private VBox rootAdmin = null;
    private boolean isAdmin = false;

    @Override
    public void start(Stage HomeStage) throws IOException {
        StudentTrack.getInstance();
        QueueTrack.getInstance();

        String csvFilePath = "target/CrytsalPalace/MassStudentAdd.csv";
        String[][] csvData = parseCSV(csvFilePath);

        StudentTrack.getInstance().addAdmin("admin", "pass");

        for (String[] row : csvData) {
            Student s = new Student(row[0], row[1], Integer.parseInt(row[2]), Course.valueOf(row[3]), Professor.valueOf(row[4]));
            StudentTrack.getInstance().addStudent(s);
        }

        HomeStage.setTitle("Punch In Punch Out");
        HomeStage.setResizable(true);
        //sceneMain = new Scene(intialPane, 1200, 1080);
        sceneMain = new Scene(homePage(), 1200, 1080);

        HomeStage.setScene(sceneMain);
        HomeStage.show();

        HomeStage.setOnCloseRequest((WindowEvent event) -> {
            QueueTrack.getInstance().dequeueAll();
            StudentTrack.getInstance().saveInstance();
            QueueTrack.getInstance().saveInstance();
        });
    }

    public static void main(String[] args) {
        launch();
    }

    private Pane homePage(){

        rootHome = new VBox();
        BorderPane twoPane = new BorderPane();
        SplitPane lgnPane = new SplitPane();
        SplitPane qPane = queuePane();

        ImageView logo;
        Image logoImage = new Image(getClass().getResourceAsStream("LogoCenter/tutorCenter.png"));
        logo = new ImageView(logoImage);
        logo.setPreserveRatio(true);
        logo.setFitHeight(90);

        BorderPane filterBox = new BorderPane();
        filterBox.setPrefHeight(150);
        filterBox.setPadding(new Insets(10,10,10,10));

        filterBox.setStyle("-fx-background-color: blue;");

        Button btnRemoveSession = new Button("Remove Session");
        Button btnAdminPage = new Button("Admin Page");
        Button btnLogout = new Button("Logout");

        btnRemoveSession.setPrefSize(150, 40);
        btnAdminPage.setPrefSize(150, 40);

        if(isAdmin){
            filterBox.setLeft(btnRemoveSession);
            filterBox.setCenter(btnLogout);
            filterBox.setRight(btnAdminPage);
        }

        //btnRemoveSession will remove the selected session from the qPane table


        Text nameOfUser = new Text("ADMIN");
        nameOfUser.setFont(Font.font("Open Sans", 25));


        twoPane.setPadding(new Insets(10,10,10,10));
        twoPane.setCenter(logo);

        btnRemoveSession.setOnAction(actionEvent -> {

        });
        btnAdminPage.setOnAction(actionEvent -> {
            sceneMain.setRoot(adminPage());
        });
        btnLogout.setOnAction(actionEvent -> {
            isAdmin = false;
            sceneMain.setRoot(homePage());
        });


        rootHome.getChildren().addAll(twoPane, filterBox, qPane);

        return rootHome;

    }

    private Pane adminPage(){

        rootAdmin = new VBox();
        BorderPane twoPane = new BorderPane();
        SplitPane userPane = new SplitPane();
        SplitPane mainStatPane = sessionHistroyPane(StudentTrack.getInstance().getTotalSessionHistory());


        HBox filterBox = new HBox();
        filterBox.setPrefHeight(60);
        filterBox.setPadding(new Insets(10,10,10,10));
        filterBox.setSpacing(100);
        filterBox.setAlignment(Pos.CENTER);
        filterBox.setStyle("-fx-background-color: blue;");



        Button btnStatPane = new Button("Total Session History");
        Button btnUsePane = new Button("User Statistics");
        Button btnExport = new Button("Export Data");
        Button btnReturn = new Button("Return");

        btnStatPane.setPrefSize(150, 40);
        btnUsePane.setPrefSize(150, 40);


        filterBox.getChildren().addAll(btnStatPane,btnExport ,btnUsePane, btnReturn);





        twoPane.setPadding(new Insets(10,10,10,10));


        TableView<Student> userTable = new TableView<>();
        ObservableList<Student> stdnts = FXCollections.observableArrayList(StudentTrack.getInstance().getStudents());

        TableColumn<Student, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIDString()));

        TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));

        TableColumn<Student, Integer> timeColumn = new TableColumn<>("time");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getMinutesTotal()));

        userTable.getColumns().addAll(idColumn, nameColumn, timeColumn);
        userTable.setItems(stdnts);


        ListView<Session> sessionHistoryList = new ListView<>();

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Student selectedUser = userTable.getSelectionModel().getSelectedItem();
                ObservableList<Session> studentSeshHis = FXCollections.observableArrayList(selectedUser.getSessionHistory());

                sessionHistoryList.setItems(studentSeshHis);
            }
        });



        btnStatPane.setOnAction(actionEvent -> {
            rootAdmin.getChildren().removeAll(userPane);
            rootAdmin.getChildren().add(mainStatPane);
        });
        btnUsePane.setOnAction(actionEvent -> {
            rootAdmin.getChildren().removeAll(mainStatPane);
            rootAdmin.getChildren().add(userPane);
        });
        btnExport.setOnAction(actionEvent -> {
            StudentTrack.getInstance().exportToCSV();
        });
        btnReturn.setOnAction(actionEvent -> {
            sceneMain.setRoot(homePage());
        });
        userPane.getItems().addAll(userTable, sessionHistoryList);

        rootAdmin.getChildren().addAll(twoPane, filterBox, mainStatPane);

        return rootAdmin;

    }

    private SplitPane queuePane() {
        SplitPane splitPane = new SplitPane();
        splitPane.setPrefHeight(1080);
        BorderPane borderPane = loginPage();

        TableView<Session> tableView = new TableView<>();
        ObservableList<Session> sessionList = FXCollections.observableList(QueueTrack.getInstance().getOpenQueue());


        TableColumn<Session, String> courseColumn = new TableColumn<>("Course");
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        courseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourse().name()));

        TableColumn<Session, String> professorColumn = new TableColumn<>("Professor");
        professorColumn.setCellValueFactory(new PropertyValueFactory<>("professor"));
        professorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProfessor().name()));

        TableColumn<Session , String> topicColumn = new TableColumn<>("Topic");
        topicColumn.setCellValueFactory(new PropertyValueFactory<>("topic"));
        topicColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTopic()));

        TableColumn<Session, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getName()));

        tableView.getColumns().addAll(nameColumn, courseColumn, professorColumn, topicColumn);
        tableView.setItems(sessionList);

        splitPane.getItems().addAll(tableView, borderPane);

        return splitPane;
    }


    private BorderPane loginPage() {
        GridPane loginSegement = new GridPane();
        BorderPane thePanes = new BorderPane();

        loginSegement.setAlignment(Pos.CENTER);
        loginSegement.setPadding(new Insets(20));
        loginSegement.setHgap(10);
        loginSegement.setVgap(10);

        Label lblID = new Label("Identification");
        Label lblName = new Label("Name");
        Label lblPassword = new Label("Password");
        TextField tfIdentification = new TextField();
        TextField tfPassword = new TextField();
        TextField tfName = new TextField();

        loginSegement.add(lblID, 0, 0);
        //PaneLoginPage.add(lblPassword, 0, 1);
        loginSegement.add(tfIdentification, 1, 0, 2, 1);
        //PaneLoginPage.add(tfPassword, 1, 1, 2, 1);

        HBox HboxLogin = new HBox();
        HboxLogin.setSpacing(10);
        HboxLogin.setAlignment(Pos.CENTER_RIGHT);
        Button btnEnter = new Button("Enter");
        Button btnSignUp = new Button("Sign up");
        Button btnSignUpConfirm = new Button("Sign up");
        btnEnter.setDisable(true);
        btnEnter.setPrefWidth(60);
        btnSignUp.setPrefWidth(60);
        HboxLogin.getChildren().addAll(btnSignUp, btnEnter);
        loginSegement.add(HboxLogin, 1, 2, 2, 1);
        thePanes.setCenter(loginSegement);

        //everything above here is main login screen
        class MyKeyEventHanlder implements EventHandler<KeyEvent> {
            public void handle(KeyEvent e) {
                String username = tfIdentification.getText();
                String password = tfPassword.getText();
                btnEnter.setDisable(!validateLogin(username));
            }
        }
        tfIdentification.setOnKeyTyped(new MyKeyEventHanlder());
        tfPassword.setOnKeyTyped(new MyKeyEventHanlder());


        btnEnter.setOnAction(e->{
            String idString = tfIdentification.getText();
            Course[] courses = new Course[2];

            boolean checkUserExist = idString.matches("\\d+");
            if(checkUserExist){
                if(QueueTrack.getInstance().contains(Integer.parseInt(idString)) != null) {
                    Button btnEndSession = new Button("End Session");
                    thePanes.getChildren().remove(loginSegement);
                    thePanes.setTop(loginSegement);
                    thePanes.setBottom(btnEndSession);

                    btnEndSession.setOnAction(e1 ->{
                        QueueTrack.getInstance().dequeue(Integer.parseInt(idString));
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setHeaderText("Successfully logged out");
                        alert.setContentText("You spent " + StudentTrack.getInstance().getLastSession().getDuration()+ " minutes in the queue");
                        alert.showAndWait();
                    });
                }else{
                    if (StudentTrack.getInstance().isThere(Integer.parseInt(idString))) {
                        Student currStudent = StudentTrack.getInstance().getStudent(Integer.parseInt(idString));

                        VBox bothVbox = new VBox();
                        VBox courseVbox = new VBox();
                        bothVbox.setAlignment(Pos.CENTER);
                        courseVbox.setAlignment(Pos.CENTER);
                        bothVbox.setSpacing(40);
                        courseVbox.setSpacing(40);
                        Label courseLabel = new Label("Courses");
                        courseLabel.setPrefSize(200, 50);
                        courseLabel.setAlignment(Pos.CENTER_LEFT);
                        courseVbox.getChildren().add(courseLabel);
                        TextField tfTopic = new TextField();
                        tfTopic.setPadding(new Insets(20));

                        List<CheckBox> checkBoxes = new ArrayList<>();

                        Button btnStartSession = new Button("Start Session");
                        btnStartSession.setAlignment(Pos.CENTER);
                        btnStartSession.setPadding(new Insets(20));
                        btnStartSession.setPrefSize(400, 150);
                        for(Course course: currStudent.getCourses()){
                            CheckBox cb = new CheckBox(course.name());
                            cb.setAlignment(Pos.CENTER_LEFT);
                            checkBoxes.add(cb);
                            cb.setPrefSize(200, 50);
                            courseVbox.getChildren().add(cb);

                            cb.setOnAction(e1->{
                                if(cb.isSelected()){
                                    cb.setSelected(true);
                                    courses[0] = (Course.valueOf(cb.getText()));
                                    handleCheckBoxSelection(cb, checkBoxes);
                                }else{
                                    courses[0] = null;
                                    cb.setSelected(false);
                                }
                            });

                        }

                        btnStartSession.setOnAction(e1 ->{
                            if(courses[0] == null){
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setHeaderText("NO COURSE OR PROFESSOR SELECTED");
                                alert.setContentText("Please select a course and professor");
                                alert.showAndWait();
                            }else{
                                Session session = new Session(currStudent.getId(), courses[0], tfTopic.getText(), LocalDate.now().toString(), System.currentTimeMillis() );
                                QueueTrack.getInstance().enqueue(session);
                                QueueTrack.getInstance().saveInstance();
                                sceneMain.setRoot(homePage());
                                thePanes.getChildren().remove(bothVbox);
                                thePanes.getChildren().remove(btnStartSession);
                                tfIdentification.clear();
                                tfPassword.clear();
                                btnEnter.setDisable(true);
                            }
                        });

                        bothVbox.getChildren().addAll(tfTopic ,courseVbox);
                        thePanes.getChildren().remove(loginSegement);
                        thePanes.setTop(loginSegement);
                        thePanes.setCenter(bothVbox);
                        thePanes.setBottom(btnStartSession);
                        thePanes.setAlignment(btnStartSession, Pos.CENTER);
                        thePanes.setMargin(btnStartSession, new Insets(20));
                        thePanes.setMargin(bothVbox, new Insets(20));
                        thePanes.setMargin(loginSegement, new Insets(20));

                    }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("ID NOT FOUND");
                        alert.setContentText("Failed to validate user sign up");
                        alert.showAndWait();
                    }
                }
            }else {
                if(StudentTrack.getInstance().checkForAdmin(idString)){
                    loginSegement.add(lblPassword, 0, 1);
                    loginSegement.add(tfPassword, 1, 1, 2, 1);
                    loginSegement.getChildren().remove(HboxLogin);

                    HBox HboxAdmin = new HBox();
                    HboxAdmin.setSpacing(10);
                    HboxAdmin.setAlignment(Pos.CENTER_RIGHT);
                    Button btnLogin = new Button("Login");
                    Button btnCancel = new Button("Cancel");
                    btnLogin.setDisable(true);
                    btnLogin.setPrefWidth(60);
                    btnCancel.setPrefWidth(60);
                    HboxAdmin.getChildren().addAll(btnCancel, btnLogin);
                    loginSegement.add(HboxAdmin, 1, 2 ,2, 1);

                    btnCancel.setOnAction(e1->{
                        loginSegement.getChildren().remove(lblPassword);
                        loginSegement.getChildren().remove(tfPassword);
                        loginSegement.getChildren().remove(HboxAdmin);
                        loginSegement.add(HboxLogin, 1, 2, 2, 1);
                    });

                    tfPassword.setOnKeyTyped(new EventHandler<KeyEvent>() {
                        public void handle(KeyEvent e) {
                            String username = tfPassword.getText();
                            btnLogin.setDisable(!validateLogin(username));
                        }
                    });

                    btnLogin.setOnAction(e1 ->{
                        String passwordString = tfPassword.getText();
                        if(StudentTrack.getInstance().validateLoginAdmin(idString, passwordString)){
                            isAdmin = true;
                            sceneMain.setRoot(homePage());
                        }
                    });

                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("WRONG CREDENTIALS");
                    alert.setContentText("Failed to validate user sign up");
                    alert.showAndWait();
                }
            }
        });

        btnSignUp.setOnAction(e->{

            lblID.setText("Full Name");
            tfIdentification.clear();
            loginSegement.getChildren().remove(HboxLogin);

            Course[] allCourses = Course.values();
            ArrayList<Course> selectedCourses = new ArrayList<>();


            thePanes.getChildren().clear();
            thePanes.setTop(loginSegement);
            VBox signUpVbox = new VBox();
            signUpVbox.setAlignment(Pos.CENTER);

            for(Course c : allCourses){
                CheckBox cb = new CheckBox(c.name());
                cb.setAlignment(Pos.CENTER_LEFT);
                cb.setPrefSize(200, 50);
                signUpVbox.getChildren().add(cb);

                cb.setOnAction(e1->{
                    if(cb.isSelected()){
                        cb.setSelected(true);
                        selectedCourses.add(Course.valueOf(cb.getText()));
                    }else{
                        selectedCourses.remove(Course.valueOf(cb.getText()));
                        cb.setSelected(false);
                    }
                });

            }

            thePanes.setCenter(signUpVbox);


            HBox HboxSignUp = new HBox();
            HboxSignUp.setSpacing(10);
            HboxSignUp.setAlignment(Pos.CENTER_RIGHT);
            Button btnCancel = new Button("Cancel");
            btnSignUpConfirm.setDisable(true);
            btnCancel.setPrefWidth(60);
            btnSignUpConfirm.setPrefWidth(60);
            HboxSignUp.getChildren().addAll(btnCancel, btnSignUpConfirm);
            loginSegement.add(HboxSignUp, 1, 3, 2, 1);

            btnCancel.setOnAction(e1->{
                lblID.setText("Identification");
                thePanes.getChildren().clear();
                thePanes.setCenter(loginSegement);
                loginSegement.getChildren().remove(HboxSignUp);
                loginSegement.add(HboxLogin, 1, 2, 2, 1);
            });

            btnSignUpConfirm.setOnAction(e1->{
                String[] parts = tfIdentification.getText().split(" ", 2);
                Student thisStudent = StudentTrack.getInstance().addStudent(parts[0], parts[1]);
                for(Course c : selectedCourses){
                    thisStudent.addCourse(c);
                }
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("SUCCESFUL");
                alert.setContentText("Your new ID is " + thisStudent.getId());
                alert.showAndWait();
                lblID.setText("Identification");
                thePanes.getChildren().clear();
                thePanes.setCenter(loginSegement);
                loginSegement.getChildren().remove(HboxSignUp);
                loginSegement.add(HboxLogin, 1, 2, 2, 1);

            });
        });

        tfIdentification.setOnKeyTyped(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent e) {
                String username = tfIdentification.getText();
                String password = tfPassword.getText();
                btnEnter.setDisable(!validateLogin(username));
                btnSignUpConfirm.setDisable(!validateLogin(username));
            }
        });

        tfPassword.setOnKeyTyped(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent e) {
                String username = tfIdentification.getText();
                String password = tfPassword.getText();
                btnEnter.setDisable(!validateLogin(username));
                //btnSignUpConfirm.setDisable(!validateSignUp(username, password, confirmPassword));
            }
        });

        return thePanes;
    }

    private boolean validateLogin(String username) {
        return (username.length()>=4);
    }

    public static String[][] parseCSV(String csvFilePath) {
        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                data.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data.toArray(new String[0][]);
    }

    private void handleCheckBoxSelection(CheckBox selectedCheckBox, List<CheckBox> allCheckBoxes) {
        if (selectedCheckBox.isSelected()) {
            for (CheckBox checkBox : allCheckBoxes) {
                if (checkBox != selectedCheckBox) {
                    checkBox.setSelected(false);
                }
            }
        }
    }

    private boolean validateLogin(String username, String password) {
        return (username.length()>=4 && password.length()>=4);
    }

    private SplitPane sessionHistroyPane(ArrayList<Session> sessionArrayList) {
        SplitPane splitPane = new SplitPane();
        SplitPane userPane = new SplitPane();
        splitPane.setPrefHeight(1080);

        TableView<Session> tableView = new TableView<>();
        ObservableList<Session> sessionList = FXCollections.observableArrayList(sessionArrayList);

        TableColumn<Session, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));

        TableColumn<Session, String> courseColumn = new TableColumn<>("Course");
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        courseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourse().name()));

        TableColumn<Session, String> professorColumn = new TableColumn<>("Professor");
        professorColumn.setCellValueFactory(new PropertyValueFactory<>("professor"));
        professorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProfessor().name()));

        TableColumn<Session, String> topicColumn = new TableColumn<>("Topic");
        topicColumn.setCellValueFactory(new PropertyValueFactory<>("topic"));
        topicColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTopic()));

        TableColumn<Session, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));

        TableColumn<Session, Long> durationColumn = new TableColumn<>("Duration");
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("Duration"));
        durationColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDuration()));

        tableView.getColumns().addAll(idColumn, courseColumn, professorColumn, topicColumn, durationColumn, dateColumn);
        tableView.setItems(sessionList);


        VBox chartsPane = new VBox();
        //PieChart genreChart = createGenrePieChart();
        PieChart reportChart = createClassNumReport();

        chartsPane.getChildren().addAll( reportChart);

        splitPane.getItems().addAll(tableView, chartsPane);

        return splitPane;
    }



    private PieChart createClassNumReport() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        Map<String, Integer> seshCount = new HashMap<>();

        for (Session session : StudentTrack.getInstance().getTotalSessionHistory()) {
            seshCount.put(session.getCourse().name(), seshCount.getOrDefault(session.getCourse().name(), 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : seshCount.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        return new PieChart(pieChartData);
    }
}