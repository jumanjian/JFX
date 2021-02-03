package com.click.controllers;

import akka.actor.typed.ActorSystem;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
/*
public class Main {
    public static void main(String[] args) {
        //LoginScreen loginScreen= new LoginScreen();
        //loginScreen.start(null);
        ActorSystem<LoginActor.Command> userLogin = ActorSystem.create(LoginActor.create(), "LoginActor");
        userLogin.tell(new LoginActor.InitialCommand());
    }

}*/

public class Main extends Application{
    public static void main(String[] args) {
       launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.initStyle(StageStyle.DECORATED);
        File loginImageFile = new File("images/icon.jpg");
        Image icon = new Image(loginImageFile.toURI().toString());
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }
}

