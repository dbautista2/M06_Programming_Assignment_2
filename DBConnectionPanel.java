import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class DBConnectionPanel extends GridPane {

   private TextField urlField;
   private TextField usernameField;
   private PasswordField passwordField;
   private Button connectButton;

   public DBConnectionPanel() {
       initializeUI();
   }

   private void initializeUI() {
       setPadding(new Insets(10));
       setHgap(10);
       setVgap(5);

       urlField = new TextField("jdbc:sqlite:test.db");
       usernameField = new TextField();
       passwordField = new PasswordField();
       connectButton = new Button("Connect to Database");

       add(new Label("Database URL:"), 0, 0);
       add(urlField, 1, 0);
       add(new Label("Username:"), 0, 1);
       add(usernameField, 1, 1);
       add(new Label("Password:"), 0, 2);
       add(passwordField, 1, 2);
       add(connectButton, 1, 3);
   }

   public String getUrl() {
       return urlField.getText().trim();
   }

   public String getUsername() {
       return usernameField.getText().trim();
   }

   public String getPassword() {
       return passwordField.getText();
   }

   public Button getConnectButton() {
       return connectButton;
   }
}
