package com.click.models;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.click.controllers.Controller;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DbConnection extends AbstractBehavior<DbConnection.Command> {

    private DbConnection(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(DbConnection::new);
    }

    public static Connection connect() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:DB_PriceUpdater.db"); // connecting to our database
            System.out.println("Connected!\n");
        } catch (ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e + "");
        }
        return con;
    }

    //String[] ProductsToBeUpdated = {"Miadi", "Ariel", "Downy", "Sunlight"};
    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(ReadPriceCommand.class, command -> {
                    Connection con = DbConnection.connect();
                    PreparedStatement ps = null;
                    ResultSet rs = null;
                    try {
                        for (Object product : command.getProductToBeUpdated()) {
                            //String sql = "SELECT * FROM Products where ProductName=? and Password=?";
                            String sql = "SELECT NewPrice FROM Products WHERE ProductName = ?";
                            ps = con.prepareStatement(sql);
                            ps.setString(1, (String) product);
                            rs = ps.executeQuery();
                            //System.out.println(rs.getInt(1));
                            command.getSender().tell(new Controller.PriceFromDBCommand((String) product, rs.getInt(1)));
                        }

                    } catch (SQLException e) {
                        System.out.println(e.toString());
                    } finally {
                        try {
                            assert rs != null;
                            rs.close();
                            ps.close();
                            con.close();
                        } catch (SQLException e) {
                            System.out.println(e.toString());
                        }
                    }
                    return this;
                })
                .onMessage(InsertPriceCommand.class, command -> {
                    Connection con = DbConnection.connect();
                    PreparedStatement ps = null;
                    try {
                        for (Map.Entry mapElement : command.getInputHashMap().entrySet()) {
                            String sql = "INSERT INTO Products(ProductName, NewPrice) VALUES(?,?)";
                            ps = con.prepareStatement(sql);
                            ps.setString(1, (String) mapElement.getKey());
                            ps.setInt(2, (int) mapElement.getValue());
                            ps.execute();
                            System.out.println("Data has been inserted!\n");
                        }
                    } catch (SQLException e) {
                        System.out.println(e.toString());
                    } finally {
                        try {
                            ps.close();
                            con.close();
                        } catch (SQLException e) {
                            System.out.println(e.toString());
                        }
                    }
                    /*for (Map.Entry mapElement : command.getInputHashMap().entrySet()) {
                            String sql = "INSERT INTO Products(ProductName, NewPrice) VALUES(?,?)";
                            ps = con.prepareStatement(sql);
                            ps.setString(1, (String) mapElement.getKey());
                            ps.setInt(2, (int) mapElement.getValue());
                            ps.execute();
                            System.out.println("Data has been inserted!\n");
                        }*/
                    return this;
                })
                .build();
    }

    public interface Command extends Serializable {
    }

    public static class InsertPriceCommand implements Command {
        private static final long serialVersionUID = 1L;
        private final HashMap<String, Integer> inputHashMap;

        public InsertPriceCommand(HashMap<String, Integer> inputHashMap) {
            this.inputHashMap = inputHashMap;
        }

        public HashMap<String, Integer> getInputHashMap() {
            return inputHashMap;
        }
    }

    public static class ReadPriceCommand implements Command {
        private static final long serialVersionUID = 1L;
        private final ActorRef<Controller.Command> Sender;
        private final ArrayList ProductToBeUpdated;

        public ReadPriceCommand(ActorRef<Controller.Command> sender, ArrayList productToBeUpdated) {
            Sender = sender;
            ProductToBeUpdated = productToBeUpdated;
        }

        public ActorRef<Controller.Command> getSender() {
            return Sender;
        }

        public ArrayList getProductToBeUpdated() {
            return ProductToBeUpdated;
        }
    }
}
