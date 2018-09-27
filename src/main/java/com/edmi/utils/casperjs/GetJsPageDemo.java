package com.edmi.utils.casperjs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class GetJsPageDemo {
    public static void main(String[] args) {
        String url = "https://www.baidu.com/";
        String content = CasperJsRequest.getJsContent(url);
        System.out.println(content);
        Document doc = Jsoup.parse(content);
        System.out.println("title ----- "+doc.title());
        Elements eles = doc.select("span.bg.s_btn_wr");
        String st = eles.attr("value");
        System.out.println("----- "+st);

    }
}
