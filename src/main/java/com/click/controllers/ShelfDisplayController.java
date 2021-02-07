package com.click.controllers;



import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import javafx.scene.control.Label;
import org.sqlite.jdbc4.JDBC4Connection;

import java.io.Serializable;
import java.sql.Connection;

public class ShelfDisplayController{
    @javafx.fxml.FXML
    private Label oldPriceDisplayLabel;
    @javafx.fxml.FXML
    private Label newPriceDisplayLabel;
    @javafx.fxml.FXML
    private Label productNameLabel;

    public void setTextOldPriceDisplayLabel(String oldPrice) {
        oldPriceDisplayLabel.setText(oldPrice);
    }

    public void setTextNewPriceDisplayLabel(String newPrice) {
        newPriceDisplayLabel.setText(newPrice);
    }
    public void setTextProductNameLabel(String productName) {
        productNameLabel.setText(productName);
    }
}