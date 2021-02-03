/*
package com.click.models;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.click.controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActor extends AbstractBehavior<LoginActor.Command> {

    public LoginActor(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(LoginActor::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(LoginDetailsCommand.class, command -> {
                    LoginController loginController = new LoginController();
                    Connection con = DbConnection.connect();
                    PreparedStatement ps = null;
                    ResultSet rs = null;
                    try {
                        String sql = "SELECT * FROM Users where UserName=? and Password=?";
                        ps = con.prepareStatement(sql);
                        ps.setString(1, command.getUsername());
                        ps.setString(2, command.getUsername());
                        rs = ps.executeQuery();
                        int count = 0;
                        while (rs.next()) {
                            count = count + 1;
                        }
                        if (count == 1) {
                            try {
                                //loginMessageLabel.setText("You attempted to login!");

                                loginController.openProductsUpdateView();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (count > 1) {
                            loginController.getLoginMessageLabel().setText("Wrong username or password!");
                        } else {
                            loginController.getLoginMessageLabel().setText("Something's wrong. Duplicate data!");
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

                    return this;
                })
                .build();
    }

    public interface Command extends Serializable {
    }

    public static class LoginDetailsCommand implements Command {
        private static final long serialVersionUID = 1L;
        private final String Username;
        private final String Password;

        public LoginDetailsCommand(String username, String password) {
            Username = username;
            Password = password;
        }

        public String getUsername() {
            return Username;
        }

        public String getPassword() {
            return Password;
        }
    }

    private void validateLogin() {
        LoginController loginController = new LoginController();
        loginController.getLoginMessageLabel().setText("Wrong username or password!");
        Connection con = DbConnection.connect();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM Users where UserName=? and Password=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, loginController.getUsername());
            ps.setString(2, loginController.getPassword());
            rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count = count + 1;
            }
            if (count == 1) {
                try {
                    //loginMessageLabel.setText("You attempted to login!");
                    loginController.openProductsUpdateView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (count > 1) {
                loginController.getLoginMessageLabel().setText("Wrong username or password!");
            } else {
                loginController.getLoginMessageLabel().setText("Something's wrong. Duplicate data!");
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

*/
