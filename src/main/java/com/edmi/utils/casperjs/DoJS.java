package com.edmi.utils.casperjs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DoJS {
    public static String getAjaxCotnent(String url) throws IOException {
        Runtime rt = Runtime.getRuntime();

        Process p = rt.exec("casperjs D:/tools/casperjs/casperjs-1.1.4-1/getpage.js --url=" + url + " --timeout=600000 --item=content");
        InputStream is = p.getInputStream();
//BufferedReader br = new BufferedReader(new InputStreamReader(is));
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuffer sbf = new StringBuffer();
        String tmp = "";
        while ((tmp = br.readLine()) != null) {
            sbf.append(tmp.trim());
        }
        System.out.println("---------" + sbf.toString());
        return sbf.toString();
    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        String url = "http://deal.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo=934901081";
        String content = getAjaxCotnent(url);
        System.out.println(content);
        long end = System.currentTimeMillis();
        System.out.println("本次访问耗时： " + ((end - start) / 1000) + "  s ");

    }
}
