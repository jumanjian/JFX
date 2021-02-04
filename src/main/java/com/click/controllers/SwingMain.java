package com.click.controllers;

import akka.actor.typed.ActorSystem;
import com.click.controllers.Controller;

public class SwingMain {
    public static void main(String[] args) {
        ActorSystem<Controller.Command> loginTerminal = ActorSystem.create(Controller.create(), "LoginTerminal");
        loginTerminal.tell(new Controller.InitialCommand());
    }
}