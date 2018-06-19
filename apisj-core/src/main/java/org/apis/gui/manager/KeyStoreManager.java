package org.apis.gui.manager;

import com.google.gson.Gson;
import org.apis.crypto.ECKey;
import org.apis.keystore.InvalidPasswordException;
import org.apis.keystore.KeyStoreData;
import org.apis.keystore.KeyStoreUtil;
import org.spongycastle.util.encoders.Hex;

import java.io.*;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class KeyStoreManager {
    /* ==============================================
     *  KeyStoreManager Field : public
     * ============================================== */
    public static final String defaultKeystorePath = System.getProperty("user.dir") + "/keystore";


    /* ==============================================
     *  KeyStoreManager Field : private
     * ============================================== */
    private byte[] privateKey = new byte[0];
    private String address = "";
    private String walletAddress = "";
    private String keystoreJsonData = "";
    private String keystoreFullPath = "";
    private KeyStoreData keystoreJsonObject = null; //keystoreJsonData to jsonObject
    private int downloadKeyStoreIndex = 0;
    private File keystoreFile = null;


    /* ==============================================
     *  KeyStoreManager Singleton
     * ============================================== */
    private KeyStoreManager () {}

    private static class Singleton {
        private static final KeyStoreManager instance = new KeyStoreManager();
    }
    public static KeyStoreManager getInstance () {
        return Singleton.instance;
    }


    /* ==============================================
     *  KeyStoreManager Method
     * ============================================== */
    public File getDefaultKeystoreDirectory(){
        File keystoreDir = new File(System.getProperty("user.dir"), "keystore");
        if (! keystoreDir.exists()) {
            //create directory
            keystoreDir.mkdirs();
        }
        return keystoreDir;
    }
    public void createKeystoreJsonData(String privateKey, String alias, String password) {
        try {
            if(privateKey == null || "".equals(privateKey)){
                this.privateKey = SecureRandom.getInstanceStrong().generateSeed(32);
            }else {
                this.setPrivateKey(privateKey);
            }

            this.address = ECKey.fromPrivate(this.privateKey).toString();
            this.keystoreJsonData = KeyStoreUtil.getEncryptKeyStore(this.privateKey, alias, password);

            keystoreJsonObject = new Gson().fromJson(this.keystoreJsonData.toLowerCase(), KeyStoreData.class);



            String downloadFilePath = this.getDefaultKeystoreDirectory().getPath();

            TimeZone time;
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss.SSSZ");

            time = TimeZone.getTimeZone("UTC");
            df.setTimeZone(time);

            this.walletAddress =  this.keystoreJsonObject.address;
            this.keystoreFullPath = downloadFilePath+"/UTC--" + df.format(date) + "--" + this.walletAddress;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void createKeyStoreFileLoad(File openFile) {
        if(openFile != null) {
            String fileName = openFile.getName();
            String absolutePath = openFile.getAbsolutePath();

            try (BufferedReader br = new BufferedReader(new FileReader(openFile))) {
                String sCurrentLine;
                String allText = "";

                while((sCurrentLine = br.readLine()) != null) {
                    sCurrentLine = sCurrentLine.replaceAll(" ","");
                    allText += sCurrentLine.trim();
                }

                KeyStoreData keyStoreData = new Gson().fromJson(allText.toLowerCase(), KeyStoreData.class);
                keystoreJsonObject = keyStoreData;
                this.keystoreJsonData = allText.toLowerCase();
                this.keystoreFullPath = absolutePath;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void downloadKeystore(){
        try{

            String keystoreFullPath = this.keystoreFullPath;
            if (downloadKeyStoreIndex == 0) {
            }else {
                keystoreFullPath = keystoreFullPath + "("+downloadKeyStoreIndex+")";
            }

            FileWriter fileWriter = new FileWriter(keystoreFullPath);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.write(this.keystoreJsonData);
            System.out.println(this.keystoreJsonData);
            bw.close();
            fileWriter.close();

            downloadKeyStoreIndex++;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Delete Keystore file
    public void deleteKeystore() {

        if(downloadKeyStoreIndex > 0) {

            String fileList[] = this.getDefaultKeystoreDirectory().list();

            String fileFullPath = "";
            for(int i=0; i<fileList.length; i++) {
                if(fileList[i].contains(this.walletAddress)) {
                    fileFullPath = this.getDefaultKeystoreDirectory().getPath();
                    System.out.println("fileFullPath  : "+fileFullPath);
                    File deleteFile = new File(fileFullPath+"\\"+fileList[i]);
                    deleteFile.delete();
                }
            }
        }
    }

    public void resetKeystore(){
        downloadKeyStoreIndex = 0;
    }

    public boolean matchPassword(String password) {
        boolean result = false;
        byte[] decryptedKey = new byte[0];
        try {
            System.out.println("address : "+this.keystoreJsonData+", passwd : " +password);
            decryptedKey = KeyStoreUtil.decryptPrivateKey(this.keystoreJsonData,password);
            result = true;

            System.out.println(this.keystoreJsonData);
        } catch (InvalidPasswordException e){
            return result;
        }catch (Exception e) {
            return result;
        }

        return result;
    }

    /* ==============================================
     *  KeyStoreManager Method : Getter
     * ============================================== */
    public String getPrivateKey() {
        return Hex.toHexString(this.privateKey);
    }
    public String getWalletAddress() {return this.walletAddress; }
    public String getKeystoreJsonData(){ return ("".equals(this.keystoreJsonData)) ? null : this.keystoreJsonData; }
    public String getKeystoreFullPath(){ return ("".equals(this.keystoreFullPath)) ? null : this.keystoreFullPath; }
    public String getKeystoreFileName(){return this.keystoreFile.getName();}
    public KeyStoreData getKeystoreJsonObject() { return this.keystoreJsonObject; }

    /* ==============================================
     *  KeyStoreManager Method : Setter
     * ============================================== */
    public String setKeystoreFile(File file){ this.keystoreFile = file; return this.keystoreFile.getName();};
    public void setPrivateKey(String hexPrivateKey){ this.privateKey = Hex.decode(hexPrivateKey); }
    public void setKeystoreJsonData(String keystoreJsonData){ this.keystoreJsonData = keystoreJsonData;}
    public void setKeystoreJsonObject(KeyStoreData keystoreData) { this.keystoreJsonObject = keystoreData; }

}

