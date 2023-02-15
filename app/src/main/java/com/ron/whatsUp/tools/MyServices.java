package com.ron.whatsUp.tools;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;

public class MyServices {
    private static MyServices _instance = null;
    private final int PHONE_LEN = 13;
    private Context context;
    byte[] keyBytes = {0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, (byte) 0x80, (byte) 0x90, (byte) 0xa0, (byte) 0xb0, (byte) 0xc0, (byte) 0xd0, (byte) 0xe0, (byte) 0xf0, 0x00};
    SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
    private HashMap<String, String> my_contacts = new HashMap<>();

    private MyServices(Context context) {
        this.context = context.getApplicationContext();

    }

    public static void initHelper(Context context) {
        if (_instance == null) {
            _instance = new MyServices(context);
        }
    }

    public static MyServices getInstance(){
        return _instance;
    }
    public void update_app_lang(String lan, AppCompatActivity appCompatActivity){
        if(!lan.equals(DataManager.HEBREW) && !lan.equals(DataManager.ENGLISH))
            return;

        String locale_lang = null;


        if(lan.equals(DataManager.ENGLISH)){
            locale_lang = "en";
        }
        else if(lan.equals(DataManager.HEBREW)){
            locale_lang = "iw";
        }

        Resources res = appCompatActivity.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(locale_lang));
        res.updateConfiguration(conf, dm);
    }
    public HashMap<String, String> get_all_contacts(AppCompatActivity appCompatActivity){
        Cursor phones = appCompatActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String phone = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String name = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            if (phone.contains("+972")) {
                phone = phone.replaceAll("[^\\d]", "");
                phone = "+" + phone;
                if (phone.length() == PHONE_LEN)
                    this.my_contacts.put(phone, name);
            } else if (phone.startsWith("05")) {
                phone = phone.replaceAll("[^\\d]", "");
                phone = phone.replaceFirst("0", "+972");
                if (phone.length() == PHONE_LEN)
                    this.my_contacts.put(phone, name);
            }

        }
        phones.close();
        return this.my_contacts;
    }
    public String encrypt(String msg){
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String encryptedString = Base64.getEncoder().encodeToString(encrypted);
                return encryptedString;
            }
        }
        catch (Exception exp){

        }
        return msg;
    }
    public String decrypt(String msg){
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decrypted = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                decrypted = cipher.doFinal(Base64.getDecoder().decode(msg));
            }
            String decryptedString = new String(decrypted, StandardCharsets.UTF_8);
            return decryptedString;
        }
        catch (Exception exp){

        }
        return msg;
    }
}
