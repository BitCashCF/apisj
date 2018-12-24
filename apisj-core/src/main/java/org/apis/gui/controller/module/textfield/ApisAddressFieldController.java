package org.apis.gui.controller.module.textfield;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apis.gui.common.IdenticonGenerator;
import org.apis.gui.controller.base.BaseViewController;
import org.apis.gui.manager.AppManager;
import org.apis.gui.manager.ImageManager;
import org.apis.util.AddressUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class ApisAddressFieldController extends BaseViewController {

    @FXML private AnchorPane rootPane;
    @FXML private TextField addressField;
    @FXML private ImageView icon;

    private String address;
    private String mask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        AppManager.settingTextFieldStyle(addressField);
        AppManager.settingIdenticonStyle(icon);

        addressField.textProperty().addListener(addressFieldListener());
    }

    public ChangeListener<String> addressFieldListener() {
        return (observable, oldValue, newValue) -> {
            address = newValue;
            mask = null;

            if(newValue.indexOf("@") < 0 && newValue.indexOf("0x") >= 0){
                newValue = newValue.replaceAll("0x","");
                addressField.setText(newValue);
                return;
            }

            if(newValue != null && newValue.indexOf("@") >= 0){
                mask = newValue;
            }else if(AddressUtil.isAddress(newValue)){
                mask = AppManager.getInstance().getMaskWithAddress(newValue);
            }

            address = newValue;
            if(mask != null && mask.length() > 0){
                //use masking address
                address = AppManager.getInstance().getAddressWithMask(mask);
            }

            icon.setImage(ImageManager.getIdenticons(address));

            if(handler != null){
                handler.change(address, mask);
            }

        };
    }


    public void setText(String text) {
        addressField.setText(text);
    }

    public String getText() {
        return addressField.getText();
    }

    public String getAddress(){
        if(address == null || !AddressUtil.isAddress(address)){
            return null;
        }else{
            return address;
        }
    }

    public void setVisible(boolean isVisible) {
        this.rootPane.setVisible(isVisible);
    }


    public ApisAddressFieldImpl handler;
    public void setHandler(ApisAddressFieldImpl handler){ this.handler = handler; }
    public interface ApisAddressFieldImpl{
        void change(String address, String mask);
    }
}
