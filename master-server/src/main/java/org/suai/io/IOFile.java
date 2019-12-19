package org.suai.io;

import java.io.*;
import java.util.HashMap;

public class IOFile {
    private static String filename;

    public IOFile() {
        filename = "/home/dmbldr/Study/Java/Course/MultiPaint/master-server/src/main/resources/users.txt";
    }
    public IOFile(String filename) {
        IOFile.filename = filename;
    }

    public HashMap<String, String> initUsers() {
        try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String s;
            HashMap<String, String> tmp = new HashMap<>();
            while ((s = br.readLine()) != null) {
                String[] login = s.split(";");
                tmp.put(login[0], login[1]);
            }
            return tmp;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void changeDataBase(HashMap<String, String> users) {
        File oldFile = new File(filename);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(oldFile, false))) {
            for(HashMap.Entry<String, String> it : users.entrySet()) {
                bw.write(it.getKey() + ";" + it.getValue() + "\n");
                bw.flush();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean registration(String name, String password) {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(filename, true))) {
            br.write(name + ";" + password + "\n");
            br.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
}
