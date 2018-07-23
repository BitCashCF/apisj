package org.apis.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.apis.gui.model.SelectBoxDomainModel;

import java.net.URL;
import java.util.ResourceBundle;

public class ApisSelectBoxItemDomainController implements Initializable {
    private SelectBoxDomainModel itemModel;
    private SelectBoxItemDomainInterface handler;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label domainLabel, priceLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void onMouseEntered(){ rootPane.setStyle("-fx-background-color: f2f2f2"); }

    public void onMouseExited(){
        rootPane.setStyle("-fx-background-color: transparent");
    }

    public void onMouseClicked(){
        if(handler != null){
            handler.onMouseClicked(this.itemModel);
        }
    }

    public void setModel(SelectBoxDomainModel model) {
        this.itemModel = model;

        this.domainLabel.textProperty().setValue(this.itemModel.getDomain());
        this.priceLabel.textProperty().setValue(this.itemModel.getApis());
    }

    public void setHandler(SelectBoxItemDomainInterface handler) {
        this.handler = handler;
    }

    public interface SelectBoxItemDomainInterface {
        void onMouseClicked(SelectBoxDomainModel itemModel);
    }
}
