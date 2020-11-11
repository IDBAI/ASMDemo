package com.bamboo.canary


class IOUtils {
    public static byte[] read(InputStream ins) throws IOException {
        ByteArrayOutputStream ous = new ByteArrayOutputStream()
        int len
        byte[] buffer = new byte[4096]
        while ((len = ins.read(buffer)) != -1)
        {
            ous.write(buffer,0,len)
        }
        byte[] bytes = ous.toByteArray()
        ous.close()
        return bytes
    }
}