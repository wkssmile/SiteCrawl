package com.edmi.utils.casperjs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * casperjs应用接口类
 *
 * @author yi.zhang
 */
public class Casperjs {

    public interface Item {
        String URL = "url";
        String TITLE = "title";
        String CONTENT = "content";
        String URLANDTITLE = "urlAndtitle";
        String TILANDPAGE = "tilAndPage";

    }

    public static String doCasperjs(CasperArgs arg) throws IOException, InterruptedException {
        String cmd = getCmd(arg);
        String result = null;
        if (cmd != null)
            result = getAjaxCotnent(cmd);
        return result;
    }

    public static String getCmd(CasperArgs arg) {
        String cmd = "casperjs ";
        String jsfile = arg.getJsFile();
        String url = arg.getUrl();
        String item = arg.getItem();
        Integer timeout = arg.getTimeout();

        if (jsfile == null || jsfile.isEmpty())
            return null;
        if (url == null || url.isEmpty())
            return null;
        if (item == null || item.isEmpty())
            return null;
        if (timeout == null)
            return null;

        if (!url.contains("http://") && !url.contains("https://")) {
            url = "http://" + url;
        }
        if (url.contains("&")) {
            url.replace("&", "\\" + "&");
        }
        if (timeout <= 5000) {
            timeout = 5000;
        }

        cmd += jsfile;
        cmd += " --url=" + url;
        cmd += " --timeout=" + timeout;
        cmd += " --item=" + item;
        System.out.println(cmd);
        return cmd;
    }

    public static String getAjaxCotnent(String cmd) throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(cmd);
        InputStream is = p.getInputStream();
//BufferedReader br = new BufferedReader(new InputStreamReader(is));
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuffer sbf = new StringBuffer();
        String tmp = "";
        while ((tmp = br.readLine()) != null) {
            sbf.append(tmp.trim());
        }
//        System.out.println("---------" + sbf.toString());
        return sbf.toString();
    }

    public static String getDomain(String url) {
        String pattern = "((\\w+\\.){2,3}\\w+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(url);
        if (m.find()) {
            return m.group(0);
        }
        return null;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Runtime r = Runtime.getRuntime();
        Process p = null;
        try {
            String s = "casperjs /home/cxfang/software/casperjs/getpage.js --url=https://www.baidu.com --timeout=50000 --item=content takes 193";
            p = r.exec(s);
        } catch (Exception e) {
            System.out.println("错误:" + e.getMessage());
            e.printStackTrace();
        }

    }
}

