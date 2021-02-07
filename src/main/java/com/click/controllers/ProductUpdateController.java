package com.click.controllers;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import com.click.models.DbConnectionActor;
import com.click.models.ProductModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.CompletionStage;

public class ProductUpdateController implements Initializable {
    HashMap<String, ArrayList<Integer>> ProductPriceHash = new HashMap<>();
    @FXML
    private ImageView bannerImageView;
    @FXML
    private TextField productNameTextField;
    @FXML
    private TextField newPriceTextField;
    @FXML
    private TextField oldPriceTextField;
    @FXML
    private TextField inventoryTextField;
    @FXML
    private Button saveButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button editButton;
    @FXML
    private RadioButton isGlobalRadiobutton;
    @FXML
    private TableView<ProductModel> productsTableView;
    @FXML
    private TableColumn<ProductModel, Integer> productIdColumn;
    @FXML
    private TableColumn<ProductModel, String> productNameColumn;
    @FXML
    private TableColumn<ProductModel, Integer> oldPriceColumn;
    @FXML
    private TableColumn<ProductModel, Integer> newPriceColumn;
    @FXML
    private TableColumn<ProductModel, Integer> inventoryColumn;

    @FXML
    private void buttonOnActionHandler(ActionEvent actionEvent) {
        if (actionEvent.getSource() == saveButton) {
            saveOperation();
        } else if (actionEvent.getSource() == editButton) {
            edit();
        } else if (actionEvent.getSource() == updateButton) {
            controlOperation();
        }
    }

    @FXML
    private void mouseOnClickedEvent(MouseEvent event) {
        ProductModel productModel = productsTableView.getSelectionModel().getSelectedItem();
        productNameTextField.setText(productModel.getProductName());
        oldPriceTextField.setText("" + productModel.getOldPrice());
        newPriceTextField.setText("" + productModel.getNewPrice());
        inventoryTextField.setText("" + productModel.getInventory());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showProducts();
        File loginImageFile = new File("images/banner.png");
        Image loginImage = new Image(loginImageFile.toURI().toString());
        bannerImageView.setImage(loginImage);
    }

    private ObservableList<ProductModel> getProductsList() {
        ObservableList<ProductModel> productsList = FXCollections.observableArrayList();
        Connection con = DbConnectionActor.connect();
        String sql = "SELECT * FROM Products";
        Statement st = null;
        ResultSet rs = null;
        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            ProductModel productModel;
            while (rs.next()) {
                productModel = new ProductModel(rs.getInt("ProductID"), rs.getString("ProductName"),
                        rs.getInt("OldPrice"), rs.getInt("NewPrice"), rs.getInt("Inventory"));
                productsList.add(productModel);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try {
                assert rs != null;
                rs.close();
                st.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return productsList;
    }

    private void showProducts() {
        ObservableList<ProductModel> display = getProductsList();

        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("ProductID"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("ProductName"));
        oldPriceColumn.setCellValueFactory(new PropertyValueFactory<>("OldPrice"));
        newPriceColumn.setCellValueFactory(new PropertyValueFactory<>("NewPrice"));
        inventoryColumn.setCellValueFactory(new PropertyValueFactory<>("Inventory"));

        productsTableView.setItems(display);
    }

    private void saveOperation() {
        Connection con = DbConnectionActor.connect();
        String sql = "INSERT INTO Products(ProductName, OldPrice, NewPrice, Inventory) VALUES('" + productNameTextField.getText()
                + "','" + oldPriceTextField.getText() + "','" + newPriceTextField.getText() + "','" + inventoryTextField.getText() + "')";
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        showProducts();
    }

    private void edit() {
        Connection con = DbConnectionActor.connect();
        String sql = "UPDATE Products SET OldPrice = " + oldPriceTextField.getText() + " , NewPrice = " + newPriceTextField.getText() + " , Inventory = " + inventoryTextField.getText() + " WHERE ProductName = '" + productNameTextField.getText() + "'";
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        showProducts();

        ArrayList<Integer> Values = new ArrayList<>();
        Values.add(Integer.valueOf(newPriceTextField.getText()));
        Values.add(Integer.valueOf(oldPriceTextField.getText()));
        Values.add(Integer.valueOf(inventoryTextField.getText()));

        ProductPriceHash.put(productNameTextField.getText(), Values);
    }
    private void controlOperation() {
        ActorSystem<ControlActor.Command> Terminal = ActorSystem.create(ControlActor.create(), "ControlActor");
        CompletionStage<HashMap> productUpdates = AskPattern.ask(Terminal,
                (me) -> new ControlActor.InitialCommand(me, ProductPriceHash),
                Duration.ofSeconds(80000),
                Terminal.scheduler());
        productUpdates.whenComplete(
                (reply, failure) -> {
                    if (reply != null) {
                        System.out.println("Operation Successful");
                    } else {
                        System.out.println("No response");
                    }
                }
        );
    }
    /*HashMap<String, ArrayList<Integer>> HashFromControl;

    public void setHashFromControl(HashMap<String, ArrayList<Integer>> hashFromControl) {
        HashFromControl = hashFromControl;
    }

    public void createDisplays() {
        String Product;
        String NewPrice;
        String OldPrice;
        for (Map.Entry<String, ArrayList<Integer>> mapElement : HashFromControl.entrySet()) {
            Product = mapElement.getKey().toString();
            NewPrice = mapElement.getValue().get(0).toString();
            OldPrice = mapElement.getValue().get(1).toString();
            try {
                openShelfDisplayView(Product,NewPrice,OldPrice);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Deprecated
    private void openShelfDisplayView(String Product, String NewPrice, String OldPrice) throws IOException {
        ShelfDisplayController shelf = new ShelfDisplayController();
        Stage shelfDisplayStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("shelfDisplay.fxml"));
        shelfDisplayStage.setScene(new Scene(root, 800, 600));
        File loginImageFile = new File("images/icon.jpg");
        Image icon = new Image(loginImageFile.toURI().toString());
        shelfDisplayStage.getIcons().add(icon);
        shelfDisplayStage.show();
        shelf.setTextProductNameLabel(Product);
        shelf.setTextNewPriceDisplayLabel(NewPrice);
        shelf.setTextOldPriceDisplayLabel(OldPrice);
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }*/
}
