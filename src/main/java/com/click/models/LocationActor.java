package com.click.models;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LocationActor extends AbstractBehavior<LocationActor.Command> {

    private LocationActor(ActorContext<LocationActor.Command> context) {
        super(context);
    }

    public static Behavior<LocationActor.Command> create() {
        return Behaviors.setup(LocationActor::new);
    }

    @Override
    public Receive<LocationActor.Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(UpdateCommand.class, command ->{
                    System.out.println("Location actor " + command.getLocationName() + " is running!");
                    HashMap<String, ArrayList<Integer>> productsOutput =  command.getProductPriceMap();
                    for (Map.Entry <String, ArrayList<Integer>> mapElement: productsOutput.entrySet()) {
                        Behavior<DisplayActor.Command> displayBehavior =
                                Behaviors.supervise(DisplayActor.create()).onFailure(SupervisorStrategy.restart());
                        ActorRef<DisplayActor.Command> display = getContext().spawn(displayBehavior, mapElement.getKey());
                        display.tell(new DisplayActor.NewPriceCommand(getContext().getSelf(), mapElement.getKey(), mapElement.getValue().get(0),mapElement.getValue().get(1), command.getLocationName()));
                        getContext().watch(display);
                    }
                    return this;
                })
                .onMessage(DisplayActorReply.class, command ->{
                    System.out.println(command.getLog());
                    return this;
                })
                .onSignal(Terminated.class, handler ->{
                    return this;
                })
                .build();
    }

    public interface Command extends Serializable {
    }

    public static class  UpdateCommand implements Command {
        private static final long serialVersionUID = 1L;
        private final ActorRef<ControlActor.Command> Sender;
        private final HashMap ProductPriceMap;
        private final String LocationName;

        public UpdateCommand(ActorRef<ControlActor.Command> sender, HashMap productPriceMap, String locationName) {
            Sender = sender;
            ProductPriceMap = productPriceMap;
            LocationName = locationName;
        }

        public ActorRef<ControlActor.Command> getSender() {
            return Sender;
        }

        public HashMap getProductPriceMap() {
            return ProductPriceMap;
        }

        public String getLocationName() {
            return LocationName;
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
}
