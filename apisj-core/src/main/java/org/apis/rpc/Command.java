package org.apis.rpc;

import com.google.gson.JsonObject;
import org.apis.contract.ContractLoader;
import org.apis.core.*;
import org.apis.crypto.ECKey;
import org.apis.facade.Ethereum;
import org.apis.keystore.*;
import org.apis.rpc.template.*;
import org.apis.util.ByteUtil;
import org.apis.util.ConsoleUtil;
import org.apis.util.FastByteComparisons;
import org.apis.util.blockchain.ApisUtil;
import org.java_websocket.WebSocket;
import org.json.simple.parser.ParseException;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.apis.crypto.HashUtil.EMPTY_DATA_HASH;
import static org.apis.rpc.JsonUtil.*;

public class Command {
    static final String COMMAND_FLAT = "flat_";
    static final String COMMAND_GETBLOCK_NUMBER = "getblocknumber";
    static final String COMMAND_WALLET_INFO = "walletinfo";
    static final String COMMAND_GETNONCE = "getnonce";
    static final String COMMAND_GETBALANCE = "getbalance";
    static final String COMMAND_GETBALANCE_BY_MASK = "getbalancebymask";

    static final String COMMAND_GETMASK_BY_ADDRESS = "getmaskbyaddress";
    static final String COMMAND_GETADDRESS_BY_MASK = "getaddressbymask";
    static final String COMMAND_GETTRANSACTION = "gettx";
    static final String COMMAND_GETTRANSACTIONRECEIPT = "gettxreceipt";
    static final String COMMAND_SENDTRANSACTION_SIGNNING = "sendtxsignning";
    static final String COMMAND_SENDTRANSACTION = "sendtx";
    static final String COMMAND_SENDRAWTRANSACTION = "sendrawtx";

    static final String COMMAND_GETBLOCK_BY_NUMBER = "getblockbynumber";
    static final String COMMAND_GETBLOCK_BY_HASH = "getblockbyhash";
    static final String COMMAND_GETMASTERNODE_LIST = "getmnlist";
    static final String COMMAND_GETMASTERNODE_INFO = "getmninfo";

    static final String COMMAND_REGISTERKNOWLEDGEKEY = "registerknowledgekey";

    // data type
    static final String DATA_TAG_REQUESTID = "requestId";
    static final String DATA_TAG_TYPE = "type";
    static final String DATA_TAG_AUTH = "auth";
    static final String DATA_TAG_DATA = "data";

    static final String TYPE_LOGIN = "login";
    static final String TYPE_TOKEN = "token";
    static final String TYPE_BLOCK = "block";
    static final String TYPE_BLOCK_NUMBER = "blocknumber";
    static final String TYPE_ADDRESS = "address";
    static final String TYPE_MASK = "mask";
    static final String TYPE_TXHASH = "txhash";
    static final String TYPE_BLOCKHASH = "blockhash";
    static final String TYPE_GASLIMIT = "gaslimit";
    static final String TYPE_GASPRICE = "gasprice";
    static final String TYPE_VALUE = "value";
    static final String TYPE_KEYSTORE_PW = "keystorepassword";
    static final String TYPE_KNOWLEDGE_PW = "knowledgepassword";
    static final String TYPE_WALLET_INDEX = "walletIndex";
    static final String TYPE_COUNT = "count";
    static final String TYPE_TX = "tx";
    static final String TYPE_APIS = "APIS";
    static final String TYPE_MNR = "MNR";
    static final String TYPE_NONCE = "nonce";
    static final String TYPE_MESSAGE = "message";
    static final String TYPE_CODE = "code";
    static final String TYPE_ERROR = "error";

    // error
    static final int ERROR_CODE_UNNKOWN = 100;
    static final int ERROR_CODE_OVERFLOW_MAXCONNECTION = 101;
    static final int ERROR_CODE_DUPLICATE_IP = 102;
    static final int ERROR_CODE_WRONG_ID_PASSWORD = 103;
    static final int ERROR_CODE_WRONG_AUTHKEY = 104;
    static final int ERROR_CODE_WRONG_TOKENKEY = 105;
    static final int ERROR_CODE_WRONG_REQUESTID = 106;
    static final int ERROR_CODE_WITHOUT_PERMISSION_CLIENT = 113;
    static final int ERROR_CODE_WITHOUT_PERMISSION_IP = 114;
    static final int ERROR_CODE_WITHOUT_PERMISSION_TYPE = 115;
    static final int ERROR_CODE_NULL_REQUESTID = 120;

