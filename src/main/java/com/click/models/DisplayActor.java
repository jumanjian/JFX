package com.click.models;

import akka.actor.typed.ActorRef;
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
        displayFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        displayFrame.setVisible(true);
        displayFrame.setSize(800, 700);
        displayFrame.getContentPane().setBackground(new Color(248, 248, 255));
        displayFrame.pack();

        displayFrame.setContentPane(mainPanel);
        displayFrame.setBounds(100, 100, 600, 300);
    }

    @Override
    public Receive<DisplayActor.Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(NewPriceCommand.class, command -> {
                    /*Random r = new Random();
                    if (r.nextInt(1) == 1 ){

                    }*/
                    displayFrame(command.getLocationName());

                    lblOldPrice.setText(command.getOldPrice().toString());
                    lblNewPrice.setText(command.getNewPrice().toString());
                    lblProductName.setText(command.getProductName());
                    command.getControl().tell(new LocationActor.DisplayActorReply(getContext().getSelf(), "Price Displayed Successfully in " + command.getLocationName()));
                    getContext().getLog().debug("Display actor running");
                    return this;
                })
                .build();
    }

    public interface Command extends Serializable {
    }

    public static class NewPriceCommand implements Command {
        private static final long serialVersionUID = 1L;
        private final ActorRef<LocationActor.Command> Control;
        private final String ProductName;
        private final Integer NewPrice;
        private final Integer OldPrice;
        private final String LocationName;


        public NewPriceCommand(ActorRef<LocationActor.Command> control, String productName, Integer newPrice, Integer oldPrice, String locationName) {
            Control = control;
            ProductName = productName;
            NewPrice = newPrice;
            OldPrice = oldPrice;
            LocationName = locationName;
        }

        public ActorRef<LocationActor.Command> getControl() {
            return Control;
        }

        public String getProductName() {
            return ProductName;
        }

        public Integer getNewPrice() {
            return NewPrice;
        }

        public Integer getOldPrice() {
            return OldPrice;
        }

        public String getLocationName() { return LocationName;}
    }
}
