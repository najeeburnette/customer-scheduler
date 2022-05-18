package controller;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.*;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * FXML controller class for Main Menu.
 *
 * @author Najee Burnette
 */

public class MainMenuController implements Initializable
{

    PreparedStatement ps;
    private Users userNow;
    public static Customers selectedCustomer;
    public static Appointments selectedAppointment;


    ObservableList<Customers> customerList = FXCollections.observableArrayList();
    private ObservableList<Appointments> appointmentList = FXCollections.observableArrayList();
    ObservableList<Countries> countryList = FXCollections.observableArrayList();
    ObservableList<FirstLevelDivisions> divisionsList = FXCollections.observableArrayList();

    @FXML private Label schedulingTitleLabel;
    @FXML private Label customerLabel;
    @FXML private Label appointmentsLabel;

    @FXML private Button addAppointmentButton;
    @FXML private Button addCustomerButton;
    @FXML private Button deleteAppointmentButton;
    @FXML private Button deleteCustomerButton;
    @FXML private Button updateAppointmentButton;
    @FXML private Button updateCustomerButton;

    @FXML private TableView<Customers> customerTable;
    @FXML private TableColumn<Customers, Integer> customerIdColumn;
    @FXML private TableColumn<Customers, String> nameColumn;
    @FXML private TableColumn<Customers, String> addressColumn;
    @FXML private TableColumn<Customers, String> postalColumn;
    @FXML private TableColumn<Customers, String> phoneColumn;
    @FXML private TableColumn<Customers, Integer> divisionColumn;

    @FXML private TableView<Appointments> appointmentTable;
    @FXML private TableColumn<Appointments, Integer> apptIdColumn;
    @FXML private TableColumn<Appointments, String> titleColumn;
    @FXML private TableColumn<Appointments, String> descColumn;
    @FXML private TableColumn<Appointments, String> locationColumn;
    @FXML private TableColumn<Appointments, String> typeColumn;
    @FXML private TableColumn<Appointments, Integer> customerIdColumn2;
    @FXML private TableColumn<Appointments, Integer> contactIdColumn;
    @FXML private TableColumn<Appointments, String> startColumn;
    @FXML private TableColumn<Appointments, String> endColumn;
    @FXML private TableColumn<Appointments, Integer> userIdColumn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        postalColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        divisionColumn.setCellValueFactory(new PropertyValueFactory<>("divisionID"));

        apptIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactIdColumn.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        customerIdColumn2.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        try { populateCustomersTable(); }
        catch (SQLException throwables) { throwables.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }

