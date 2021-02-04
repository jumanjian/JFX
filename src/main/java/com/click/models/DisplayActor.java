package com.click.models;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class DisplayActor extends AbstractBehavior<DisplayActor.Command> {
    private JPanel mainPanel;
    private JLabel lblProductName;
    private JLabel lblOldPrice;
    private JLabel lblNewPrice;

    private DisplayActor(ActorContext<DisplayActor.Command> context) {
        super(context);
    }

    public static Behavior<DisplayActor.Command> create() {
        return Behaviors.setup(DisplayActor::new);
    }


    private void displayFrame(String ProductName) {
        JFrame displayFrame = new JFrame();
        displayFrame.setTitle(ProductName);
        displayFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        displayFrame.setVisible(true);
        //displayFrame.setSize(200, 200);
        displayFrame.getContentPane().setBackground(new Color(248, 248, 255));
        displayFrame.pack();

        displayFrame.setContentPane(mainPanel);
        displayFrame.setBounds(100, 100, 600, 300);
    }

    @Override
    public Receive<DisplayActor.Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(NewPriceCommand.class, command -> {
                    displayFrame(command.getProductName());
                    lblOldPrice.setText(lblNewPrice.getText());
                    lblNewPrice.setText(String.valueOf(command.getNewPrice()));
                    lblProductName.setText(command.getProductName());
                    return this;
                })
                .build();
    }

    public interface Command extends Serializable {
    }

    public static class NewPriceCommand implements Command {
        private static final long serialVersionUID = 1L;
        private final String productName;
        private final int newPrice;
        //private final int oldPrice;

       /* public NewPriceCommand(String productName, int newPrice, int oldPrice) {
            this.productName = productName;
            this.newPrice = newPrice;
            this.oldPrice = oldPrice;
        }*/

        public NewPriceCommand(String productName, int newPrice) {
            this.productName = productName;
            this.newPrice = newPrice;
        }

        public String getProductName() {
            return productName;
        }

        public int getNewPrice() {
            return newPrice;
        }

        //public int getOldPrice() { return oldPrice; }
    }
}
