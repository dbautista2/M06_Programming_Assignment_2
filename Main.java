import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class Main extends Application {

    private Connection connection;
    private DBConnectionPanel dbConnectionPanel;
    private TextArea logTextArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        dbConnectionPanel = new DBConnectionPanel();
        logTextArea = new TextArea();
        logTextArea.setEditable(false);

        Button insertButton = new Button("Insert Records");
        insertButton.setOnAction(e -> insertRecords());

        VBox centerBox = new VBox(10);
        centerBox.getChildren().addAll(dbConnectionPanel, insertButton, logTextArea);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Batch Insert Comparison App");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Connect to database when button is clicked
        dbConnectionPanel.getConnectButton().setOnAction(e -> connectToDatabase());
    }

    private void connectToDatabase() {
        String url = dbConnectionPanel.getUrl();
        String username = dbConnectionPanel.getUsername();
        String password = dbConnectionPanel.getPassword();

        try {
            Class.forName("org.sqlite.JDBC"); // Explicitly load SQLite JDBC driver
            connection = DriverManager.getConnection(url, username, password);
            logMessage("Connected to database: " + url);
        } catch (ClassNotFoundException | SQLException ex) {
            logMessage("Failed to connect to database: " + ex.getMessage());
        }
    }

    private void insertRecords() {
        if (connection == null) {
            logMessage("Not connected to database. Please connect first.");
            return;
        }

        final int batchSize = 1000;
        long startTime;

        try {
            // Without batch update
            startTime = System.currentTimeMillis();
            insertRecordsWithoutBatch(batchSize);
            long endTimeWithoutBatch = System.currentTimeMillis() - startTime;
            logMessage("Time taken without batch insert: " + endTimeWithoutBatch + " ms");

            // With batch update
            startTime = System.currentTimeMillis();
            insertRecordsWithBatch(batchSize);
            long endTimeWithBatch = System.currentTimeMillis() - startTime;
            logMessage("Time taken with batch insert: " + endTimeWithBatch + " ms");

        } catch (SQLException e) {
            logMessage("Error inserting records: " + e.getMessage());
        }
    }

    private void insertRecordsWithoutBatch(int batchSize) throws SQLException {
        String sql = "INSERT INTO Temp (num1, num2, num3) VALUES (?, ?, ?)";
        Random random = new Random();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 1; i <= 1000; i++) {
            preparedStatement.setDouble(1, random.nextDouble());
            preparedStatement.setDouble(2, random.nextDouble());
            preparedStatement.setDouble(3, random.nextDouble());
            preparedStatement.executeUpdate();
        }
    }

    private void insertRecordsWithBatch(int batchSize) throws SQLException {
        String sql = "INSERT INTO Temp (num1, num2, num3) VALUES (?, ?, ?)";
        Random random = new Random();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 1; i <= 1000; i++) {
            preparedStatement.setDouble(1, random.nextDouble());
            preparedStatement.setDouble(2, random.nextDouble());
            preparedStatement.setDouble(3, random.nextDouble());
            preparedStatement.addBatch();

            if (i % batchSize == 0) {
                preparedStatement.executeBatch();
            }
        }
        preparedStatement.executeBatch(); // Execute any remaining batches
    }

    private void logMessage(String message) {
        logTextArea.appendText(message + "\n");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (connection != null) {
            connection.close();
        }
    }
}
