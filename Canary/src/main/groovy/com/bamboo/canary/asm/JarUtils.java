package com.bamboo.canary.asm;

import com.android.build.api.transform.JarInput;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class JarUtils {

    public static void writeJarFile(JarInput jarInput, String entryName, byte[] data) throws Exception{
        File file = jarInput.getFile();
        JarFile jarFile = new JarFile(file);

        TreeMap tm = new TreeMap();
        Enumeration es = jarFile.entries();
        while(es.hasMoreElements()){
            JarEntry je = (JarEntry)es.nextElement();
            byte[] b = readStream(jarFile.getInputStream(je));
            tm.put(je.getName(),b);
        }

        JarOutputStream jos = new JarOutputStream(new FileOutputStream(file));
        Iterator it = tm.entrySet().iterator();
        boolean has = false;

        while(it.hasNext()){
            Map.Entry item = (Map.Entry) it.next();
            String name = (String)item.getKey();
            JarEntry entry = new JarEntry(name);
            jos.putNextEntry(entry);
            byte[] temp ;
            if(name.equals(entryName)){
                temp = data;
                has = true ;
            }else{
                temp = (byte[])item.getValue();
            }
            jos.write(temp, 0, temp.length);
        }

        if(!has){
            JarEntry newEntry = new JarEntry(entryName);
            jos.putNextEntry(newEntry);
            jos.write(data, 0, data.length);
        }
        jos.finish();
        jos.close();

    }


    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }


}
