package org.apis.gui.controller.wallet.tokenlist;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apis.gui.common.JavaFXStyle;
import org.apis.gui.controller.base.BaseViewController;
import org.apis.gui.manager.AppManager;
import org.apis.gui.manager.ImageManager;
import org.apis.gui.manager.StyleManager;
import org.apis.util.blockchain.ApisUtil;

import java.math.BigInteger;
import java.net.URL;
import java.util.ResourceBundle;

public class TokenListHeadController extends BaseViewController {
    public static final int HEADER_STATE_CLOSE = 0;
    public static final int HEADER_STATE_OPEN = 1;
    private int headerState = HEADER_STATE_CLOSE;

    private String tokenAddress;
    private String tokenName;
    private BigInteger tokenValue;
    private String tokenSymbol;
    private String prevOnMouseClickedEventFxid = "";

    @FXML private AnchorPane rootPane;
    @FXML private Pane leftLine;
    @FXML private ImageView walletIcon, foldIcon1, frozenImg;
    @FXML private Label name, valueNatural1, valueUnit1;

    private Image imageFold = new Image("image/btn_fold@2x.png");
    private Image imageUnFold = new Image("image/btn_unfold@2x.png");

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Rectangle clip2 = new Rectangle(this.walletIcon.getFitWidth()-0.5,this.walletIcon.getFitHeight()-0.5);
        clip2.setArcWidth(30);
        clip2.setArcHeight(30);
        walletIcon.setClip(clip2);

        setState(HEADER_STATE_CLOSE);

        frozenImg.setVisible(false);
    }

    @Override
    public void update(){
        setTokenAddress(tokenAddress);
    }

    @FXML
    public void onMouseClicked(InputEvent event){
        String id = ((Node)event.getSource()).getId();
        if(id.equals("rootPane")){
            if(handler != null
                    && ( prevOnMouseClickedEventFxid.equals("") || prevOnMouseClickedEventFxid.equals("rootPane"))){
                handler.onClickEvent(event, tokenAddress);
            }

            prevOnMouseClickedEventFxid = "rootPane";
        }
    }

    @FXML
    public void onMouseEntered(InputEvent event){
        String id = ((Node)event.getSource()).getId();
        if(id.equals("rootPane")){
            if(this.headerState == HEADER_STATE_CLOSE){
                rootPane.setStyle(new JavaFXStyle(rootPane.getStyle()).add("-fx-background-color", "#f8f8f8").toString());
            }else if(this.headerState == HEADER_STATE_OPEN){
                rootPane.setStyle(new JavaFXStyle(rootPane.getStyle()).add("-fx-background-color", "#f8f8f8").toString());
            }
        }
    }

    @FXML
    public void onMouseExited(InputEvent event){
        String id = ((Node)event.getSource()).getId();
        if(id.equals("rootPane")){
            if(this.headerState == HEADER_STATE_CLOSE){
                rootPane.setStyle(new JavaFXStyle(rootPane.getStyle()).add("-fx-background-color", "#ffffff").toString());
            }else if(this.headerState == HEADER_STATE_OPEN){
                rootPane.setStyle(new JavaFXStyle(rootPane.getStyle()).add("-fx-background-color", "#ffffff").toString());
            }
        }
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;

        if(tokenAddress != null) {
            if (tokenAddress.equals("-1")) {
                tokenName = "APIS";
                tokenValue = AppManager.getInstance().getTotalApis();
                tokenSymbol = "APIS";
                this.walletIcon.setImage(ImageManager.apisIcon);
                this.name.setText(tokenName);
                this.valueUnit1.setText(tokenSymbol);
                this.valueNatural1.setText(ApisUtil.readableApis(tokenValue, ',', true));
            } else if (tokenAddress.equals("-2")) {
                tokenName = "MNR";
                tokenValue = AppManager.getInstance().getTotalMineral() ;
                tokenSymbol = "MNR";
                this.walletIcon.setImage(ImageManager.mineraIcon);
                this.name.setText(tokenName);
                this.valueUnit1.setText("MNR");
                this.valueNatural1.setText(ApisUtil.readableApis(tokenValue, ',', true));
            } else {
                for(int i=0; i<AppManager.getInstance().getTokens().size(); i++){
                    if(AppManager.getInstance().getTokens().get(i).getTokenAddress().equals(this.tokenAddress)){
                        tokenName = AppManager.getInstance().getTokens().get(i).getTokenName();

                        if(AppManager.getInstance().isFrozen(AppManager.getInstance().getTokens().get(i).getTokenAddress())) {
                            frozenImg.setVisible(true);
                            StyleManager.fontColorStyle(name, StyleManager.AColor.C4871ff);
                        } else {
                            frozenImg.setVisible(false);
                            StyleManager.fontColorStyle(name, StyleManager.AColor.C353535);
                        }
                        break;
                    }
                }
                tokenValue = AppManager.getInstance().getTotalTokenValue(tokenAddress);
                tokenSymbol = AppManager.getInstance().getTokenSymbol(tokenAddress);
                this.walletIcon.setImage(AppManager.getInstance().getTokenIcon(tokenAddress));
                this.name.setText(tokenName);
                this.valueUnit1.setText(tokenSymbol);
                this.valueNatural1.setText(ApisUtil.readableApis(tokenValue, ',', true));
            }
        }
    }

    public void setState(int state){
        this.headerState = state;
        switch (state){
            case HEADER_STATE_CLOSE :
                rootPane.setStyle( new JavaFXStyle(rootPane.getStyle()).add("-fx-background-color", "#ffffff").toString() );
                foldIcon1.setImage(imageUnFold);
                rootPane.setEffect(null);
                leftLine.setVisible(false);
                break;

            case HEADER_STATE_OPEN :
                rootPane.setStyle( new JavaFXStyle(rootPane.getStyle()).add("-fx-background-color", "#ffffff").toString() );
                foldIcon1.setImage(imageFold);
                rootPane.setEffect(new DropShadow(10, Color.color(0,0,0,0.2)));
                leftLine.setVisible(true);
                break;
        }
    }

    private TokenListHeadImpl handler;
    public void setHandler(TokenListHeadImpl handler){this.handler = handler; }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public String getTokenName() {
        return tokenName;
    }

    public BigInteger getTokenValue() {
        return tokenValue;
    }

    public String getSymbol() {
        return tokenSymbol;
    }

    public interface TokenListHeadImpl{
        void onClickEvent(InputEvent event, String tokenAddress);
    }

}