        try { populateAppointmentsTable(); }
        catch (SQLException throwables) { throwables.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }


    }
    /**
     * Gets the customer object selected in the Customer tableview.
     *
     * @return A customer object, null if no customer selected.
     */
    public static Customers getSelectedCustomer() { return selectedCustomer; }

    /**
     * Gets the Appointment object selected by the user in the Appointment tableview.
     *
     * @return A appointment object, null if no appointment selected.
     */
    public static Appointments getSelectedAppointment() { return selectedAppointment; }

    /**
     * Populates the customer table.
     * <p>
     * Data is pulled from database to populate the table using a prepared statement
     * Data is put inside an observable used to display in the Customer table view.
     * list which is then used to populate the table view in Scene Builder.
     *
     * @throws IOException throws when input or output operation is failed or error interpreted
     * @throws SQLException throws when sql operation is failed or error interpreted
     */
    @FXML private void populateCustomersTable() throws SQLException, IOException
    {
        try
        {
            customerList.clear();
            PreparedStatement ps = JDBC.connection.prepareStatement(
                    "SELECT * FROM customers, first_level_divisions, countries "
                            + "WHERE customers.Division_ID = first_level_divisions.Division_ID "
                            + "AND first_level_divisions.COUNTRY_ID = countries.Country_ID "
                            + "ORDER BY Customer_ID");

            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                int customerID = rs.getInt("Customer_ID");
                String customerName = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String postalCode = rs.getString("Postal_Code");
                String phone = rs.getString("Phone");
                LocalDateTime createDate = rs.getTimestamp("Create_Date").toLocalDateTime();
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int divisionID = rs.getInt("Division_ID");
                String divisionName = rs.getString("Division");
                String countryName = rs.getString("Country");

                customerList.add(new Customers(customerID, customerName, address,
                        postalCode, phone, createDate, createdBy, lastUpdate,
                        lastUpdatedBy, divisionID, divisionName, countryName));
            }
            customerTable.setItems(customerList);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Error with SQL!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Error!");
        }
    }

    /**
     * Populates the appointment table.
     * <p>
     * Data is pulled from database to populate the table using a prepared statement
     * Data is put inside an observable used to display in the Appointments table view.
     * list which is then used to populate the table view in Scene Builder.
     *
     *@throws IOException throws when input or output operation is failed or error interpreted
     *@throws SQLException throws when sql operation is failed or error interpreted
     */

    @FXML private void populateAppointmentsTable() throws SQLException, IOException {
        try {
            appointmentList.clear();

            PreparedStatement ps = JDBC.connection.prepareStatement(
                    "SELECT * FROM appointments, customers, users, contacts "
                            + "WHERE appointments.User_ID = users.User_ID "
                            + "AND appointments.Contact_ID = contacts.Contact_ID "
                            + "AND appointments.Customer_ID = customers.Customer_ID "
                            + "ORDER BY Start");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int appointmentID = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
                int customerID = rs.getInt("Customer_ID");
                String contactName = rs.getString("Contact_Name");
                String customerName = rs.getString("Customer_Name");

                // Not needed in table
                LocalDateTime createdDate = rs.getTimestamp("Create_Date").toLocalDateTime();
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int contactID = rs.getInt("Contact_ID");
                String email = rs.getString("Email");

                int userID = userNow.getuserID();

                appointmentList.add(new Appointments(appointmentID, title, description,
                        location, type, start, end, createdDate, createdBy, lastUpdate,
                        lastUpdatedBy, customerID, userID, contactID, contactName, customerName));
            }
            appointmentTable.setItems(appointmentList);
        }

        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Error with SQL!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Error!");
        }
    }

    /**
     * This method switches screens to the menu for adding a new customer to the database.
     *
     * @throws IOException
     */
    @FXML public void onActionAddCustomer(javafx.event.ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("/view/AddCustomerForm.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Add Customer");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method switches screens to the menu for modifying the information of a customer
     * selected in the table view.
     *
     * @throws IOException
     */
    public void onActionUpdateCustomer(javafx.event.ActionEvent actionEvent) throws IOException
    {

        selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            errorPopup(1);
        }
        else{
            Parent root = FXMLLoader.load(getClass().getResource("/view/UpdateCustomerForm.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Update Customer");
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * This method switches screens to the menu for add appointments to customers
     * selected in the table view.
     *
     * @throws IOException
     */
    @FXML void onActionAppointmentButton(ActionEvent event) throws IOException {
        selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            errorPopup(1);
        }
        else{
            Parent root = FXMLLoader.load(getClass().getResource("/view/AddAppointmentForm.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Add Appointment");
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * Displays error message prompts for various errors.
     */
    private void errorPopup(int alertNum) {

        ResourceBundle rb = ResourceBundle.getBundle("properties/lang", Locale.getDefault());

        Alert alert = new Alert(Alert.AlertType.ERROR);

        switch (alertNum) {
            case 1:
                alert.setTitle(rb.getString("Error: Nothing Selected"));
                alert.setHeaderText(rb.getString("No customer selected!"));
                alert.showAndWait();
                break;
            case 2:
                alert.setTitle("Error: Nothing Selected");
                alert.setHeaderText("No appointment selected");
                alert.showAndWait();
                break;
        }
    }
}