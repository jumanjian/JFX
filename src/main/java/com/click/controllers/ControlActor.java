package com.click.controllers;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.click.models.DbConnectionActor;
import com.click.models.DisplayActor;
import org.apache.commons.collections.MultiMap;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ControlActor extends AbstractBehavior<ControlActor.Command> {

    private ControlActor(ActorContext<ControlActor.Command> context) {
        super(context);
    }

    public static Behavior<ControlActor.Command> create() {
        return Behaviors.setup(ControlActor::new);
    }

    private ActorRef<HashMap> terminal;



    /*private void askDisplayForResponse(ActorRef<DisplayActor.Command> display) {
        getContext().ask(Command.class, display, Duration.ofSeconds(5),
                (me) -> new DisplayActor.NewPriceCommand(me, FinalProductNameFromDB, FinalNewProductPriceFromDB, FinalOldProductPriceFromDB),

                (response, throwable) -> {
                    if (response != null) {
                        return response;
                    } else {
                        System.out.println("Display" + display.path() + "failed to respond.");
                        return new NoResponseReceivedCommand(display);
                    }
                });
    }*/

    @Override
    public Receive<ControlActor.Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(InitialCommand.class, command -> {
                    this.terminal = command.getSender();

                    ArrayList ProductsToBeUpdated = new ArrayList();
                    HashMap<String, ArrayList<Integer>> productArrayList =  command.getProductPriceMap();
                    for (Map.Entry <String, ArrayList<Integer>> mapElement: productArrayList.entrySet()) {
                        ProductsToBeUpdated.add(mapElement.getKey());
                    }
                    ActorSystem<DbConnectionActor.Command> fetchPrice = ActorSystem.create(DbConnectionActor.create(), "FetchConnection");
                    fetchPrice.tell(new DbConnectionActor.ReadPriceCommand(getContext().getSelf(), ProductsToBeUpdated));
                    return this;
                })
                .onMessage(PriceFromDBCommand.class, command -> {
                    this.terminal.tell(command.getProductsHashMap());

                    HashMap<String, ArrayList<Integer>> productsOutput =  command.getProductsHashMap();
                    for (Map.Entry <String, ArrayList<Integer>> mapElement: productsOutput.entrySet()) {
                        ActorRef<DisplayActor.Command> display = getContext().spawn(DisplayActor.create(), mapElement.getKey());
                        display.tell(new DisplayActor.NewPriceCommand(getContext().getSelf(), mapElement.getKey(), mapElement.getValue().get(0),mapElement.getValue().get(1)));
                    }
                    return this;
                })
                .onMessage(NoResponseReceivedCommand.class, command ->{
                    System.out.println("Retrying with display" + command.getDisplay().path());
                    //askDisplayForResponse(command.getDisplay());
                    return this;
                })
                .build();
    }
    public interface Command extends Serializable {
    }

    public static class InitialCommand implements Command {
        private static final long serialVersionUID = 1L;
        private ActorRef<HashMap> Sender;
        private final HashMap ProductPriceMap;

        public InitialCommand(ActorRef<HashMap> sender, HashMap productPriceMap) {
            Sender = sender;
            ProductPriceMap = productPriceMap;
        }

        public ActorRef<HashMap> getSender() {
            return Sender;
        }

        public HashMap getProductPriceMap() {
            return ProductPriceMap;
        }
    }

    public static class PriceFromDBCommand implements Command {
        private static final long serialVersionUID = 1L;
        private final HashMap productsHashMap;

        public PriceFromDBCommand(HashMap productsHashMap) {
            this.productsHashMap = productsHashMap;
        }

        public HashMap getProductsHashMap() {
            return productsHashMap;
        }
    }

    public static class DisplayActorReply implements Command {
        private static final long serialVersionUID = 1L;
        private final ActorRef<DisplayActor.Command> DisplayResponse;
        private final String Log;

        public DisplayActorReply(ActorRef<DisplayActor.Command> displayResponse, String log) {
            DisplayResponse = displayResponse;
            Log = log;
        }

        public ActorRef<DisplayActor.Command> getDisplayResponse() {
            return DisplayResponse;
        }

        public String getLog() {
            return Log;
        }
    }

    private class NoResponseReceivedCommand implements Command {
        private static final long serialVersionUID = 1L;
        private final ActorRef<DisplayActor.Command> display;

        public NoResponseReceivedCommand(ActorRef<DisplayActor.Command> display) {
            this.display = display;
        }

        public ActorRef<DisplayActor.Command> getDisplay() {
            return display;
        }
    }
}