    static final String ERROR_DEPORT_UNKNOWN = "unknown error.";
    static final String ERROR_DEPORT_OVERFLOW_MAXCONNECTION = "Reached max connection allowance.";
    static final String ERROR_DEPORT_DUPLICATE_IP = "IP address duplicated.";
    static final String ERROR_DEPORT_WRONG_ID_PASSWORD = "Wrong ID or password.";
    static final String ERROR_DEPORT_WRONG_AUTHKEY = "Unauthorized key.";
    static final String ERROR_DEPORT_WRONG_TOKENKEY = "Unauthorized token key.";
    static final String ERROR_DEPORT_WRONG_REQUESTID = "Wrong RequestID.";
    static final String ERROR_DEPORT_WITHOUT_PERMISSION_CLIENT = "Unallowed client.";
    static final String ERROR_DEPORT_WITHOUT_PERMISSION_IP = "Unallowed IP address.";
    static final String ERROR_DEPORT_WITHOUT_PERMISSION_TYPE = "Unallowed command.";
    static final String ERROR_DEPORT_NULL_REQUESTID = "Cannot find RequestID.";

    static final String ERROR_NULL_ADDRESS_BY_MASK = "Address registered to the mask does not exist.";
    static final String ERROR_NULL_MASK_BY_ADDRESS = "There is no mask registered to the address.";
    static final String ERROR_NULL_TRANSACTION_BY_HASH = "There is no transaction can be found with the hash.";
    static final String ERROR_NULL_TOADDRESS_OR_TOMASK = "There is no receiving address or mask.";
    static final String ERROR_NULL_BLOCK_BY_NUMBER = "There is no block can be found with the number.";
    static final String ERROR_NULL_BLOCK_BY_HASH = "There is no block can be found with the hash.";
    static final String ERROR_NULL_MASTERNODE_ADDRESS = "There is no address registered as masternode.";
    static final String ERROR_NULL_WALLET_ADDRESS = "There is no address registered as wallet.";

