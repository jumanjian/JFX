package com.click.models;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.click.controllers.ControlActor;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import java.io.Serializable;
import java.sql.*;
import java.util.*;

public class DbConnectionActor extends AbstractBehavior<DbConnectionActor.Command> {

    private DbConnectionActor(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(DbConnectionActor::new);
    }

    public static Connection connect() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:DB_PriceUpdater.db"); // connecting to our database
        } catch (ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e + "");
        }
        return con;
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(ReadPriceCommand.class, command -> {
                    Connection con = DbConnectionActor.connect();
                    PreparedStatement ps = null;
                    ResultSet rs = null;
                    HashMap<String, ArrayList<Integer>> productsFromDB = new HashMap<>();
                    try {
                        for (Object product : command.getProductToBeUpdated()) {
                            String sql = "SELECT OldPrice, NewPrice FROM Products WHERE ProductName = ?";
                            ps = con.prepareStatement(sql);
                            ps.setString(1, (String) product);
                            rs = ps.executeQuery();
                            ArrayList<Integer> Values = new ArrayList<>();
                            Values.add(rs.getInt(1));
                            Values.add(rs.getInt(2));
                            productsFromDB.put((String) product, Values);
                            System.out.println("Data has been fetched!\n");
                            System.out.println((String) product +rs.getInt(1)+rs.getInt(2));
                        }
                        System.out.println(productsFromDB);
                        command.getSender().tell(new ControlActor.PriceFromDBCommand(productsFromDB));
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
                .build();
    }

    public interface Command extends Serializable {
    }

    public static class ReadPriceCommand implements Command {
        private static final long serialVersionUID = 1L;
        private final ActorRef<ControlActor.Command> Sender;
        private final ArrayList ProductToBeUpdated;

        public ReadPriceCommand(ActorRef<ControlActor.Command> sender, ArrayList productToBeUpdated) {
            Sender = sender;
            ProductToBeUpdated = productToBeUpdated;
        }

        public ActorRef<ControlActor.Command> getSender() {
            return Sender;
        }

        public ArrayList getProductToBeUpdated() {
            return ProductToBeUpdated;
        }
    }
}