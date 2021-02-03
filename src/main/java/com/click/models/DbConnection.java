package com.click.models;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.click.controllers.LoginController;

import java.io.Serializable;
import java.sql.*;

public class DbConnection {

    public static Connection connect() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:DB_PriceUpdater.db"); // connecting to our database
            //System.out.println("Connected!\n");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e + "");
        }
        return con;
    }
}
   /*public Connection loginConnection(){
        Connection con = DbConnection.connect();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM Users where UserName=? and Password=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, LoginController.getUsername());
            ps.setString(2, command.getPassword());
            rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count = count + 1;
            }
            if (count == 1) {
                command.getSender().tell(new TerminalLogin.DbResponseCommand("1"));
            } else if (count > 1) {
                command.getSender().tell(new TerminalLogin.DbResponseCommand("2"));
            } else {
                command.getSender().tell(new TerminalLogin.DbResponseCommand("3"));

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
    }*/


/*public class DbConnection extends AbstractBehavior<DbConnection.Command> {

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
            System.out.println(e + "");
        }
        return con;
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(LogInCommand.class, command -> {
                    Connection con = DbConnection.connect();
                    PreparedStatement ps = null;
                    ResultSet rs = null;
                    try {
                        String sql = "SELECT * FROM Users where UserName=? and Password=?";
                        ps = con.prepareStatement(sql);
                        ps.setString(1, command.getUsername());
                        ps.setString(2, command.getPassword());
                        rs = ps.executeQuery();
                        int count = 0;
                        while (rs.next()) {
                            count = count + 1;
                        }
                        if (count == 1) {
                            command.getSender().tell(new TerminalLogin.DbResponseCommand("1"));
                        } else if (count > 1) {
                            command.getSender().tell(new TerminalLogin.DbResponseCommand("2"));
                        } else {
                            command.getSender().tell(new TerminalLogin.DbResponseCommand("3"));

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
                .onMessage(ReadCommand.class, command -> {
                    Connection con = DbConnection.connect();
                    PreparedStatement ps = null;
                    ResultSet rs = null;
                    try {
                        String sql = "SELECT * FROM Products";
                        ps = con.prepareStatement(sql);
                        rs = ps.executeQuery();
                        command.getReceivedTable().setModel(DbUtils.resultSetToTableModel(rs));
                        command.getSender().tell(new ProductView.DbReadCommand((getContext().getSelf()), command.getReceivedTable()));
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
                .onMessage(WriteCommand.class, command -> {
                    Connection con = DbConnection.connect();
                    PreparedStatement ps = null;
                    try {
                        String sql = "INSERT INTO Products(product_Name,price,inventory) VALUES(?,?,?) ";
                        ps = con.prepareStatement(sql);
                        ps.setString(1, command.getProduct());
                        ps.setInt(2, command.getPrice());
                        ps.setInt(3, command.getNewStock());
                        ps.execute();
                        System.out.println("Data has been inserted!\n");
                    } catch (SQLException e) {
                        System.out.println(e.toString());
                        // always remember to close database connections
                    } finally {
                        try {
                            if (ps != null) {
                                ps.close();
                            }
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

    public static class StartDbConnection implements Command {
        private static final long serialVersionUID = 1L;
        private final String Message;

        public StartDbConnection(String message) {
            Message = message;
        }

        public String getMessage() {
            return Message;
        }
    }

}*/
