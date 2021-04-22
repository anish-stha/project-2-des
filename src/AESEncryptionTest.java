/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AESEncryptionTest {
    
    //Simple test for AES encryption
    public static void SimpleAESTest(){
        AESEncryption aesengine = new AESEncryption();
        //String thePlainText = "A quick brown fox jumps over the lazy dog";
        String cipher = null;
        Scanner scanner = new Scanner( System.in );
        System.out.println("Enter the password:");
        String password = scanner.nextLine();
        System.out.println("Enter the plain text:");
        String thePlainText = scanner.nextLine();
        System.out.println("#####################################");
        
        System.out.println(String.format("Plain Text:\t %s", thePlainText));
        try {
            //cipher = aesengine.encrypt("easypassword", thePlainText);
            cipher = aesengine.encrypt(password,thePlainText);
            System.out.println(String.format("Encoded:\t %s", cipher));
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(AESEncryptionTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            String decrypted = aesengine.decrypt(password, cipher );
            System.out.println(String.format("Decrypted:\t %s", decrypted));
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(AESEncryptionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    
    //Get filenames in a list
    public static List<String> listFilesForFolder(File folder) {
        System.out.println("Reading file names");
        List<String> names = new ArrayList<String>();
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                //listFilesForFolder(fileEntry);
                //skip
            } else {
                System.out.println(fileEntry.getName());
                names.add(fileEntry.getName());
            }
        }
        return names;
    }
   
    //Test for file operations
    public static void filesAESTest(String password, String path){
        final File folder = new File(path);
        List<String> names = listFilesForFolder(folder);
        for (int i = 0; i < names.size(); i++) {
            System.out.println("Currently in ");
            System.out.println(names.get(i));
            try {
                encryptForFile(password, path + names.get(i), path + "Encrypted/enc" +names.get(i));
            } catch (IOException ex) {
                Logger.getLogger(AESEncryptionTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (int i = 0; i < names.size(); i++) {
            try {
                decryptForFile(password, path + "Encrypted/" + "enc" + names.get(i), path + "Decrypted/dec" +names.get(i));
            } catch (IOException ex) {
                Logger.getLogger(AESEncryptionTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        }
        
    }
    
    
    //encryption fucntion for one file
    public static void encryptForFile(String password, String readName, String writeName) throws IOException {   
        System.out.println(String.format ("Starting Encryption for %s", readName));
        
        RandomAccessFile aFile = new RandomAccessFile(readName, "r");
        FileChannel inChannel = aFile.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        AESEncryption aesengine = new AESEncryption();
        BufferedWriter bw = new BufferedWriter(new FileWriter(writeName));
        String chunk = null;
        while(inChannel.read(byteBuffer) > 0)
        {
            
            ArrayList<Character> charList = new ArrayList<Character>();
//            Charset charset = Charset.forName("UTF-8");
//            String chunk = charset.decode(byteBuffer).toString();
//            System.out.println(chunk);
            byteBuffer.flip();
            for (int i = 0; i < byteBuffer.limit(); i++) {
                    //System.out.print((char) byteBuffer.get());
                    charList.add((char) byteBuffer.get());
                    
            }
            chunk = getStringRepresentation(charList);
            System.out.println(chunk);
            try {
                String cipher = aesengine.encrypt(password, chunk);
                bw.write(cipher);
            } catch (GeneralSecurityException ex) {
                Logger.getLogger(AESEncryptionTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            byteBuffer.clear(); 
        }
        
        //closing
        bw.close();
        inChannel.close();
        aFile.close();
        
    }    
    
    
    //decryption fucntion for one file
    public static void decryptForFile(String password, String readName, String writeName) throws IOException {   
        System.out.println(String.format ("Starting Decryption for %s", readName));
        
        RandomAccessFile aFile = new RandomAccessFile(readName, "r");
        FileChannel inChannel = aFile.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        AESEncryption aesengine = new AESEncryption();
        BufferedWriter bw = new BufferedWriter(new FileWriter(writeName));
        String chunk = null;
        while(inChannel.read(byteBuffer) > 0)
        {
            
            ArrayList<Character> charList = new ArrayList<Character>();
//            Charset charset = Charset.forName("UTF-8");
//            String chunk = charset.decode(byteBuffer).toString();
//            System.out.println(chunk);
            byteBuffer.flip();
            for (int i = 0; i < byteBuffer.limit(); i++) {
                    //System.out.print((char) byteBuffer.get());
                    charList.add((char) byteBuffer.get());
                    
            }
            chunk = getStringRepresentation(charList);
            System.out.println(chunk);
            try {
                String cipher = aesengine.decrypt(password, chunk);
                bw.write(cipher);
            } catch (GeneralSecurityException ex) {
                Logger.getLogger(AESEncryptionTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            byteBuffer.clear(); 
        }
        
        //closing
        bw.close();
        inChannel.close();
        aFile.close();
        
    }    

    //Get String from Arraylist of chars
    //used when using arraylist<Character> to append byteBuffer while reading file
    public static String getStringRepresentation(ArrayList<Character> list)
    {    
        StringBuilder builder = new StringBuilder(list.size());
        for(Character ch: list)
        {
            builder.append(ch);
        }
        return builder.toString();
    }
    public static void main(String[] args) {
        
        Scanner scanner = new Scanner( System.in );
        System.out.println("1. Simple test \n");
        System.out.println("2. File encryptions in a folder \n");
        System.out.println("(Currently using F:/Encryptions/Tests/)");
        System.out.println("(Currently using F:/Encryptions/Tests/Encrypted/)");
        System.out.println("(Currently using F:/Encryptions/Tests/Decrypted/)");        
        int what = scanner.nextInt();
        if (what == 1){
            SimpleAESTest();
        }
        else{
            filesAESTest("hello", "F:/Encryptions/Tests/");
        }
    }
}
    

