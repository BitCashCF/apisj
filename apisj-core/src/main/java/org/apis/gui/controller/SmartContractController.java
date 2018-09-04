package org.apis.gui.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apis.core.CallTransaction;
import org.apis.core.Transaction;
import org.apis.gui.common.JavaFXStyle;
import org.apis.gui.manager.AppManager;
import org.apis.gui.manager.StringManager;
import org.apis.gui.model.ContractModel;
import org.apis.solidity.SolidityType;
import org.apis.solidity.compiler.CompilationResult;
import org.apis.util.ByteUtil;
import org.spongycastle.util.encoders.Hex;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SmartContractController implements Initializable {
    // Gas Price Popup Flag
    private static final boolean GAS_PRICE_POPUP_MOUSE_ENTERED = true;
    private static final boolean GAS_PRICE_POPUP_MOUSE_EXITED = false;
    private boolean gasPricePopupFlag = GAS_PRICE_POPUP_MOUSE_EXITED;
    private int selectedTabIndex = 0;
    // Gas Price
    private BigInteger gasPrice = new BigInteger("50");

    @FXML
    private Label aliasLabel, addressLabel, placeholderLabel;

    @FXML
    private Label tabLabel1, tabLabel2, tabLabel3, sideTabLabel1, sideTabLabel2;
    @FXML
    private Pane tabLinePane1, tabLinePane2, tabLinePane3, sideTabLinePane1, sideTabLinePane2;
    @FXML
    private AnchorPane tab1LeftPane, tab1RightPane, tab2LeftPane;
    @FXML
    private AnchorPane tab1AmountPane, tab2AmountPane, tab2ReadWritePane;
    @FXML
    private GridPane tab1GasPriceGrid, tab1GasPricePopupGrid, tab2GasPriceGrid, tab2GasPricePopupGrid;
    @FXML
    private GridPane transferBtn, writeBtn;
    @FXML
    private Label tab1GasPricePlusMinusLabel, tab2GasPricePlusMinusLabel, tab1GasPricePopupLabel, tab2GasPricePopupLabel, tab1GasPricePopupDefaultLabel, tab2GasPricePopupDefaultLabel;
    @FXML
    private Label cSelectHeadText, cSelectItemDefaultText, cSelectItemBalanceText, pSelectHeadText, pSelectHeadText_1;
    @FXML
    private ImageView cSelectHeadImg, tab1GasPricePopupImg, tab2GasPricePopupImg, tab1GasPriceMinusBtn, tab2GasPriceMinusBtn, tab1GasPricePlusBtn, tab2GasPricePlusBtn;
    @FXML
    private VBox cSelectList, cSelectChild;
    @FXML
    private GridPane cSelectHead, cSelectItemDefault, cSelectItemBalance;
    @FXML
    private VBox pSelectList, pSelectChild;
    @FXML
    private GridPane pSelectHead, pSelectItem100, pSelectItem75, pSelectItem50, pSelectItem25, pSelectItem10;
    @FXML
    private VBox pSelectList_1, pSelectChild_1;
    @FXML
    private GridPane pSelectHead_1, pSelectItem100_1, pSelectItem75_1, pSelectItem50_1, pSelectItem25_1, pSelectItem10_1;
    @FXML
    private TextField tab1AmountTextField, tab2AmountTextField, tab1GasLimitTextField, tab2GasLimitTextField, tab2ReadWriteTextField1, tab2ReadWriteTextField2;
    @FXML
    private GridPane tab1SolidityTextGrid, codeTab1, codeTab2;
    private ApisCodeArea tab1SolidityTextArea1 = new ApisCodeArea();
    @FXML
    private TextFlow tab1SolidityTextArea2;
    @FXML
    private TextArea tab1SolidityTextArea3;
    @FXML
    private ProgressBar tab1ProgressBar, tab2ProgressBar;
    @FXML
    private Slider tab1Slider, tab2Slider;

    // Multilingual Support Label
    @FXML
    private Label tabTitle, selectWallet, amountToSend, amountTotal, textareaMessage, gasPriceTitle, gasPriceFormula, gasPriceLabel, gasLimitLabel, detailLabel,
                  detailContentsFee, detailContentsTotal, tab1LowLabel, tab1HighLabel, transferAmountTitle, detailLabel1, transferAmountLabel, gasPriceReceipt,
                  totalWithdrawal, afterBalance, transferAmountDesc1, transferAmountDesc2, transferBtnLabel, selectContract, selectWallet1, amountToSend1, amountTotal1,
                  readWriteContract, gasPriceTitle1, gasPriceFormula1, gasPriceLabel1, gasLimitLabel1, detailLabel2, detailContentsFee1, detailContentsTotal1,
                  tab2LowLabel, tab2HighLabel;
    // Number Label
    @FXML
    private Label amountToSendNature, amountToSendDecimal, amountToSendNature1, amountToSendDecimal1, detailContentsFeeNum, detailContentsTotalNum, detailContentsFeeNum1,
                  detailContentsTotalNum1, transferAmountTitleNature, transferAmountTitleDecimal, transferAmountLabelNature, transferAmountLabelDecimal,
                  gasPriceReceiptNature, gasPriceReceiptDecimal, totalWithdrawalNature, totalWithdrawalDecimal, afterBalanceNature, afterBalanceDecimal;

    @FXML
    private ApisSelectBoxController walletSelectorController, walletSelector_1Controller;

    // Contract TextArea
    @FXML private ScrollPane contractInputView;
    @FXML private ComboBox contractCombo;
    @FXML private VBox contractMethodList;


    private Image downGrey, downWhite;
    // Percentage Select Box Lists
    private ArrayList<VBox> pSelectLists = new ArrayList<>();
    private ArrayList<VBox> pSelectChildList = new ArrayList<>();
    private ArrayList<GridPane> pSelectHeadList = new ArrayList<>();
    private ArrayList<Label> pSelectHeadTextList = new ArrayList<>();
    private ArrayList<TextField> pAmountTextFieldList = new ArrayList<>();
    private ArrayList<ApisSelectBoxController> pWalletSelectorList = new ArrayList<>();
    private ArrayList<GridPane> pSelectItem100List = new ArrayList<>();
    private ArrayList<GridPane> pSelectItem75List = new ArrayList<>();
    private ArrayList<GridPane> pSelectItem50List = new ArrayList<>();
    private ArrayList<GridPane> pSelectItem25List = new ArrayList<>();
    private ArrayList<GridPane> pSelectItem10List = new ArrayList<>();
    // Gas Price Slider Lists
    private ArrayList<GridPane> gasPriceGridList = new ArrayList<>();
    private ArrayList<GridPane> gasPricePopupGridList = new ArrayList<>();
    private ArrayList<Label> gasPricePlusMinusLabelList = new ArrayList<>();
    private ArrayList<Label> gasPricePopupLabelList = new ArrayList<>();
    private ArrayList<Label> gasPricePopupDefaultLabelList = new ArrayList<>();
    private ArrayList<ImageView> gasPriceMinusBtnList = new ArrayList<>();
    private ArrayList<ImageView> gasPricePlusBtnList = new ArrayList<>();
    private ArrayList<ImageView> gasPricePopupImgList = new ArrayList<>();
    private ArrayList<Slider> gasPriceSliderList = new ArrayList<>();


    // 컨트렉트 객체
    private CompilationResult res;
    private CompilationResult.ContractMetadata metadata;
    private ArrayList<Object> contractParams = new ArrayList<>();
    private Thread autoCompileThread;
    private long minGasLimit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppManager.getInstance().guiFx.setSmartContract(this);
        settingLayoutData();
        // Multilingual Support
        languageSetting();
        cSelectItemDefaultText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                initContract();
            }
        });

        initTabClean();
        initSideTabClean();

        this.tab1LeftPane.setVisible(true);
        this.tab1RightPane.setVisible(true);
        this.transferBtn.setVisible(true);
        this.tabLabel1.setTextFill(Color.web("#910000"));
        this.tabLabel1.setStyle("-fx-font-family: 'Open Sans SemiBold'; -fx-font-size:11px;");
        this.tabLinePane1.setVisible(true);
        this.sideTabLabel1.setTextFill(Color.web("#910000"));
        this.sideTabLabel1.setStyle("-fx-font-family: 'Open Sans SemiBold'; -fx-font-size:12px;");
        this.sideTabLinePane1.setVisible(true);

        // Image Setting
        downGrey = new Image("image/ic_down_gray@2x.png");
        downWhite = new Image("image/ic_down_white@2x.png");

        // Percentage Select Box List Handling
        pSelectLists.add(pSelectList);
        pSelectChildList.add(pSelectChild);
        pSelectHeadList.add(pSelectHead);
        pSelectHeadTextList.add(pSelectHeadText);
        pAmountTextFieldList.add(tab1AmountTextField);
        pWalletSelectorList.add(walletSelectorController);
        pSelectItem100List.add(pSelectItem100);
        pSelectItem75List.add(pSelectItem75);
        pSelectItem50List.add(pSelectItem50);
        pSelectItem25List.add(pSelectItem25);
        pSelectItem10List.add(pSelectItem10);

        walletSelectorController.setHandler(new ApisSelectBoxController.ApisSelectBoxImpl() {
            @Override
            public void onSelectItem() {
                settingLayoutData();
            }

            @Override
            public void onMouseClick() {

            }
        });

        pSelectLists.add(pSelectList_1);
        pSelectChildList.add(pSelectChild_1);
        pSelectHeadList.add(pSelectHead_1);
        pSelectHeadTextList.add(pSelectHeadText_1);
        pAmountTextFieldList.add(tab2AmountTextField);
        pWalletSelectorList.add(walletSelector_1Controller);
        pSelectItem100List.add(pSelectItem100_1);
        pSelectItem75List.add(pSelectItem75_1);
        pSelectItem50List.add(pSelectItem50_1);
        pSelectItem25List.add(pSelectItem25_1);
        pSelectItem10List.add(pSelectItem10_1);

        walletSelector_1Controller.setHandler(new ApisSelectBoxController.ApisSelectBoxImpl() {
            @Override
            public void onSelectItem() {
                settingLayoutData();
            }

            @Override
            public void onMouseClick() {

            }
        });

        hidePercentSelectBox(0);
        hidePercentSelectBox(1);

        // Contract Read and Write Select Box List Handling
        initContract();
        hideContractSelectBox();

        // Focused
        tab1AmountTextField.focusedProperty().addListener(tab1AmountListener);
        tab1GasLimitTextField.focusedProperty().addListener(tab1GasLimitListener);
        tab2AmountTextField.focusedProperty().addListener(tab2AmountListener);
        tab2GasLimitTextField.focusedProperty().addListener(tab2GasLimitListener);

        // Input
        tab1AmountTextField.textProperty().addListener(tab1AmountTextListener);
        tab1GasLimitTextField.textProperty().addListener(tab1GasLimitTextListener);

        // Progress Bar and Slider Handling
        tab1Slider.valueProperty().addListener(tab1SliderListener);
        tab2Slider.valueProperty().addListener(tab2SliderListener);

        gasPriceGridList.add(tab1GasPriceGrid);
        gasPricePopupGridList.add(tab1GasPricePopupGrid);
        gasPricePopupImgList.add(tab1GasPricePopupImg);
        gasPricePlusMinusLabelList.add(tab1GasPricePlusMinusLabel);
        gasPricePopupLabelList.add(tab1GasPricePopupLabel);
        gasPricePopupDefaultLabelList.add(tab1GasPricePopupLabel);
        gasPriceMinusBtnList.add(tab1GasPriceMinusBtn);
        gasPricePlusBtnList.add(tab1GasPricePlusBtn);
        gasPriceSliderList.add(tab1Slider);

        gasPriceGridList.add(tab2GasPriceGrid);
        gasPricePopupGridList.add(tab2GasPricePopupGrid);
        gasPricePopupImgList.add(tab2GasPricePopupImg);
        gasPricePlusMinusLabelList.add(tab2GasPricePlusMinusLabel);
        gasPricePopupLabelList.add(tab2GasPricePopupLabel);
        gasPricePopupDefaultLabelList.add(tab2GasPricePopupLabel);
        gasPriceMinusBtnList.add(tab2GasPriceMinusBtn);
        gasPricePlusBtnList.add(tab2GasPricePlusBtn);
        gasPriceSliderList.add(tab2Slider);

        for(int i=0; i<gasPriceGridList.size(); i++){
            this.gasPricePopupLabelList.get(i).textProperty().bind(gasPricePlusMinusLabelList.get(i).textProperty());
            this.gasPricePlusMinusLabelList.get(i).textProperty().set(gasPrice+" nAPIS");
            hideGasPricePopup(i);
        }

        // Text Area Listener
        tab1SolidityTextArea1.focusedProperty().addListener(tab1TextAreaListener);
        tab1SolidityTextArea1.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                autoCompileThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Thread thread = Thread.currentThread();
                        int count = 0;
                        while(autoCompileThread == thread){
                            try {
                                thread.sleep(1000);
                                count++;
                                if(count == 5){ // 5초 카운트 이후, 컴파일
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            startToCompile();
                                        }
                                    });
                                    break;
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                autoCompileThread.start();

                event.consume();
            }
        });

        tab1SolidityTextGrid.add(tab1SolidityTextArea1,0,0);

        contractCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue != null) {
                    createContractFieldInMethodList(newValue.toString());
                }
            }
        });

    }

    public void languageSetting() {
        tabTitle.textProperty().bind(StringManager.getInstance().smartContract.tabTitle);
        tabLabel1.textProperty().bind(StringManager.getInstance().smartContract.tabLabel1);
        tabLabel2.textProperty().bind(StringManager.getInstance().smartContract.tabLabel2);
        tabLabel3.textProperty().bind(StringManager.getInstance().smartContract.tabLabel3);
        selectWallet.textProperty().bind(StringManager.getInstance().smartContract.selectWallet);
        amountToSend.textProperty().bind(StringManager.getInstance().smartContract.amountToSend);
        amountTotal.textProperty().bind(StringManager.getInstance().smartContract.amountTotal);
        sideTabLabel1.textProperty().bind(StringManager.getInstance().smartContract.sideTabLabel1);
        sideTabLabel2.textProperty().bind(StringManager.getInstance().smartContract.sideTabLabel2);
        textareaMessage.textProperty().bind(StringManager.getInstance().smartContract.textareaMessage);

        gasPriceTitle.textProperty().bind(StringManager.getInstance().smartContract.gasPriceTitle);
        gasPriceFormula.textProperty().bind(StringManager.getInstance().smartContract.gasPriceFormula);
        gasPriceLabel.textProperty().bind(StringManager.getInstance().smartContract.gasPriceLabel);
        gasLimitLabel.textProperty().bind(StringManager.getInstance().smartContract.gasLimitLabel);
        detailLabel.textProperty().bind(StringManager.getInstance().smartContract.detailLabel);
        detailContentsFee.textProperty().bind(StringManager.getInstance().smartContract.detailContentsFee);
        detailContentsTotal.textProperty().bind(StringManager.getInstance().smartContract.detailContentsTotal);
        tab1GasPricePopupDefaultLabel.textProperty().bind(StringManager.getInstance().smartContract.tab1DefaultLabel);
        tab1LowLabel.textProperty().bind(StringManager.getInstance().smartContract.tab1LowLabel);
        tab1HighLabel.textProperty().bind(StringManager.getInstance().smartContract.tab1HighLabel);
        transferAmountTitle.textProperty().bind(StringManager.getInstance().smartContract.transferAmountLabel);
        detailLabel1.textProperty().bind(StringManager.getInstance().smartContract.detailLabel);
        transferAmountLabel.textProperty().bind(StringManager.getInstance().smartContract.transferAmountLabel);
        gasPriceReceipt.textProperty().bind(StringManager.getInstance().smartContract.gasPriceReceipt);
        totalWithdrawal.textProperty().bind(StringManager.getInstance().smartContract.totalWithdrawal);
        afterBalance.textProperty().bind(StringManager.getInstance().smartContract.afterBalance);
        transferAmountDesc1.textProperty().bind(StringManager.getInstance().smartContract.transferAmountDesc1);
        transferAmountDesc2.textProperty().bind(StringManager.getInstance().smartContract.transferAmountDesc2);
        transferBtnLabel.textProperty().bind(StringManager.getInstance().smartContract.transferBtnLabel);
        selectContract.textProperty().bind(StringManager.getInstance().smartContract.selectContract);
        selectWallet1.textProperty().bind(StringManager.getInstance().smartContract.selectWallet1);
        amountToSend1.textProperty().bind(StringManager.getInstance().smartContract.amountToSend);
        amountTotal1.textProperty().bind(StringManager.getInstance().smartContract.amountTotal);
        readWriteContract.textProperty().bind(StringManager.getInstance().smartContract.readWriteContract);
        cSelectItemDefaultText.textProperty().bind(StringManager.getInstance().smartContract.selectDefaultText);
        gasPriceTitle1.textProperty().bind(StringManager.getInstance().smartContract.gasPriceTitle);
        gasPriceFormula1.textProperty().bind(StringManager.getInstance().smartContract.gasPriceFormula);
        gasPriceLabel1.textProperty().bind(StringManager.getInstance().smartContract.gasPriceLabel);
        gasLimitLabel1.textProperty().bind(StringManager.getInstance().smartContract.gasLimitLabel);
        detailLabel2.textProperty().bind(StringManager.getInstance().smartContract.detailLabel);
        detailContentsFee1.textProperty().bind(StringManager.getInstance().smartContract.detailContentsFee);
        detailContentsTotal1.textProperty().bind(StringManager.getInstance().smartContract.detailContentsTotal);
        tab2GasPricePopupDefaultLabel.textProperty().bind(StringManager.getInstance().smartContract.tab1DefaultLabel);
        tab2LowLabel.textProperty().bind(StringManager.getInstance().smartContract.tab1LowLabel);
        tab2HighLabel.textProperty().bind(StringManager.getInstance().smartContract.tab1HighLabel);
    }

    private ChangeListener<Boolean> tab1AmountListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

            String sAmount = tab1AmountTextField.getText();
            String[] amountSplit = sAmount.split("\\.");
            if(sAmount != null && !sAmount.equals("")){
                if(amountSplit.length == 0){
                    sAmount = "0.000000000000000000";
                }else if(amountSplit.length == 1){
                    sAmount = sAmount.replace(".","") + ".000000000000000000";
                }else{
                    String decimal = amountSplit[1];
                    for(int i=0; i<18 - amountSplit[1].length(); i++){
                        decimal = decimal + "0";
                    }
                    amountSplit[1] = decimal;
                    sAmount = amountSplit[0] + "." + amountSplit[1];
                }
                tab1AmountTextField.textProperty().setValue(sAmount);
            }

            textFieldFocus();
        }
    };

    private ChangeListener<Boolean> tab1GasLimitListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            textFieldFocus();
            if(newValue != null){
                String gasLimit = tab1GasLimitTextField.getText();
                if(gasLimit.length() > 0 && minGasLimit > Long.parseLong(gasLimit)){
                    tab1GasLimitTextField.setText(""+minGasLimit);
                }
            }
        }
    };

    private ChangeListener<Boolean> tab2AmountListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            textFieldFocus();
        }
    };

    private ChangeListener<Boolean> tab2GasLimitListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            textFieldFocus();
        }
    };

    private ChangeListener<String> tab1AmountTextListener = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (!newValue.matches("[\\d.]*")) {
                tab1AmountTextField.setText(newValue.replaceAll("[^\\d.]", ""));
            }
        }
    };

    private ChangeListener<String> tab1GasLimitTextListener = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (!newValue.matches("[\\d]*")) {
                tab1GasLimitTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        }
    };

    private ChangeListener<Number> tab1SliderListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            tab1ProgressBar.setProgress((newValue.doubleValue()-tab1Slider.getMin()) / (tab1Slider.getMax()-tab1Slider.getMin()));

            gasPrice = new BigInteger(""+newValue.intValue());
            tab1GasPricePlusMinusLabel.textProperty().set(gasPrice.toString()+" nAPIS");
            if(newValue.intValue() != 50) {
                tab1GasPricePopupDefaultLabel.setVisible(false);
            } else {
                tab1GasPricePopupDefaultLabel.setVisible(true);
            }
            settingLayoutData();
        }
    };

    private ChangeListener<Number> tab2SliderListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            tab2ProgressBar.setProgress((newValue.doubleValue()-tab2Slider.getMin()) / (tab2Slider.getMax()-tab2Slider.getMin()));

            gasPrice = new BigInteger(""+newValue.intValue());
            tab2GasPricePlusMinusLabel.textProperty().set(gasPrice.toString()+" nAPIS");
            if(newValue.intValue() != 50) {
                tab2GasPricePopupDefaultLabel.setVisible(false);
            } else {
                tab2GasPricePopupDefaultLabel.setVisible(true);
            }
            settingLayoutData();
        }
    };

    private ChangeListener<Boolean> tab1TextAreaListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if(newValue) {
                hideGasPricePopupAll();
            }
        }
    };

    public void textFieldFocus() {
        hideGasPricePopupAll();

        if(tab1AmountTextField.isFocused()) {
            tab1AmountPane.setStyle("-fx-background-color: #ffffff; -fx-border-color: #999999; -fx-border-radius : 4 4 4 4; -fx-background-radius: 4 4 4 4;");
        } else {
            tab1AmountPane.setStyle("-fx-background-color: #f2f2f2; -fx-border-color: #d8d8d8; -fx-border-radius : 4 4 4 4; -fx-background-radius: 4 4 4 4;");
            settingLayoutData();
        }

        if(tab1GasLimitTextField.isFocused()) {
            tab1GasLimitTextField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #999999; -fx-border-radius : 4 4 4 4; -fx-background-radius: 4 4 4 4;" +
                    " -fx-font-family: 'Open Sans SemiBold'; -fx-font-size:12px;");
        } else {
            tab1GasLimitTextField.setStyle("-fx-background-color: #f2f2f2; -fx-border-color: #d8d8d8; -fx-border-radius : 4 4 4 4; -fx-background-radius: 4 4 4 4;" +
                    " -fx-font-family: 'Open Sans SemiBold'; -fx-font-size:12px;");
            settingLayoutData();
        }

        if(tab2AmountTextField.isFocused()) {
            tab2AmountPane.setStyle("-fx-background-color: #ffffff; -fx-border-color: #999999; -fx-border-radius : 4 4 4 4; -fx-background-radius: 4 4 4 4;");
        } else {
            tab2AmountPane.setStyle("-fx-background-color: #f2f2f2; -fx-border-color: #d8d8d8; -fx-border-radius : 4 4 4 4; -fx-background-radius: 4 4 4 4;");
            settingLayoutData();
        }

        if(tab2GasLimitTextField.isFocused()) {
            tab2GasLimitTextField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #999999; -fx-border-radius : 4 4 4 4; -fx-background-radius: 4 4 4 4;" +
                    " -fx-font-family: 'Open Sans SemiBold'; -fx-font-size:12px;");
        } else {
            tab2GasLimitTextField.setStyle("-fx-background-color: #f2f2f2; -fx-border-color: #d8d8d8; -fx-border-radius : 4 4 4 4; -fx-background-radius: 4 4 4 4;" +
                    " -fx-font-family: 'Open Sans SemiBold'; -fx-font-size:12px;");
            settingLayoutData();
        }
    }

    @FXML
    public void contractDeployPopup() {
        if(checkTransferButton()){
            String address = this.walletSelectorController.getAddress();
            String balance = this.tab1AmountTextField.getText().replace(".","");
            String gasPrice = new BigInteger(""+(int)tab1Slider.getValue()).multiply(new BigInteger("1000000000")).toString();
            String gasLimit = this.tab1GasLimitTextField.getText();
            String contractName = (String)this.contractCombo.getSelectionModel().getSelectedItem();
            System.out.println("contractName : "+contractName);
            PopupContractWarningController controller = (PopupContractWarningController) AppManager.getInstance().guiFx.showMainPopup("popup_contract_warning.fxml", 0);
            controller.setData(address, balance, gasPrice, gasLimit, metadata, contractName, contractParams);
        }
    }
    @FXML
    public void contractCallSendPopup(){
        String contractAddress = "bc6c9fd2bf07c05a6aae2d6469e88dd8762acaa2"; //컨트렉트 주소
        String medataAbi = "[{\"constant\":false,\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"n\",\"type\":\"int256\"}],\"name\":\"add\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"n\",\"type\":\"int256\"}],\"name\":\"sub\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"n\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";

        // 데이터 불러오기
        CallTransaction.Contract contract = new CallTransaction.Contract(medataAbi);
        System.out.println(" callConstantFunction : "+AppManager.getInstance().callConstantFunction(contractAddress, contract.getByName("get")));


        //CallTransaction.Function add = contract.getByName("add");
        //byte[] functionCallBytes = add.encode(5);
        CallTransaction.Function sub = contract.getByName("sub");
        byte[] functionCallBytes = sub.encode(7);

        String address = "9c8766a4be4830812acf0eebab34e4801e276d41";
        String password = "aaaa";
        String gasPrice = "300000000";
        String gasLimit = "200000";
        Transaction tx = AppManager.getInstance().ethereumGenerateTransaction(address,"0",gasPrice, gasLimit, Hex.decode(contractAddress), password, functionCallBytes);
        System.out.println("tx.getHash() : "+Hex.toHexString(tx.getHash()));

    }

    @FXML
    public void contractSelectPopup(){
        PopupContractReadWriteSelectController controller = (PopupContractReadWriteSelectController)AppManager.getInstance().guiFx.showMainPopup("popup_contract_read_write_select.fxml", 0);
        controller.setHandler(new PopupContractReadWriteSelectController.PopupContractReadWriteSelectImpl() {
            @Override
            public void onClickSelect(ContractModel model) {
                aliasLabel.setText(model.getName());
                addressLabel.setText(model.getAddress());
                placeholderLabel.setVisible(false);
            }
        });
    }

    @FXML
    public void startToCompile(){
        res = null;
        this.tab1SolidityTextArea2.getChildren().clear();

        String contract = this.tab1SolidityTextArea1.getText();
        if(contract == null || contract.length() <= 0){
            return;
        }
// 컴파일에 성공하면 json 스트링을 반환한다.
        String message = AppManager.getInstance().ethereumSmartContractStartToCompile(contract);
        if(message != null && message.length() > 0 && AppManager.isJSONValid(message)){
            try {
                textareaMessage.setVisible(false);
                contractInputView.setVisible(true);

                res = CompilationResult.parse(message);

                // 컨트렉트 이름 파싱
                // <stdin>:testContract
                ArrayList<String> contractList = new ArrayList<>();
                String[] splitKey = null;
                for(int i=0; i<res.getContractKeys().size(); i++){
                    splitKey = res.getContractKeys().get(i).split(":");
                    if(splitKey.length > 1){
                        contractList.add(splitKey[1]);
                    }
                }


                // 컨트렉트 등록
                ObservableList list = FXCollections.observableList(contractList);
                if(list.size() > 0){
                    this.contractCombo.getItems().clear();
                    this.contractCombo.setItems(list);

                    // 첫번째 아이템 선택
                    this.contractCombo.getSelectionModel().select(0);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
// 컴파일에 실패할 경우
        }else{
            textareaMessage.setVisible(false);
            contractInputView.setVisible(false);

            String[] tempSplit = message.split("<stdin>:");
            for(int i=0; i<tempSplit.length; i++){
                System.out.println("["+(i+1)+"] : "+tempSplit[i]);
                Text text  = new Text(tempSplit[i]);
                text.setFont(Font.font("Roboto Mono", 10));

                if(tempSplit[i].indexOf("Warning") >= 0){
                    text.setFill(Color.rgb(221, 83 , 23));
                }else if(tempSplit[i].indexOf("Error") >= 0){
                    text.setFill(Color.rgb(145, 0 , 0));
                }else {
                    text.setFill(Color.rgb(66, 133 , 244));
                }

                this.tab1SolidityTextArea2.getChildren().add(text);
            }
        }

        checkTransferButton();
    }


    @FXML
    private void onMouseClicked(InputEvent event) {
        String fxid = ((Node)event.getSource()).getId();

        if(fxid.equals("tab1")) {
            initTab(0);

        } else if(fxid.equals("tab2")) {
            initTab(1);

        } else if(fxid.equals("tab3")) {
            initTab(2);

        } else if(fxid.equals("sideTab1")) {
            initSideTab(0);

        } else if(fxid.equals("sideTab2")) {
            initSideTab(1);

        }

        // Amount Percentage Select Box
        String tempId = null;
        for(int i=0; i<pSelectHeadList.size(); i++){

            // header
            tempId = (i == 0) ? "pSelectHead" : "pSelectHead_"+i;
            if(fxid.equals(tempId)){
                if(this.pSelectLists.get(i).isVisible() == true) {
                    hidePercentSelectBox(i);
                } else {
                    showPercentSelectBox(i);
                }
            }

            // 100
            tempId = (i == 0) ? "pSelectItem100" : "pSelectItem100_"+i;
            if(fxid.equals(tempId)){
                this.pSelectHeadTextList.get(i).textProperty().setValue("100%");
                String sBalance = pWalletSelectorList.get(i).getBalance();
                BigInteger balance = new BigInteger(sBalance).multiply(new BigInteger("100")).divide(new BigInteger("100"));
                pAmountTextFieldList.get(i).textProperty().setValue(AppManager.addDotWidthIndex(balance.toString()));
                this.pSelectHeadList.get(i).setStyle("-fx-border-radius : 0 4 4 0; -fx-background-radius: 0 4 4 0; -fx-background-color:#910000;");
                hidePercentSelectBox(i);
                settingLayoutData();
            }

            // 75
            tempId = (i == 0) ? "pSelectItem75" : "pSelectItem75_"+i;
            if(fxid.equals(tempId)){
                this.pSelectHeadTextList.get(i).textProperty().setValue("75%");
                String sBalance = pWalletSelectorList.get(i).getBalance();
                BigInteger balance = new BigInteger(sBalance).multiply(new BigInteger("75")).divide(new BigInteger("100"));
                pAmountTextFieldList.get(i).textProperty().setValue(AppManager.addDotWidthIndex(balance.toString()));
                this.pSelectHeadList.get(i).setStyle("-fx-border-radius : 0 4 4 0; -fx-background-radius: 0 4 4 0; -fx-background-color:#910000;");
                hidePercentSelectBox(i);
                settingLayoutData();
            }

            // 50
            tempId = (i == 0) ? "pSelectItem50" : "pSelectItem50_"+i;
            if(fxid.equals(tempId)){
                this.pSelectHeadTextList.get(i).textProperty().setValue("50%");
                String sBalance = pWalletSelectorList.get(i).getBalance();
                BigInteger balance = new BigInteger(sBalance).multiply(new BigInteger("50")).divide(new BigInteger("100"));
                pAmountTextFieldList.get(i).textProperty().setValue(AppManager.addDotWidthIndex(balance.toString()));
                this.pSelectHeadList.get(i).setStyle("-fx-border-radius : 0 4 4 0; -fx-background-radius: 0 4 4 0; -fx-background-color:#910000;");
                hidePercentSelectBox(i);
                settingLayoutData();
            }

            // 25
            tempId = (i == 0) ? "pSelectItem25" : "pSelectItem25_"+i;
            if(fxid.equals(tempId)){
                this.pSelectHeadTextList.get(i).textProperty().setValue("25%");
                String sBalance = pWalletSelectorList.get(i).getBalance();
                BigInteger balance = new BigInteger(sBalance).multiply(new BigInteger("25")).divide(new BigInteger("100"));
                pAmountTextFieldList.get(i).textProperty().setValue(AppManager.addDotWidthIndex(balance.toString()));
                this.pSelectHeadList.get(i).setStyle("-fx-border-radius : 0 4 4 0; -fx-background-radius: 0 4 4 0; -fx-background-color:#910000;");
                hidePercentSelectBox(i);
                settingLayoutData();
            }

            // 10
            tempId = (i == 0) ? "pSelectItem10" : "pSelectItem10_"+i;
            if(fxid.equals(tempId)){
                this.pSelectHeadTextList.get(i).textProperty().setValue("10%");
                String sBalance = pWalletSelectorList.get(i).getBalance();
                BigInteger balance = new BigInteger(sBalance).multiply(new BigInteger("10")).divide(new BigInteger("100"));
                pAmountTextFieldList.get(i).textProperty().setValue(AppManager.addDotWidthIndex(balance.toString()));
                this.pSelectHeadList.get(i).setStyle("-fx-border-radius : 0 4 4 0; -fx-background-radius: 0 4 4 0; -fx-background-color:#910000;");
                hidePercentSelectBox(i);
                settingLayoutData();
            }
        }

        // Contract Read and Write Select Box
        if(fxid.equals("cSelectHead")) {
            if(this.cSelectList.isVisible() == true) {
                hideContractSelectBox();
            } else {
                showContractSelectBox();
            }
        } else if(fxid.equals("cSelectItemDefault")) {
            initContract();
            hideContractSelectBox();
        } else if(fxid.equals("cSelectItemBalance")) {
            cSelectHead.setStyle("-fx-background-color: #f2f2f2; -fx-border-color: d8d8d8; -fx-border-radius : 4 4 4 4; -fx-background-radius: 4 4 4 4;");
            cSelectHeadText.setText(cSelectItemBalanceText.getText());
            cSelectHeadText.setTextFill(Color.web("#999999"));
            cSelectHeadImg.setImage(downGrey);
            tab2ReadWritePane.setVisible(true);
            tab2ReadWritePane.prefHeightProperty().setValue(-1);

            hideContractSelectBox();
        }

        tempId = null;
        // Gas Price
        for(int i=0; i<gasPriceGridList.size(); i++) {
            tempId = "tab"+(i+1)+"GasPricePlusMinusPane";
            if(fxid.equals(tempId)) {
                if (!gasPricePopupGridList.get(i).isVisible()) {
                    showGasPricePopup(i);
                    gasPriceSliderList.get(i).requestFocus();
                    event.consume();
                }

            } else if(fxid.equals("tab"+(i+1)+"GasPriceMinusBtn")) {
                gasPriceSliderList.get(i).setValue(gasPriceSliderList.get(i).getValue()-10);
                gasPriceSliderList.get(i).requestFocus();
                event.consume();

            } else if(fxid.equals("tab"+(i+1)+"GasPricePlusBtn")) {
                gasPriceSliderList.get(i).setValue(gasPriceSliderList.get(i).getValue()+10);
                gasPriceSliderList.get(i).requestFocus();
                event.consume();

            }

            if(fxid.equals("contractHomePane")) {
                if(gasPricePopupGridList.get(i).isVisible()) {
                    if (!gasPricePopupFlag) {
                        hideGasPricePopupAll();
                    } else {
                        gasPriceSliderList.get(i).requestFocus();
                    }
                }
            }

        }

    }

    @FXML
    private void onMouseEntered(InputEvent event){
        String id = ((Node)event.getSource()).getId();

        // Amount Percentage Select Box
        for(int i=0; i<pSelectHeadList.size(); i++){
            if(id.equals((i == 0) ? "pSelectItem100" : "pSelectItem100_"+i)){
                pSelectItem100List.get(i).setStyle("-fx-background-color : #f2f2f2");

            }else if(id.equals((i == 0) ? "pSelectItem75" : "pSelectItem75_"+i)){
                pSelectItem75List.get(i).setStyle("-fx-background-color : #f2f2f2");

            }else if(id.equals((i == 0) ? "pSelectItem50" : "pSelectItem50_"+i)){
                pSelectItem50List.get(i).setStyle("-fx-background-color : #f2f2f2");

            }else if(id.equals((i == 0) ? "pSelectItem25" : "pSelectItem25_"+i)){
                pSelectItem25List.get(i).setStyle("-fx-background-color : #f2f2f2");

            }else if(id.equals((i == 0) ? "pSelectItem10" : "pSelectItem10_"+i)){
                pSelectItem10List.get(i).setStyle("-fx-background-color : #f2f2f2");

            }
        }

        // Contract Read and Write Select Box
        if(id.equals("cSelectItemDefault")) {
            cSelectItemDefault.setStyle("-fx-background-color: #f2f2f2;");
        } else if(id.equals("cSelectItemBalance")) {
            cSelectItemBalance.setStyle("-fx-background-color: #f2f2f2;");
        }

        // Gas Price Popup
        for(int i=0; i<gasPriceGridList.size(); i++) {
            if (id.equals("tab"+(i+1)+"GasPricePopupGrid")) {
                gasPricePopupFlag = GAS_PRICE_POPUP_MOUSE_ENTERED;
            }
        }
    }

    @FXML
    private void onMouseExited(InputEvent event){
        String id = ((Node)event.getSource()).getId();

        // Amount Percentage Select Box
        for(int i=0; i<pSelectHeadList.size(); i++){
            if(id.equals((i == 0) ? "pSelectItem100" : "pSelectItem100_"+i)){
                pSelectItem100List.get(i).setStyle("-fx-background-color : #ffffff");

            }else if(id.equals((i == 0) ? "pSelectItem75" : "pSelectItem75_"+i)){
                pSelectItem75List.get(i).setStyle("-fx-background-color : #ffffff");

            }else if(id.equals((i == 0) ? "pSelectItem50" : "pSelectItem50_"+i)){
                pSelectItem50List.get(i).setStyle("-fx-background-color : #ffffff");

            }else if(id.equals((i == 0) ? "pSelectItem25" : "pSelectItem25_"+i)){
                pSelectItem25List.get(i).setStyle("-fx-background-color : #ffffff");

            }else if(id.equals((i == 0) ? "pSelectItem10" : "pSelectItem10_"+i)){
                pSelectItem10List.get(i).setStyle("-fx-background-color : #ffffff");

            }
        }

        // Contract Read and Write Select Box
        if(id.equals("cSelectItemDefault")) {
            cSelectItemDefault.setStyle("-fx-background-color: #ffffff;");
        } else if(id.equals("cSelectItemBalance")) {
            cSelectItemBalance.setStyle("-fx-background-color: #ffffff;");
        }

        // Gas Price Popup
        for(int i=0; i<gasPriceGridList.size(); i++) {
            if (id.equals("tab"+(i+1)+"GasPricePopupGrid")) {
                gasPricePopupFlag = GAS_PRICE_POPUP_MOUSE_EXITED;
            }
        }
    }

    public void update(){
        for(int i=0; i<pWalletSelectorList.size(); i++) {
            pWalletSelectorList.get(i).update();
            pWalletSelectorList.get(i).setStage(ApisSelectBoxController.STAGE_DEFAULT);
        }
        settingLayoutData();
    }

    // 화면 초기
    private void initLayoutData(){
        // 지갑선택
        for(int i=0; i<pWalletSelectorList.size(); i++){
            pWalletSelectorList.get(i).selectedItem(0);
        }

        // Amount 텍스트 필드
        for(int i=0; i<pAmountTextFieldList.size(); i++){
            pAmountTextFieldList.get(i).textProperty().set("");
        }

        // Contract Editor
        textareaMessage.setVisible(true);
        contractInputView.setVisible(false);
        contractMethodList.getChildren().clear();

        //
        tab1Slider.setValue(tab1Slider.getMin());

        tab1GasLimitTextField.textProperty().set("");

        settingLayoutData();
    }

    public void settingLayoutData(){
        for(int i=0; i<pWalletSelectorList.size(); i++) {
            String sBalance = pWalletSelectorList.get(i).getBalance();
            String[] balanceSplit = AppManager.addDotWidthIndex(sBalance).split("\\.");

            // amount to send
            String sAmount = pAmountTextFieldList.get(i).getText();
            sAmount = (sAmount != null && !sAmount.equals("")) ? sAmount : AppManager.addDotWidthIndex("0");
            String[] amountSplit = sAmount.split("\\.");

            // fee
            BigInteger gasLimit = null;
            if(i == 0) {
                if (tab1GasLimitTextField.getText() != null && tab1GasLimitTextField.getText().length() > 0) {
                    gasLimit = new BigInteger(tab1GasLimitTextField.getText());
                } else {
                    gasLimit = new BigInteger("0");
                }
            } else {
                if (tab2GasLimitTextField.getText() != null && tab2GasLimitTextField.getText().length() > 0) {
                    gasLimit = new BigInteger(tab2GasLimitTextField.getText());
                } else {
                    gasLimit = new BigInteger("0");
                }
            }
            BigInteger fee = gasPrice.multiply(new BigInteger("1000000000")).multiply(gasLimit);
            String[] feeSplit = AppManager.addDotWidthIndex(fee.toString()).split("\\.");

            // mineral
            String sMineral = pWalletSelectorList.get(i).getMineral();
            String[] mineralSplit = AppManager.addDotWidthIndex(sMineral).split("\\.");
            BigInteger mineral = new BigInteger(sMineral);

            // gas
            BigInteger gas = fee.subtract(mineral);
            gas = (gas.compareTo(new BigInteger("0")) > 0) ? gas : new BigInteger("0");
            String[] gasSplit = AppManager.addDotWidthIndex(gas.toString()).split("\\.");

            // total amount
            BigInteger totalAmount = new BigInteger(sAmount.replace(".","")).add(gas);
            String[] totalAmountSplit = AppManager.addDotWidthIndex(totalAmount.toString()).split("\\.");

            //after balance
            BigInteger afterBalance = new BigInteger(pWalletSelectorList.get(i).getBalance()).subtract(totalAmount);
            afterBalance = (afterBalance.compareTo(new BigInteger("0")) >=0 ) ? afterBalance : new BigInteger("0");
            String[] afterBalanceSplit = AppManager.addDotWidthIndex(afterBalance.toString()).split("\\.");

            if(i == 0) {
                amountToSendNature.textProperty().setValue(AppManager.comma(balanceSplit[0]));
                amountToSendDecimal.textProperty().setValue("." + balanceSplit[1]);

                detailContentsFeeNum.textProperty().setValue(AppManager.comma(feeSplit[0]) + "." + feeSplit[1]);
                detailContentsTotalNum.textProperty().setValue(AppManager.comma(mineralSplit[0]) + "." + mineralSplit[1]);

            } else {
                amountToSendNature1.textProperty().setValue(AppManager.comma(balanceSplit[0]));
                amountToSendDecimal1.textProperty().setValue("." + balanceSplit[1]);

                detailContentsFeeNum1.textProperty().setValue(AppManager.comma(feeSplit[0]) + "." + feeSplit[1]);
                detailContentsTotalNum1.textProperty().setValue(AppManager.comma(mineralSplit[0]) + "." + mineralSplit[1]);
            }

            if(selectedTabIndex == i) {
                transferAmountTitleNature.textProperty().setValue(AppManager.comma(totalAmountSplit[0]));
                transferAmountTitleDecimal.textProperty().setValue("." + totalAmountSplit[1]);

                transferAmountLabelNature.textProperty().setValue(AppManager.comma(amountSplit[0]));
                transferAmountLabelDecimal.textProperty().setValue("." + amountSplit[1]);

                gasPriceReceiptNature.textProperty().setValue(AppManager.comma(gasSplit[0]));
                gasPriceReceiptDecimal.textProperty().setValue("." + gasSplit[1]);

                totalWithdrawalNature.textProperty().setValue(AppManager.comma(totalAmountSplit[0]));
                totalWithdrawalDecimal.textProperty().setValue("." + totalAmountSplit[1]);

                afterBalanceNature.textProperty().setValue(AppManager.comma(afterBalanceSplit[0]));
                afterBalanceDecimal.textProperty().setValue("." + afterBalanceSplit[1]);
            }
        }

        // 트랜스퍼 버튼 활성화/비활성화 체크
        checkTransferButton();
    }

    public void showPercentSelectBox(int index){
        this.pSelectLists.get(index).setVisible(true);
        this.pSelectLists.get(index).prefHeightProperty().setValue(-1);
        this.pSelectChildList.get(index).prefHeightProperty().setValue(-1);
    }

    public void hidePercentSelectBox(int index){
        this.pSelectLists.get(index).setVisible(false);
        this.pSelectLists.get(index).prefHeightProperty().setValue(0);
        this.pSelectChildList.get(index).prefHeightProperty().setValue(48);
    }

    public void showContractSelectBox(){
        this.cSelectList.setVisible(true);
        this.cSelectList.prefHeightProperty().setValue(-1);
        this.cSelectChild.prefHeightProperty().setValue(-1);
    }

    public void hideContractSelectBox(){
        this.cSelectList.setVisible(false);
        this.cSelectList.prefHeightProperty().setValue(0);
        this.cSelectChild.prefHeightProperty().setValue(40);
    }

    public void initTab(int index) {
        this.selectedTabIndex = index;
        initTabClean();
        initSideTabClean();

        if(index == 0) {
            this.tab1LeftPane.setVisible(true);
            this.tab1RightPane.setVisible(true);
            this.transferBtn.setVisible(true);
            this.tabLabel1.setTextFill(Color.web("#910000"));
            this.tabLabel1.setStyle("-fx-font-family: 'Open Sans SemiBold'; -fx-font-size:11px;");
            this.tabLinePane1.setVisible(true);
            this.sideTabLabel1.setTextFill(Color.web("#910000"));
            this.sideTabLabel1.setStyle("-fx-font-family: 'Open Sans SemiBold'; -fx-font-size:12px;");
            this.sideTabLinePane1.setVisible(true);

            //button
            transferBtn.setVisible(true);
            writeBtn.setVisible(false);
            checkTransferButton();

            // layout data
            initLayoutData();
        } else if(index == 1) {
            this.tab2LeftPane.setVisible(true);
            this.tab1RightPane.setVisible(true);
            this.tabLabel2.setTextFill(Color.web("#910000"));
            this.tabLabel2.setStyle("-fx-font-family: 'Open Sans SemiBold'; -fx-font-size:11px;");
            this.tabLinePane2.setVisible(true);

            //button
            transferBtn.setVisible(false);
            writeBtn.setVisible(true);

        } else if(index == 2) {
            this.tabLabel3.setTextFill(Color.web("#910000"));
            this.tabLabel3.setStyle("-fx-font-family: 'Open Sans SemiBold'; -fx-font-size:11px;");
            this.tabLinePane3.setVisible(true);

        }
        settingLayoutData();
    }

    public void initSideTab(int index) {
        initSideTabClean();

        if(index == 0) {
            this.sideTabLabel1.setTextFill(Color.web("#910000"));
            this.sideTabLabel1.setStyle("-fx-font-family: 'Open Sans SemiBold'; -fx-font-size:12px;");
            this.sideTabLinePane1.setVisible(true);

            codeTab1.setVisible(true);
            codeTab2.setVisible(false);

        } else if(index == 1) {
            this.sideTabLabel2.setTextFill(Color.web("#910000"));
            this.sideTabLabel2.setStyle("-fx-font-family: 'Open Sans SemiBold'; -fx-font-size:12px;");
            this.sideTabLinePane2.setVisible(true);

            codeTab1.setVisible(false);
            codeTab2.setVisible(true);
        }
    }

    public void initTabClean() {
        tab1LeftPane.setVisible(false);
        tab1RightPane.setVisible(false);
        tab2LeftPane.setVisible(false);
        transferBtn.setVisible(false);
        tabLabel1.setTextFill(Color.web("#999999"));
        tabLabel2.setTextFill(Color.web("#999999"));
        tabLabel3.setTextFill(Color.web("#999999"));
        tabLabel1.setStyle("-fx-font-family: 'Open Sans'; -fx-font-size:11px;");
        tabLabel2.setStyle("-fx-font-family: 'Open Sans'; -fx-font-size:11px;");
        tabLabel3.setStyle("-fx-font-family: 'Open Sans'; -fx-font-size:11px;");
        tabLinePane1.setVisible(false);
        tabLinePane2.setVisible(false);
        tabLinePane3.setVisible(false);
    }

    public void initSideTabClean() {
        sideTabLabel1.setTextFill(Color.web("#999999"));
        sideTabLabel2.setTextFill(Color.web("#999999"));
        sideTabLabel1.setStyle("-fx-font-family: 'Open Sans'; -fx-font-size:12px;");
        sideTabLabel2.setStyle("-fx-font-family: 'Open Sans'; -fx-font-size:12px;");
        sideTabLinePane1.setVisible(false);
        sideTabLinePane2.setVisible(false);

        tab1SolidityTextArea1.clear();
        tab1SolidityTextArea2.getChildren().clear();
        tab1SolidityTextArea3.setText("");
    }

    public void initContract() {
        cSelectHead.setStyle("-fx-background-color: #999999; -fx-border-color: d8d8d8; -fx-border-radius : 4 4 4 4; -fx-background-radius: 4 4 4 4;");
        cSelectHeadText.setText(cSelectItemDefaultText.getText());
        cSelectHeadText.setTextFill(Color.web("#ffffff"));
        cSelectHeadImg.setImage(downWhite);
        tab2ReadWritePane.setVisible(false);
        tab2ReadWritePane.prefHeightProperty().setValue(0);
    }

    public void showGasPricePopup(int index) {
        gasPricePlusMinusLabelList.get(index).setTextFill(Color.web("#2b2b2b"));
        gasPriceGridList.get(index).setStyle("-fx-background-color: #ffffff; -fx-border-color: #d8d8d8; -fx-border-radius: 4 4 4 4; -fx-background-radius: 4 4 4 4;");
        gasPricePopupGridList.get(index).setVisible(true);
        gasPricePopupImgList.get(index).setVisible(true);
        gasPricePopupGridList.get(index).prefHeightProperty().setValue(-1);
        gasPricePopupImgList.get(index).prefHeight(90);
    }

    public void hideGasPricePopup(int index) {
        gasPricePlusMinusLabelList.get(index).setTextFill(Color.web("#999999"));
        gasPriceGridList.get(index).setStyle("-fx-background-color: #f2f2f2; -fx-border-color: #d8d8d8; -fx-border-radius: 4 4 4 4; -fx-background-radius: 4 4 4 4;");
        gasPricePopupGridList.get(index).setVisible(false);
        gasPricePopupImgList.get(index).setVisible(false);
        gasPricePopupGridList.get(index).prefHeightProperty().setValue(0);
        gasPricePopupImgList.get(index).prefHeight(1);
    }

    public void hideGasPricePopupAll() {
        for(int i=0; i<gasPriceGridList.size(); i++) {
            hideGasPricePopup(i);
        }
    }

    public boolean checkTransferButton(){
        boolean result = false;

        String data = tab1SolidityTextArea1.getText();
        String gasLimit = tab1GasLimitTextField.getText();
        if(data.length() > 0 && contractInputView.isVisible()
                && gasLimit.length() > 0
                && minGasLimit <= Long.parseLong(gasLimit)){
            result = true;
        }

        if(result){
            transferBtn.setStyle( new JavaFXStyle(transferBtn.getStyle()).add("-fx-background-color","#910000").toString());
        }else{
            transferBtn.setStyle( new JavaFXStyle(transferBtn.getStyle()).add("-fx-background-color","#d8d8d8").toString());
        }
        return result;
    }


    /**
     *
     * @param contractName : 컨트렉트 이름
     */
    private void createContractFieldInMethodList(String contractName){
        // 컨트렉트 선택시 생성자 체크
        if(res != null){

            metadata = res.getContract(contractName);
            if(metadata.bin == null || metadata.bin.isEmpty()){
                throw new RuntimeException("Compilation failed, no binary returned");
            }
            System.out.println("metadata.abi : "+metadata.abi);
            CallTransaction.Contract cont = new CallTransaction.Contract(metadata.abi);
            CallTransaction.Function function = cont.getByName(""); // get constructor

            contractMethodList.getChildren().clear();  //필드 초기화
            contractParams.clear();

            if(function == null) { return ; }
            for(CallTransaction.Param param : function.inputs){
                String paramName = param.name;
                String paramType = param.type.toString();

                Node node = null;
                if(param.type instanceof SolidityType.BoolType){
                    // BOOL

                    CheckBox checkBox = new CheckBox();
                    checkBox.setText(paramName);
                    node = checkBox;

                    // param 등록
                    SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty();
                    booleanProperty.bind(checkBox.selectedProperty());
                    contractParams.add(booleanProperty);

                }else if(param.type instanceof SolidityType.AddressType){
                    // AddressType
                    System.out.println("node address");
                    final TextField textField = new TextField();
                    textField.setPromptText(paramType+" "+paramName);
                    node = textField;

                    // Only Hex, maxlength : 40
                    textField.textProperty().addListener((observable, oldValue, newValue) -> {
                        if (!newValue.matches("[0-9a-f]*")) {
                            textField.setText(newValue.replaceAll("[^0-9a-f]", ""));
                        }
                        if(textField.getText().length() > 40){
                            textField.setText(textField.getText().substring(0, 40));
                        }
                    });

                    // param 등록
                    SimpleStringProperty stringProperty = new SimpleStringProperty();
                    stringProperty.bind(textField.textProperty());
                    contractParams.add(stringProperty);

                }else if(param.type instanceof SolidityType.IntType){
                    // INT, uINT

                    System.out.println("node int");
                    final TextField textField = new TextField();
                    textField.setPromptText(paramType+" "+paramName);

                    // Only Number
                    textField.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue,
                                            String newValue) {
                            if (!newValue.matches("\\d*")) {
                                textField.setText(newValue.replaceAll("[^\\d]", ""));
                            }
                        }
                    });
                    node = textField;

                    // param 등록
                    SimpleStringProperty stringProperty = new SimpleStringProperty();
                    stringProperty.bind(textField.textProperty());
                    contractParams.add(stringProperty);

                }else if(param.type instanceof SolidityType.StringType){
                    // StringType

                    TextField textField = new TextField();
                    textField.setPromptText(paramType+" "+paramName);
                    node = textField;

                    // param 등록
                    SimpleStringProperty stringProperty = new SimpleStringProperty();
                    stringProperty.bind(textField.textProperty());
                    contractParams.add(stringProperty);

                }else if(param.type instanceof SolidityType.BytesType){
                    // BytesType

                    TextField textField = new TextField();
                    textField.setPromptText(paramType+" "+paramName);
                    node = textField;

                    // param 등록
                    SimpleStringProperty stringProperty = new SimpleStringProperty();
                    stringProperty.bind(textField.textProperty());
                    contractParams.add(stringProperty);

                }else if(param.type instanceof SolidityType.Bytes32Type){
                    // Bytes32Type

                    TextField textField = new TextField();
                    textField.setPromptText(paramType+" "+paramName);
                    node = textField;

                    // param 등록
                    SimpleStringProperty stringProperty = new SimpleStringProperty();
                    stringProperty.bind(textField.textProperty());
                    contractParams.add(stringProperty);

                }else if(param.type instanceof SolidityType.FunctionType){
                    // FunctionType

                    TextField textField = new TextField();
                    textField.setPromptText(paramType+" "+paramName);
                    node = textField;

                }else if(param.type instanceof SolidityType.ArrayType){
                    // ArrayType

                    TextField textField = new TextField();
                    textField.setPromptText(paramType+" "+paramName);
                    node = textField;

                    // param 등록
                    SimpleStringProperty stringProperty = new SimpleStringProperty();
                    stringProperty.bind(textField.textProperty());
                    contractParams.add(stringProperty);
                }

                if(node != null){
                    //필드에 추가
                    contractMethodList.getChildren().add(node);
                }
            } //for function.inputs

            byte[] data = ByteUtil.merge(Hex.decode(metadata.bin), new byte[0]);
            long preGasUsed = AppManager.getInstance().getPreGasUsed(Hex.decode(walletSelectorController.getAddress()), null, data);
            tab1GasLimitTextField.textProperty().set(""+preGasUsed);
            minGasLimit = preGasUsed;
        }
    }
}
