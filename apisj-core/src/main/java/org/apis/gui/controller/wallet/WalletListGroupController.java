package org.apis.gui.controller.wallet;

import javafx.scene.input.InputEvent;
import javafx.scene.layout.VBox;
import org.apis.gui.controller.MainController;
import org.apis.gui.controller.base.BaseFxmlController;
import org.apis.gui.controller.base.BaseViewController;
import org.apis.gui.controller.popup.PopupCopyWalletAddressController;
import org.apis.gui.controller.popup.PopupMaskingController;
import org.apis.gui.manager.AppManager;
import org.apis.gui.manager.PopupManager;
import org.apis.gui.model.TokenModel;
import org.apis.gui.model.WalletItemModel;
import org.apis.gui.model.base.BaseModel;
import org.apis.keystore.KeyStoreDataExp;
import org.apis.util.ConsoleUtil;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class WalletListGroupController extends BaseViewController {
    private GroupType groupType = GroupType.WALLET;
    private BaseFxmlController header;
    private List<BaseFxmlController> items = new ArrayList<>();
    private boolean isVisibleItemList = false;

    public WalletListGroupController(GroupType groupType){
        this.groupType = groupType;

        try {
            header = new BaseFxmlController("wallet/wallet_list_header.fxml");
            WalletListHeadController controller = (WalletListHeadController)header.getController();
            controller.setGroupType(this.groupType);
            controller.setHandler(headerHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(this.groupType == GroupType.WALLET){
            List<TokenModel> tokens = AppManager.getInstance().getTokens();
            for(int i=0; i<tokens.size(); i++){
                addItem(null);
            }
        }
    }

    @Override
    public void setModel(BaseModel model) {
        if(model != null) {
            this.model = model;
            WalletItemModel itemModel = (WalletItemModel)this.model;
            this.header.getController().setModel(model);

            if (this.groupType == GroupType.WALLET) {

                // 최대 갯수만큼 토큰 컨트롤러를 생성한다.
                List<TokenModel> tokens = AppManager.getInstance().getTokens();
                int count = tokens.size() - this.items.size();
                for (int i = count; i > 0; i--) {
                    addItem(null);
                }

                // 생성된 토큰 컨트롤러가 더 많을 경우 컨트롤러 삭제
                count = this.items.size() - tokens.size();
                for (int i = count; i > 0; i--) {
                    this.items.remove(0);
                }

                // 토큰 컨트롤러에 데이터 넣기
                for (int i = 0; i < items.size(); i++) {
                    WalletListBodyController controller = (WalletListBodyController) items.get(i).getController();
                    WalletItemModel newModel = ((WalletItemModel) model).getClone();
                    newModel.setTokenAddress(tokens.get(i).getTokenAddress());
                    controller.setModel(newModel);
                }
            } else if (this.groupType == GroupType.TOKEN) {


                // 최대 갯수만큼 토큰 컨트롤러를 생성한다.
                ArrayList<KeyStoreDataExp> wallet = AppManager.getInstance().getKeystoreExpList();
                int count = wallet.size() - this.items.size();
                for (int i = count; i > 0; i--) {
                    addItem(null);
                }

                // 생성된 토큰 컨트롤러가 더 많을 경우 컨트롤러 삭제
                count = this.items.size() - wallet.size();
                for (int i = count; i > 0; i--) {
                    this.items.remove(0);
                }

                // 토큰 컨트롤러에 데이터 넣기
                BigInteger totalTokenValue = BigInteger.ZERO;
                for (int i = 0; i < items.size(); i++) {
                    WalletListBodyController controller = (WalletListBodyController) items.get(i).getController();
                    WalletItemModel newModel = new WalletItemModel();
                    newModel.setTokenAddress(itemModel.getTokenAddress());
                    newModel.setAddress(wallet.get(i).address);
                    newModel.setAlias(wallet.get(i).alias);
                    newModel.setMask(wallet.get(i).mask);
                    newModel.setApis(wallet.get(i).balance);
                    newModel.setMineral(wallet.get(i).mineral);

                    if( !newModel.getTokenAddress().equals("-1")  && !newModel.getTokenAddress().equals("-2")){
                        totalTokenValue.add(AppManager.getInstance().getTokenValue(newModel.getTokenAddress(), newModel.getAddress()));
                    }

                    controller.setModel(newModel);
                }
                ((WalletItemModel)header.getController().getModel()).setTotalTokenValue(totalTokenValue);
                header.getController().setModel(header.getController().getModel());

            }
        }
    }

    /**
     * 이 그룹에 아이템을 포함시킨다.
     * @param model 아이템을 포함 시킬 모델
     */
    public void addItem(WalletItemModel model){
        try {
            BaseFxmlController item = new BaseFxmlController("wallet/wallet_list_body.fxml");
            ((WalletListBodyController)item.getController()).setGroupType(this.groupType);
            item.getController().setModel(model);
            this.items.add(item);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 인자에 넣은 Pane에 Node를 추가한다.
     * 추가되는 순서는, header, itemList 순서이다.
     * @param parent Node들을 추가할 VBox
     */
    public void drawNode(VBox parent){
        if(header != null) {
            // header
            parent.getChildren().add(header.getNode());

            // items
            for (int i = 0; i < items.size(); i++) {
                parent.getChildren().add(items.get(i).getNode());
            }
        }
    }

    /**
     * 아이템을 모두 보여주거나 모두 숨길 수 있다.
     * @param isVisible {true : 모두 보임, false : 모두 숨김}
     */
    public void setVisibleItemList(boolean isVisible){
        this.isVisibleItemList = isVisible;
        for(int i=0; i<this.items.size(); i++){
            WalletListBodyController controller = (WalletListBodyController)this.items.get(i).getController();
            if(isVisible){
                controller.show();
            }else{
                controller.hide();
            }
        }
    }

    /**
     * 현재 설정한 상태로 지갑을 다시 보여준다.
     */
    public void refresh() {
        setVisibleItemList(this.isVisibleItemList);
    }


    /**
     * Header Event
     */
    private WalletListHeadController.WalletListHeaderInterface headerHandler = new WalletListHeadController.WalletListHeaderInterface() {
        @Override
        public void onClickEvent(InputEvent event, WalletItemModel model) {
            setVisibleItemList(!isVisibleItemList);
        }

        @Override
        public void onClickTransfer(InputEvent event, WalletItemModel model) {
            AppManager.getInstance().guiFx.getMain().selectedHeader(MainController.MainTab.TRANSFER);
            AppManager.getInstance().guiFx.getTransfer().init(model.getId());
        }

        @Override
        public void onChangeCheck(WalletItemModel model, boolean isChecked) {

        }

        @Override
        public void onClickCopy(String address, WalletItemModel model) {
            PopupCopyWalletAddressController controller = (PopupCopyWalletAddressController)PopupManager.getInstance().showMainPopup("popup_copy_wallet_address.fxml", 0);
            controller.setAddress(address);
        }

        @Override
        public void onClickAddressMasking(InputEvent event, WalletItemModel model) {
            PopupMaskingController controller = (PopupMaskingController)PopupManager.getInstance().showMainPopup("popup_masking.fxml", 0);
            controller.setSelectAddress(model.getAddress());
        }
    };


    public enum GroupType {
        /* 지갑기준 */WALLET(0),
        /* 토큰기준 */TOKEN(1);
        int num;
        GroupType(int num) {
            this.num = num;
        }
    }
}