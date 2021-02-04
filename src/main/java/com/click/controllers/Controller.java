package com.click.controllers;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.click.models.DbConnection;
import com.click.models.DisplayActor;

import java.io.Serializable;
import java.util.*;

public class Controller extends AbstractBehavior<Controller.Command> {
    //String[] ProductsToBeUpdated = {"Miadi", "Ariel", "Downy", "Sunlight"};

    private Controller(ActorContext<Controller.Command> context) {
        super(context);
    }

    public static Behavior<Controller.Command> create() {
        return Behaviors.setup(Controller::new);
    }

    private void CLI() {
        HashMap<String, Integer> productsInput = new HashMap();

        productsInput.put("Sosoft", 566);
        productsInput.put("Kifaru", 889);
        productsInput.put("Ndovu", 998);

        /*Scanner scan = new Scanner(System.in);
        do{
            productsInput.put(scan.next(), scan.nextInt());
            System.out.println(productsInput);
        }
        while (scan.next()!= "0");*/
    }

    @Override
    public Receive<Controller.Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(InitialCommand.class, command -> {
                    HashMap<String, Integer> productsInput = new HashMap();
                    productsInput.put("Sosoft", 566);
                    productsInput.put("Kifaru", 889);
                    productsInput.put("Ndovu", 998);
                    ActorSystem<DbConnection.Command> insertNewPrice = ActorSystem.create(DbConnection.create(), "InsertConnection");
                    insertNewPrice.tell(new DbConnection.InsertPriceCommand(productsInput));
                    //String[] ProductsToBeUpdated;
                    ArrayList ProductsToBeUpdated = new ArrayList();
                    for (Map.Entry mapElement : productsInput.entrySet()) {
                        ProductsToBeUpdated.add(mapElement.getKey());
                    }
                    ActorSystem<DbConnection.Command> fetchPrice = ActorSystem.create(DbConnection.create(), "FetchConnection");
                    fetchPrice.tell(new DbConnection.ReadPriceCommand(getContext().getSelf(), ProductsToBeUpdated));
                    return this;
                })
                .onMessage(PriceFromDBCommand.class, command -> {
                    //ActorSystem<DisplayActor.Command> productPriceDisplay = ActorSystem.create(DisplayActor.create(), command.getUpdatedProduct());
                    //productPriceDisplay.tell(new DisplayActor.NewPriceCommand(command.getUpdatedProduct(), command.getUpdatedPrice()));

                    ActorRef<DisplayActor.Command> productPriceDisplay = getContext().spawn(DisplayActor.create(), command.UpdatedProduct);
                    productPriceDisplay.tell(new DisplayActor.NewPriceCommand(command.getUpdatedProduct(), command.getUpdatedPrice()));
                    return this;
                })
                .build();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public interface Command extends Serializable {
    }

    public static class PriceFromDBCommand implements Command {
        private static final long serialVersionUID = 1L;
        private final Integer UpdatedPrice;
        private final String UpdatedProduct;

        public PriceFromDBCommand(String updatedProduct, Integer updatedPrice) {
            UpdatedPrice = updatedPrice;
            UpdatedProduct = updatedProduct;
        }

        public Integer getUpdatedPrice() {
            return UpdatedPrice;
        }

        public String getUpdatedProduct() {
            return UpdatedProduct;
        }
    }

    public static class InitialCommand implements Command {
        private static final long serialVersionUID = 1L;
    }
}
