package com.click.controllers;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;

import java.math.BigInteger;
import java.util.SortedSet;
import java.util.concurrent.CompletionStage;

public class SwingMain {
    public static void main(String[] args) {
//        ActorSystem<ControlActor.Command> loginTerminal = ActorSystem.create(ControlActor.create(), "ControlActor");
//        loginTerminal.tell(new ControlActor.InitialCommand());

//        CompletionStage<SortedSet<BigInteger>> result = AskPattern.ask(bigPrimes,
//                (me) -> new ManagerBehavior.InstructionCommand("start", me),
//                Duration.ofSeconds(60),
//              bigPrimes.scheduler());
    }
}