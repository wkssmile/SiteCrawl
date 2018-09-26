package com.edmi.utils.casperjs;

import java.io.IOException;

public class Request_demo {
    public static String getJsContent(String url) {
        long start = System.currentTimeMillis();
        CasperArgs arg = new CasperArgs();
        arg.setJsFile("D:/tools/casperjs/casperjs-1.1.4-1/getpage.js");
        arg.setUrl(url);
        arg.setTimeout(100000);
        arg.setItem(Casperjs.Item.CONTENT);
        String result = null;
        try {
            result = Casperjs.doCasperjs(arg);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("- Casperjs request cost:" + ((end - start) / 1000) + "s");
        return result;
    }
}