    // RPC 명령어
    public static void conduct(Ethereum ethereum, WebSocket conn, byte[] token,
                               String requestId, String request, String message) throws ParseException {
        System.out.println("request :" + request);
        String command;
        Repository repo = ((Repository)ethereum.getRepository()).getSnapshotTo(ethereum.getBlockchain().getBestBlock().getStateRoot());
        JsonObject jsonObject = new JsonObject();
        boolean isFlatString = false;

        switch (request) {

            case COMMAND_FLAT + COMMAND_GETBLOCK_NUMBER:
                isFlatString = true;
            case COMMAND_GETBLOCK_NUMBER: {
                long blockNumber = ethereum.getBlockchain().getBestBlock().getNumber();
                jsonObject.addProperty(TYPE_BLOCK_NUMBER, blockNumber);
                command = createJson(
                        isFlatString
                        , requestId
                        , COMMAND_GETBLOCK_NUMBER
                        , jsonObject
                );
                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_GETNONCE:
                isFlatString = true;
            case COMMAND_GETNONCE: {
                String address = getDecodeMessageDataContent(message, TYPE_ADDRESS);

                BigInteger nonce = null;
                try {
                    byte[] addressByte = Hex.decode(address);
                    nonce = ethereum.getRepository().getNonce(addressByte);

                    jsonObject.addProperty(TYPE_NONCE, nonce.toString());
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETNONCE
                            , jsonObject
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.addProperty(TYPE_ADDRESS, address);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETNONCE
                            , jsonObject
                            , e
                    );
                }

                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_GETBALANCE:
                isFlatString = true;
            case COMMAND_GETBALANCE: {
                String address = getDecodeMessageDataContent(message, TYPE_ADDRESS);

                try {
                    byte[] addressByte = Hex.decode(address);
                    long blockNumber = ethereum.getBlockchain().getBestBlock().getNumber();

                    BigInteger balance = ethereum.getRepository().getBalance(addressByte);
                    BigInteger mineral = ethereum.getRepository().getMineral(addressByte, blockNumber);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETBALANCE
                            , new ApisBalanceData(address, balance.toString(), mineral.toString())
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.addProperty(TYPE_ADDRESS, address);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETBALANCE
                            , jsonObject
                            , e
                    );
                }

                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_GETBALANCE_BY_MASK:
                isFlatString = true;
            case COMMAND_GETBALANCE_BY_MASK: {
                String mask = getDecodeMessageDataContent(message, TYPE_MASK);

                try {
                    byte[] addressByte = repo.getAddressByMask(mask);

                    if (addressByte != null) {
                        long blockNumber = ethereum.getBlockchain().getBestBlock().getNumber();
                        String address = ByteUtil.toHexString(addressByte);
                        BigInteger balance = ethereum.getRepository().getBalance(addressByte);
                        BigInteger mineral = ethereum.getRepository().getMineral(addressByte, blockNumber);

                        command = createJson(
                                isFlatString
                                , requestId
                                , COMMAND_GETBALANCE
                                , new ApisBalanceData(address, balance.toString(), mineral.toString())
                        );

                    } else {
                        ConsoleUtil.printRed(ERROR_NULL_ADDRESS_BY_MASK);
                        jsonObject.addProperty(TYPE_MASK, mask);
                        command = createJson(
                                isFlatString
                                , requestId
                                , COMMAND_GETBALANCE_BY_MASK
                                , jsonObject
                                , ERROR_NULL_ADDRESS_BY_MASK
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.addProperty(TYPE_MASK, mask);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETBALANCE_BY_MASK
                            , jsonObject
                            , e
                    );
                }

                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_GETMASK_BY_ADDRESS:
                isFlatString = true;
            case COMMAND_GETMASK_BY_ADDRESS: {
                String address = getDecodeMessageDataContent(message, TYPE_ADDRESS);
                String mask = null;

                try {
                    mask = repo.getMaskByAddress(Hex.decode(address));

                    if (mask == null || mask.equals("")) {
                        jsonObject.addProperty(TYPE_ADDRESS, address);
                        command = createJson(
                                isFlatString
                                , requestId
                                , COMMAND_GETMASK_BY_ADDRESS
                                , jsonObject
                                , ERROR_NULL_MASK_BY_ADDRESS
                        );
                    } else {
                        jsonObject.addProperty(TYPE_MASK, mask);
                        command = createJson(
                                isFlatString
                                , requestId
                                , COMMAND_GETMASK_BY_ADDRESS
                                , jsonObject
                        );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.addProperty(TYPE_ADDRESS, address);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETMASK_BY_ADDRESS
                            , jsonObject
                            , e
                    );
                }

                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_GETADDRESS_BY_MASK:
                isFlatString = true;
            case COMMAND_GETADDRESS_BY_MASK: {
                String mask = getDecodeMessageDataContent(message, TYPE_MASK);
                try {
                    byte[] addressByte = repo.getAddressByMask(mask);

                    if (addressByte != null) {
                        jsonObject.addProperty(TYPE_ADDRESS, ByteUtil.toHexString(addressByte));
                        command = createJson(
                                isFlatString
                                , requestId
                                , COMMAND_GETADDRESS_BY_MASK
                                , jsonObject
                        );
                    } else {
                        ConsoleUtil.printRed(ERROR_NULL_ADDRESS_BY_MASK);
                        jsonObject.addProperty(TYPE_MASK, mask);
                        command = createJson(
                                isFlatString
                                , requestId
                                , COMMAND_GETADDRESS_BY_MASK
                                , jsonObject
                                , ERROR_NULL_ADDRESS_BY_MASK
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.addProperty(TYPE_MASK, mask);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETADDRESS_BY_MASK
                            , jsonObject
                            , e);
                }


                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_GETTRANSACTION:
                isFlatString = true;
            case COMMAND_GETTRANSACTION: {
                String txHash = getDecodeMessageDataContent(message, TYPE_TXHASH);

                if (txHash.startsWith("0x")) {
                    txHash = txHash.replace("0x","");
                }

                try {
                    TransactionInfo txInfo = ethereum.getTransactionInfo(Hex.decode(txHash));

                    // 트랜잭션이 실행된 적 없는 경우? TODO (result :  null)
                    if (txInfo == null || txInfo.getReceipt() == null) {
                        jsonObject.addProperty(TYPE_TXHASH, txHash);
                        command = createJson(
                                isFlatString
                                , requestId
                                , COMMAND_GETTRANSACTION
                                , jsonObject
                                , ERROR_NULL_TRANSACTION_BY_HASH
                        );
                    } else {
                        TransactionData txData = new TransactionData(txInfo, ethereum.getBlockchain().getBlockByHash(txInfo.getBlockHash()));
                        command = createJson(
                                isFlatString
                                , requestId
                                , COMMAND_GETTRANSACTION
                                , txData
                                , txInfo.getReceipt().getError()
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.addProperty(TYPE_TXHASH, txHash);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETTRANSACTION
                            , jsonObject
                            , e
                    );
                }
                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_GETTRANSACTIONRECEIPT:
                isFlatString = true;
            case COMMAND_GETTRANSACTIONRECEIPT: {
                String txHash = getDecodeMessageDataContent(message, TYPE_TXHASH);

                if (txHash.startsWith("0x")) {
                    txHash = txHash.substring(2, txHash.length());
                }

                try {
                    TransactionInfo txInfo = ethereum.getTransactionInfo(Hex.decode(txHash));

                    // 트랜잭션이 실행된 적 없는 경우? TODO (result :  null)
                    if (txInfo == null || txInfo.getReceipt() == null) {
                        jsonObject.addProperty(TYPE_TXHASH, txHash);
                        command = createJson(
                                isFlatString
                                , requestId
                                , COMMAND_GETTRANSACTIONRECEIPT
                                , jsonObject
                                , ERROR_NULL_TRANSACTION_BY_HASH
                        );
                    } else {
                        TransactionReceiptData txReceiptData = new TransactionReceiptData(txInfo, ethereum.getBlockchain().getBlockByHash(txInfo.getBlockHash()));
                        command = createJson(
                                isFlatString
                                , requestId
                                , COMMAND_GETTRANSACTIONRECEIPT
                                , txReceiptData
                                , txInfo.getReceipt().getError()
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.addProperty(TYPE_TXHASH, txHash);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETTRANSACTIONRECEIPT
                            , jsonObject
                            , e
                    );
                }

                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_WALLET_INFO:
                isFlatString = true;
            case COMMAND_WALLET_INFO: {
                boolean isParameter = getDecodeIsData(message);
                ArrayList<WalletInfo> walletInfos = new ArrayList<>();

                if (isParameter) { // return 1
                    String address = getDecodeMessageDataContent(message, TYPE_ADDRESS);

                    try {
                        byte[] addressByte = Hex.decode(address);
                        String mask = repo.getMaskByAddress(addressByte);

                        long blockNumber = ethereum.getBlockchain().getBestBlock().getNumber();
                        BigInteger apisBalance = ethereum.getRepository().getBalance(addressByte);
                        BigInteger apisMineral = ethereum.getRepository().getMineral(addressByte, blockNumber);
                        BigInteger nonce = ethereum.getRepository().getNonce(addressByte);
                        byte[] proofKey = repo.getProofKey(addressByte);
                        boolean hasProofKey = false;
                        if (proofKey != null && !FastByteComparisons.equal(proofKey, EMPTY_DATA_HASH)) {
                            hasProofKey = true;
                        }

                        WalletInfo walletInfo = new WalletInfo(address, mask, apisBalance.toString(), apisMineral.toString(), nonce.toString(), hasProofKey);
                        walletInfos.add(walletInfo);
                        command = createJson(
                                isFlatString
                                , requestId
                                , COMMAND_WALLET_INFO
                                , walletInfos
                        );

                    } catch (Exception e) {
                        e.printStackTrace();
                        jsonObject.addProperty(TYPE_ADDRESS, address);
                        command = createJson(
                                isFlatString
                                , requestId
                                , COMMAND_WALLET_INFO
                                , jsonObject
                                , e
                        );
                    }
                }
                else { // non parameter (return list)

                    List<KeyStoreData> keyStoreDataList = KeyStoreManager.getInstance().loadKeyStoreFiles();

                    int count = keyStoreDataList.size();
                    if (count > 0) {

                        try {
                            for (int i = 0; i < count; i++) {
                                String address = keyStoreDataList.get(i).address;
                                String mask = repo.getMaskByAddress(Hex.decode(address));

                                long blockNumber = ethereum.getBlockchain().getBestBlock().getNumber();
                                BigInteger apisBalance = ethereum.getRepository().getBalance(Hex.decode(address));
                                BigInteger apisMineral = ethereum.getRepository().getMineral(Hex.decode(address), blockNumber);
                                BigInteger nonce = ethereum.getRepository().getNonce(Hex.decode(address));
                                byte[] proofKey = repo.getProofKey(Hex.decode(address));
                                boolean hasProofKey = false;
                                if (proofKey != null && !FastByteComparisons.equal(proofKey, EMPTY_DATA_HASH)) {
                                    hasProofKey = true;
                                }

                                WalletInfo walletInfo =
                                        new WalletInfo(address, mask, apisBalance.toString(),
                                                apisMineral.toString(), nonce.toString(), hasProofKey);
                                walletInfos.add(walletInfo);
                            }

                            command = createJson(
                                    isFlatString
                                    , requestId
                                    , COMMAND_WALLET_INFO
                                    , walletInfos
                            );

                        } catch (Exception e) {
                            e.printStackTrace();
                            command = createJson(
                                    isFlatString
                                    , requestId
                                    , COMMAND_WALLET_INFO
                                    , null
                                    , e
                            );
                        }
                    } else {
                        command = createJson(
                                isFlatString
                                , requestId
                                , COMMAND_WALLET_INFO
                                , null
                                , ERROR_NULL_WALLET_ADDRESS
                        );
                    }
                }

                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_SENDTRANSACTION_SIGNNING:
                isFlatString = true;
            case COMMAND_SENDTRANSACTION_SIGNNING: {
                try {
                    long gasLimit = Long.parseLong(getDecodeMessageDataContent(message, TYPE_GASLIMIT));
                    BigInteger gasPrice = new BigInteger(getDecodeMessageDataContent(message, TYPE_GASPRICE));
                    String toAddress = getDecodeMessageDataContent(message, TYPE_ADDRESS);
                    byte[] toAddressByte = null;
                    if (!toAddress.equals("null")) {
                        toAddressByte = Hex.decode(toAddress);
                    }
                    String dataMessage = getDecodeMessageDataContent(message, TYPE_MESSAGE);
                    byte[] dataMessageByte = null;
                    if (!dataMessage.equals("null")) {
                        dataMessageByte = Hex.decode(dataMessage);
                    }

                    int walletIndex = Integer.parseInt(getDecodeMessageDataContent(message, TYPE_WALLET_INDEX));
                    BigInteger value = new BigInteger(getDecodeMessageDataContent(message, TYPE_VALUE));
                    String keystorePasswordEnc = getDecodeMessageDataContent(message, TYPE_KEYSTORE_PW);
                    String keystorePasswordDec = AESDecrypt(ByteUtil.toHexString(token), keystorePasswordEnc);

                    List<KeyStoreData> keyStoreDataList = KeyStoreManager.getInstance().loadKeyStoreFiles();
                    KeyStoreData key = keyStoreDataList.get(walletIndex);
                    byte[] privateKey = KeyStoreUtil.decryptPrivateKey(key.toString(), keystorePasswordDec);

                    ECKey senderKey = ECKey.fromPrivate(privateKey);

                    BigInteger nonce = ethereum.getRepository().getNonce(senderKey.getAddress());
                    int nextBlock = ethereum.getChainIdForNextBlock();

                    Transaction tx = new Transaction(
                            ByteUtil.bigIntegerToBytes(nonce),
                            ByteUtil.bigIntegerToBytes(gasPrice),
                            ByteUtil.longToBytesNoLeadZeroes(gasLimit),
                            toAddressByte,
                            ByteUtil.bigIntegerToBytes(value),
                            dataMessageByte,
                            nextBlock);


                    tx.sign(senderKey); // signing




                    String knowledgePasswordEnc = getDecodeMessageDataContent(message, TYPE_KNOWLEDGE_PW);
                    String knowledgePasswordDec = AESDecrypt(ByteUtil.toHexString(token), knowledgePasswordEnc);

                    if (!knowledgePasswordDec.equals("")) {
                        tx.authorize(knowledgePasswordDec);
                    }

                    jsonObject.addProperty(TYPE_TX, ByteUtil.toHexString(tx.getEncoded()));
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_SENDTRANSACTION_SIGNNING
                            , jsonObject
                    );

                }

                // unknown
                catch (Exception e) {
                    e.printStackTrace();
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_SENDTRANSACTION_SIGNNING
                            , null
                            , e
                    );
                }

                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_SENDTRANSACTION:
                isFlatString = true;
            case COMMAND_SENDTRANSACTION: {

                try {
                    long gasLimit = Long.parseLong(getDecodeMessageDataContent(message, TYPE_GASLIMIT));
                    BigInteger gasPrice = new BigInteger(getDecodeMessageDataContent(message, TYPE_GASPRICE));
                    String toAddress = getDecodeMessageDataContent(message, TYPE_ADDRESS);
                    byte[] toAddressByte = null;
                    if (!toAddress.equals("null")) {

                        // check address mask
                        if (toAddress.contains("@")) {
                            toAddressByte = repo.getAddressByMask(toAddress);
                        } else {
                            toAddressByte = Hex.decode(toAddress);
                        }

                        if (toAddressByte == null) {
                            command = createJson(isFlatString, COMMAND_SENDTRANSACTION,
                                    null, ERROR_NULL_TOADDRESS_OR_TOMASK );
                            send(conn, token, command);
                            return;
                        }
                    }

                    String dataMessage = getDecodeMessageDataContent(message, TYPE_MESSAGE);
                    byte[] dataMessageByte = null;
                    if (!dataMessage.equals("null")) {
                        dataMessageByte = Hex.decode(dataMessage);
                    }

                    int walletIndex = Integer.parseInt(getDecodeMessageDataContent(message, TYPE_WALLET_INDEX));
                    BigInteger value = new BigInteger(getDecodeMessageDataContent(message, TYPE_VALUE));
                    String keystorePasswordEnc = getDecodeMessageDataContent(message, TYPE_KEYSTORE_PW);
                    String keystorePasswordDec = AESDecrypt(ByteUtil.toHexString(token), keystorePasswordEnc);

                    List<KeyStoreData> keyStoreDataList = KeyStoreManager.getInstance().loadKeyStoreFiles();
                    KeyStoreData key = keyStoreDataList.get(walletIndex);
                    byte[] privateKey = KeyStoreUtil.decryptPrivateKey(key.toString(), keystorePasswordDec);


                    ECKey senderKey = ECKey.fromPrivate(privateKey);

                    BigInteger nonce = ethereum.getRepository().getNonce(senderKey.getAddress());
                    int nextBlock = ethereum.getChainIdForNextBlock();

                    Transaction tx = new Transaction(
                            ByteUtil.bigIntegerToBytes(nonce),
                            ByteUtil.bigIntegerToBytes(gasPrice),
                            ByteUtil.longToBytesNoLeadZeroes(gasLimit),
                            toAddressByte,
                            ByteUtil.bigIntegerToBytes(value),
                            dataMessageByte,
                            nextBlock);


                    tx.sign(senderKey); // signing


                    String knowledgePasswordEnc = getDecodeMessageDataContent(message, TYPE_KNOWLEDGE_PW);
                    String knowledgePasswordDec = AESDecrypt(ByteUtil.toHexString(token), knowledgePasswordEnc);

                    if (!knowledgePasswordDec.equals("")) {
                        tx.authorize(knowledgePasswordDec);
                    }

                    command = contractRun(
                            isFlatString
                            , requestId
                            , COMMAND_SENDTRANSACTION
                            , ethereum
                            , tx
                    );
                }

                /*catch (NumberFormatException e) { // 파싱 에러
                    catch (IndexOutOfBoundsException e) { //리스트 사이즈 에러
                    catch (DecoderException e) { // 주소에러
                    catch (NullPointerException e) { // 주소에러
                    catch (InvalidPasswordException e) { // 패스워드 에러
                    catch (KeystoreVersionException e) {
                    catch (NotSupportKdfException e) {
                    catch (NotSupportCipherException e) { */

                // unknown
                catch (Exception e) {
                    e.printStackTrace();
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_SENDTRANSACTION
                            , null
                            , e
                    );
                }

                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_SENDRAWTRANSACTION:
                isFlatString = true;
            case COMMAND_SENDRAWTRANSACTION: {
                String txEncoded = getDecodeMessageDataContent(message, TYPE_TX); // tx.getencoded string

                try {
                    Transaction tx = new Transaction(Hex.decode(txEncoded));
                    command = contractRun(
                            isFlatString
                            , requestId
                            , COMMAND_SENDRAWTRANSACTION
                            , ethereum
                            , tx
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.addProperty(TYPE_TX, txEncoded);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_SENDRAWTRANSACTION
                            , jsonObject
                            , e
                    );
                }

                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_GETBLOCK_BY_NUMBER:
                isFlatString = true;
            case COMMAND_GETBLOCK_BY_NUMBER: {
                String blockNumberStr = getDecodeMessageDataContent(message, TYPE_BLOCK_NUMBER);
                long blockNumber;

                try {
                    blockNumber = Long.parseLong(blockNumberStr);
                    Block block = ethereum.getBlockchain().getBlockByNumber(blockNumber);
                    BlockData blockData = new BlockData(block);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETBLOCK_BY_NUMBER
                            , blockData
                    );

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    jsonObject.addProperty(TYPE_BLOCK_NUMBER, blockNumberStr);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETBLOCK_BY_NUMBER
                            , jsonObject
                            , ERROR_NULL_BLOCK_BY_NUMBER
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.addProperty(TYPE_BLOCK_NUMBER, blockNumberStr);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETBLOCK_BY_NUMBER
                            , jsonObject
                            , e
                    );
                }

                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_GETBLOCK_BY_HASH:
                isFlatString = true;
            case COMMAND_GETBLOCK_BY_HASH: {
                String blockHash = getDecodeMessageDataContent(message, TYPE_BLOCKHASH);

                try {
                    byte[] hash = Hex.decode(blockHash);
                    Block block = ethereum.getBlockchain().getBlockByHash(hash);
                    BlockData blockData = new BlockData(block);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETBLOCK_BY_HASH
                            , blockData
                    );

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    jsonObject.addProperty(TYPE_BLOCKHASH, blockHash);
                    command = createJson(isFlatString
                            , requestId
                            , COMMAND_GETBLOCK_BY_HASH
                            , jsonObject
                            , ERROR_NULL_BLOCK_BY_HASH
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.addProperty(TYPE_BLOCKHASH, blockHash);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETBLOCK_BY_HASH
                            , jsonObject
                            , e
                    );
                }

                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_GETMASTERNODE_LIST:
                isFlatString = true;
            case COMMAND_GETMASTERNODE_LIST: {
                List<String> generalAddress = new ArrayList<>();
                List<String> majorAddress = new ArrayList<>();
                List<String> privateAddress = new ArrayList<>();
                int isCount = 0;

                for (int i=0; i<3; i++) {
                    List<byte[]> mnList = repo.getMasterNodeList(i);
                    for (byte[] addr : mnList) {
                        if (i==0) {
                            generalAddress.add(ByteUtil.toHexString(addr));
                        } else if(i==1) {
                            majorAddress.add(ByteUtil.toHexString(addr));
                        } else {
                            privateAddress.add(ByteUtil.toHexString(addr));
                        }
                        isCount++;
                    }
                }

                if (isCount > 0) {
                    MasterNodeListInfo masterNodeListInfo = new MasterNodeListInfo(generalAddress, majorAddress, privateAddress);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETMASTERNODE_LIST
                            , masterNodeListInfo
                    );
                }
                else {
                    command = createJson(isFlatString
                            , requestId
                            , COMMAND_GETMASTERNODE_LIST
                            ,null
                            , ERROR_NULL_MASTERNODE_ADDRESS);
                }

                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_GETMASTERNODE_INFO:
                isFlatString = true;
            case COMMAND_GETMASTERNODE_INFO: {
                String address = getDecodeMessageDataContent(message, TYPE_ADDRESS);

                try {
                    byte[] addressByte = Hex.decode(address);


                    long startBlock = repo.getMnStartBlock(addressByte);
                    long lastBlock = repo.getMnLastBlock(addressByte);
                    byte[] receiptAddress = repo.getMnRecipient(addressByte);
                    BigInteger balance = repo.getMnStartBalance(addressByte);

                    MasterNodeInfo masterNodeInfo = new MasterNodeInfo(startBlock, lastBlock,
                            ByteUtil.toHexString(receiptAddress), ApisUtil.readableApis(balance));
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETMASTERNODE_INFO
                            , masterNodeInfo
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.addProperty(TYPE_ADDRESS, address);
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_GETMASTERNODE_INFO
                            , jsonObject
                            , e
                    );
                }

                send(conn, token, command);
                break;
            }

            case COMMAND_FLAT + COMMAND_REGISTERKNOWLEDGEKEY:
                isFlatString = true;
            case COMMAND_REGISTERKNOWLEDGEKEY: {
                try {
                    Transaction blankTX = new Transaction(
                            null,
                            null,
                            null,
                            null,
                            ByteUtil.bigIntegerToBytes(new BigInteger("0")),
                            null,
                            null);

                    String knowledgePasswordEnc = getDecodeMessageDataContent(message, TYPE_KNOWLEDGE_PW);
                    String knowledgePasswordDec = AESDecrypt(ByteUtil.toHexString(token), knowledgePasswordEnc);
                    blankTX.authorize(knowledgePasswordDec);

                    byte[] proofcode = blankTX.getProofCode();
                    byte[] registerData = ByteUtil.merge(Hex.decode("d7d72930000000000000000000000000"), proofcode);

                    //// send tx
                    long gasLimit = Long.parseLong(getDecodeMessageDataContent(message, TYPE_GASLIMIT));
                    BigInteger gasPrice = new BigInteger(getDecodeMessageDataContent(message, TYPE_GASPRICE));
                    byte[] toAddressByte = Hex.decode("1000000000000000000000000000000000037452");

                    int walletIndex = Integer.parseInt(getDecodeMessageDataContent(message, TYPE_WALLET_INDEX));
                    BigInteger value = new BigInteger("0");
                    String keystorePasswordEnc = getDecodeMessageDataContent(message, TYPE_KEYSTORE_PW);
                    String keystorePasswordDec = AESDecrypt(ByteUtil.toHexString(token), keystorePasswordEnc);

                    List<KeyStoreData> keyStoreDataList = KeyStoreManager.getInstance().loadKeyStoreFiles();
                    KeyStoreData key = keyStoreDataList.get(walletIndex);
                    byte[] privateKey = KeyStoreUtil.decryptPrivateKey(key.toString(), keystorePasswordDec);


                    ECKey senderKey = ECKey.fromPrivate(privateKey);

                    BigInteger nonce = ethereum.getRepository().getNonce(senderKey.getAddress());
                    int nextBlock = ethereum.getChainIdForNextBlock();

                    Transaction tx = new Transaction(
                            ByteUtil.bigIntegerToBytes(nonce),
                            ByteUtil.bigIntegerToBytes(gasPrice),
                            ByteUtil.longToBytesNoLeadZeroes(gasLimit),
                            toAddressByte,
                            ByteUtil.bigIntegerToBytes(value),
                            registerData,
                            nextBlock);


                    tx.sign(senderKey); // signing

//                    ethereum.submitTransaction(tx); // send
//
//                    jsonObject.addProperty(TYPE_TXHASH, ByteUtil.toHexString(tx.getHash()));
//                    command = createJson(isFlatString, COMMAND_REGISTERKNOWLEDGEKEY, jsonObject);
                    command = contractRun(
                            isFlatString
                            , requestId,COMMAND_REGISTERKNOWLEDGEKEY
                            , ethereum
                            , tx
                    );


                } catch (Exception e) {
                    e.printStackTrace();
                    command = createJson(
                            isFlatString
                            , requestId
                            , COMMAND_REGISTERKNOWLEDGEKEY
                            , null
                            , e
                    );
                }

                send(conn, token, command);
                break;
            }
        }
    }

    private static APISData createApisData(BigInteger balance, String address) {
        return new APISData(address, balance.toString(), ApisUtil.readableApis(balance));
    }

    private static MNRData createMnrData(BigInteger balance, String address) {
        return new MNRData(address, balance.toString(), ApisUtil.readableApis(balance));
    }

    // 전송시 사용
    public static void send(WebSocket conn, byte[] token,  String text) {
        text = JsonUtil.AESEncrypt(ByteUtil.toHexString(token), text);
        conn.send(text);
    }

    // check send tx
    private static String contractRun(boolean isFlatString, String requestId, String type, Ethereum ethereum, Transaction transaction) {
        ContractLoader.ContractRunEstimate contractRunEstimate = ContractLoader.preRunTransaction(ethereum, transaction);

        boolean isPreRunSuccess = contractRunEstimate.isSuccess();
        String preRunError = contractRunEstimate.getReceipt().getError();
        String returnCommand = "";


        /// run
        if (isPreRunSuccess) {
            ethereum.submitTransaction(transaction); // send
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(TYPE_TXHASH, ByteUtil.toHexString(transaction.getHash()));
            returnCommand = createJson(
                    isFlatString
                    , requestId
                    , type
                    , jsonObject
            );
        }
        else {
            returnCommand = createJson(
                    isFlatString
                    , requestId
                    , type
                    , null
                    , preRunError
            );
        }

        return returnCommand;
    }
}
