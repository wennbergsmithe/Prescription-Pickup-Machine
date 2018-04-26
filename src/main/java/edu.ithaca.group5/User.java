package edu.ithaca.group5;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

public class User {
    long id;
    String name;
    String username;
    String password;
    String passwordSalt;
    double balance;
    boolean isFrozen;
    String allergies = "";


    protected User(long id, String name, String username, String password, boolean isFrozen, String passwordSalt, double balance, String allergies) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.isFrozen = isFrozen;
        this.passwordSalt = passwordSalt;
        this.allergies = allergies;
    }


    public User(long id, String name, String username, String password, boolean isFrozen, double balance, String allergies) {
        this(id, name, username, password, balance, isFrozen);
        this.allergies = allergies;
    }

    protected User(long id, String name, String username, String password, double balance, boolean isFrozen) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.balance = balance;
        this.isFrozen = isFrozen;

        Random random = new Random();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = f.generateSecret(spec).getEncoded();
            Base64.Encoder enc = Base64.getEncoder();
            passwordSalt = enc.encodeToString(salt);
            this.password = enc.encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public boolean isPassword(String password) {
        Base64.Decoder dec = Base64.getDecoder();
        byte[] salt = dec.decode(passwordSalt);
        try {
            String hashString = hashPasswordAndSalt(password, salt);
            return hashString.equals(this.password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String hashPasswordAndSalt(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory f = null;
        f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = f.generateSecret(spec).getEncoded();
        Base64.Encoder enc = Base64.getEncoder();
        String hashString = enc.encodeToString(hash);
        return hashString;
    }

    public static String hashPasswordAndSalt(String password, String passwordSalt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Base64.Decoder dec = Base64.getDecoder();
        byte[] salt = dec.decode(passwordSalt);
        return hashPasswordAndSalt(password, salt);

    }


    /**
     * Gets the type of this user
     * @return the type of this user
     */
    public String getType() {
        String className = this.getClass().getName();
        className = className.toLowerCase();
        String[] splitType = className.split("\\.");
        return splitType[splitType.length - 1];
    }
}
