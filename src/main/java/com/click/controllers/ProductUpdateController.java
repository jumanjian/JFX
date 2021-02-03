package com.click.controllers;

import com.click.models.DbConnection;
import com.click.models.ProductModel;
import javafx.application.Application;
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
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ProductUpdateController implements Initializable {
    @FXML
    private ImageView bannerImageView;
    @FXML
    private TextField productNameTextField;
    @FXML
    private TextField newPriceTextField;
    @FXML
    private TextField inventoryTextField;
    @FXML
    private Button updateButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button clearButton;
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
        }else if (actionEvent.getSource() == updateButton){
            updateOperation();
        }else{
            Stage stage =(Stage) clearButton.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void mouseOnClickedEvent(MouseEvent event) {
        ProductModel productModel = productsTableView.getSelectionModel().getSelectedItem();
        productNameTextField.setText(productModel.getProductName());
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
        Connection con = DbConnection.connect();
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
        Connection con = DbConnection.connect();
        String sql = "INSERT INTO Products(ProductName, NewPrice, Inventory) VALUES('" + productNameTextField.getText()
                + "','" + newPriceTextField.getText() + "','" + inventoryTextField.getText() + "')";
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        showProducts();
    }

    private void updateOperation() {
        Connection con = DbConnection.connect();
        String sql = "UPDATE Products SET  NewPrice = " + newPriceTextField.getText() + " , Inventory = '" + inventoryTextField + "' WHERE ProductName = '" + productNameTextField.getText()+ "'";
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        showProducts();
    }
}
