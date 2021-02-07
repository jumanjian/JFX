package com.click.models;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ControlActor extends AbstractBehavior<ControlActor.Command> {

    private ActorRef<HashMap> Terminal;
    private String Location;
    private boolean IsGlobal = false;
    private ArrayList<String> All_Locations;
    private ControlActor(ActorContext<ControlActor.Command> context) {
        super(context);
    }

    public static Behavior<ControlActor.Command> create() {
        return Behaviors.setup(ControlActor::new);
    }

    @Override
    public Receive<ControlActor.Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(InitialCommand.class, command -> {
                    this.Terminal = command.getSender();
                    this.Location = command.getLocation();

                    ArrayList ProductsToBeUpdated = new ArrayList();
                    HashMap<String, ArrayList<Integer>> productArrayList = command.getProductPriceMap();
                    for (Map.Entry<String, ArrayList<Integer>> mapElement : productArrayList.entrySet()) {
                        ProductsToBeUpdated.add(mapElement.getKey());
                    }
                    ActorSystem<DbConnectionActor.Command> fetchPrice = ActorSystem.create(DbConnectionActor.create(), "FetchConnection");
                    fetchPrice.tell(new DbConnectionActor.ReadPriceCommand(getContext().getSelf(), ProductsToBeUpdated));
                    return this;
                })
                .onMessage(InitialGlobalCommand.class, command -> {
                    this.Terminal = command.getSender();
                    this.All_Locations = command.getLocations();
                    this.IsGlobal = true;

                    ArrayList ProductsToBeUpdated = new ArrayList();
                    HashMap<String, ArrayList<Integer>> productArrayList = command.getProductPriceMap();
                    for (Map.Entry<String, ArrayList<Integer>> mapElement : productArrayList.entrySet()) {
                        ProductsToBeUpdated.add(mapElement.getKey());
                    }
                    ActorSystem<DbConnectionActor.Command> fetchPrice = ActorSystem.create(DbConnectionActor.create(), "FetchConnection");
                    fetchPrice.tell(new DbConnectionActor.ReadPriceCommand(getContext().getSelf(), ProductsToBeUpdated));
                    return this;
                })
                .onMessage(PriceFromDBCommand.class, command -> {
                    this.Terminal.tell(command.getProductsHashMap());

                    if (IsGlobal == true) {
                        for (String loc : All_Locations) {
                            ActorRef<LocationActor.Command> location = getContext().spawn(LocationActor.create(), loc );
                            location.tell(new LocationActor.UpdateCommand(getContext().getSelf(), command.getProductsHashMap(), loc));
                        }
                    } else {
                        ActorRef<LocationActor.Command> location = getContext().spawn(LocationActor.create(), Location);
                        location.tell(new LocationActor.UpdateCommand(getContext().getSelf(), command.getProductsHashMap(), Location));
                    }
                    return this;
                })
                .onMessage(NoResponseReceivedCommand.class, command -> {
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
        private final HashMap ProductPriceMap;
        private final String Location;
        private final ActorRef<HashMap> Sender;

        public InitialCommand(ActorRef<HashMap> sender, HashMap productPriceMap, String location) {
            Sender = sender;
            ProductPriceMap = productPriceMap;
            Location = location;
        }

        public ActorRef<HashMap> getSender() {
            return Sender;
        }

        public HashMap getProductPriceMap() {
            return ProductPriceMap;
        }

        public String getLocation() {
            return Location;
        }
    }

    public static class InitialGlobalCommand implements Command {
        private static final long serialVersionUID = 1L;
        private final HashMap ProductPriceMap;
        private final ArrayList<String> Locations;
        private final ActorRef<HashMap> Sender;

        public InitialGlobalCommand(ActorRef<HashMap> sender, HashMap productPriceMap, ArrayList<String> locations) {
            Sender = sender;
            ProductPriceMap = productPriceMap;
            Locations = locations;
        }

        public ActorRef<HashMap> getSender() {
            return Sender;
        }

        public HashMap getProductPriceMap() {
            return ProductPriceMap;
        }

        public ArrayList<String> getLocations() {
            return Locations;
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