package com.click.controllers;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.click.models.DbConnection;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.jws.soap.SOAPBinding;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button cancelButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private ImageView loginImageView;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button loginButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File loginImageFile = new File("images/icon.jpg");
        Image loginImage = new Image(loginImageFile.toURI().toString());
        loginImageView.setImage(loginImage);
    }

    public void loginButtonOnAction(ActionEvent event) {
        if (!usernameTextField.getText().isEmpty() && !passwordTextField.getText().isEmpty()) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Please enter correct login details!");
        }
    }

    @FXML
    public void openProductsUpdateView() throws IOException {
        Stage productUpdateStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("productUpdate.fxml"));
        productUpdateStage.setScene(new Scene(root, 800, 600));
        File loginImageFile = new File("images/icon.jpg");
        Image icon = new Image(loginImageFile.toURI().toString());
        productUpdateStage.getIcons().add(icon);
        productUpdateStage.show();
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.close();
    }

    public void validateLogin() {
        Connection con = DbConnection.connect();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM Users where UserName=? and Password=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, usernameTextField.getText());
            ps.setString(2, passwordTextField.getText());
            rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count = count + 1;
            }
            if (count == 1) {
                try {
                    openProductsUpdateView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (count > 1) {
                loginMessageLabel.setText("Something's wrong. Duplicate data!");
            } else {
                loginMessageLabel.setText("Wrong username or password!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                assert rs != null;
                rs.close();
                ps.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}