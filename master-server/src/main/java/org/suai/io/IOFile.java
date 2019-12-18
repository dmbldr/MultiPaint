package org.suai.io;

import java.io.*;
import java.util.HashMap;

public class IOFile {
    private static final String filename = "/home/dmbldr/Study/Java/Course/MultiPaint/master-server/src/main/resources/users.txt";

    public IOFile() {

    }

    public static HashMap<String, String> initUsers() {
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

    public static void changeDataBase(HashMap<String, String> users) {
        File oldFile = new File(filename);
        File newFile = new File("/home/dmbldr/Study/Java/Course/MultiPaint/master-server/src/main/resources/users1.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(newFile, false))) {
            for(HashMap.Entry<String, String> it : users.entrySet()) {
                bw.write(it.getKey() + ";" + it.getValue() + "\n");
                bw.flush();
            }
            oldFile.delete();
            newFile.renameTo(oldFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean registration(String name, String password) {
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
