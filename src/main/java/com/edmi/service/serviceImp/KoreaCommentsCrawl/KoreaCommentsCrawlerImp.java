package com.edmi.service.serviceImp.KoreaCommentsCrawl;

import com.alibaba.fastjson.JSONObject;
import com.edmi.dao.korea_comments.Korea_comments_target_brandsDao;
import com.edmi.entity.korea_comments.Korea_comments_target_brands;
import com.edmi.service.service.KoreaCommentsCrawlService;
import com.edmi.utils.casperjs.Request_demo;
import com.edmi.utils.http.HttpClientUtil;
import com.edmi.utils.http.request.Request;
import com.edmi.utils.http.request.RequestMethod;
import com.edmi.utils.http.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service("KoreaCommentsCrawlService")
public class KoreaCommentsCrawlerImp implements KoreaCommentsCrawlService {
    static Logger log = Logger.getLogger(KoreaCommentsCrawlerImp.class);
    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";
    @Autowired
    private Korea_comments_target_brandsDao korea_comments_target_brandsDao;

    public Workbook getWorkbook(File file) throws IOException {
        Workbook wb = null;
        FileInputStream in = new FileInputStream(file);
        if (file.getName().endsWith(EXCEL_XLS)) {  //Excel 2003
            wb = new HSSFWorkbook(in);
            System.out.println("Excel file type:xls");
        } else if (file.getName().endsWith(EXCEL_XLSX)) {  // Excel 2007/2010
            wb = new XSSFWorkbook(in);
            System.out.println("Excel file type:xlsx");
        }
        return wb;
    }

    @Override
    public void getTargetBrandsWithFilePath(String filePath) {
        List<Korea_comments_target_brands> korea_comments_target_brandsList = new ArrayList<>(100);
        try {
            File file = new File(filePath);
            Workbook workbook = getWorkbook(file);
            //名称为 Target Brands 的sheet
            Sheet sheet = workbook.getSheet("Target Brands");
            int rowLength = sheet.getLastRowNum() + 1;
            System.out.println("Total number of rows:" + rowLength);
            for (int i = 0; i < rowLength; i++) {
                //一行
                Row row = sheet.getRow(i);
                int rowSize = row.getLastCellNum();
//                System.out.println("row size :" + rowSize);
                String lastrow = row.getCell(rowSize - 1).toString();
//                System.out.println("lastrow  :" + lastrow);
                //跳过第一行
                if (lastrow.contains("Brand Name")) {
                    continue;
                }
                if (rowSize == 3) {
                    Cell cell1 = row.getCell(0);
                    Cell cell2 = row.getCell(1);
                    Cell cell3 = row.getCell(2);
                    String brand_indexstr = cell1.toString();
                    int brand_index = 0;
                    if (StringUtils.isNotEmpty(brand_indexstr)) {
                        if (brand_indexstr.contains(".")) {
                            brand_indexstr = StringUtils.substringBefore(brand_indexstr, ".");
                            brand_index = Integer.valueOf(brand_indexstr);
                        } else {
                            brand_index = Integer.valueOf(brand_indexstr);
                        }
                        String brand_status = cell2.toString();
                        if (StringUtils.isNotEmpty(brand_status)) {
                            String brand_name = cell3.toString();
                            if (StringUtils.isNotEmpty(brand_status)) {
                                log.info("index:" + brand_index + "   Status:" + brand_status + "    Brand Name:" + brand_name);
                                Korea_comments_target_brands targetBrandModel = new Korea_comments_target_brands();
                                targetBrandModel.setBrand_index(brand_indexstr);
                                targetBrandModel.setBrand_status(brand_status);
                                targetBrandModel.setBrand_name(brand_name);
                                targetBrandModel.setInsertTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
                                targetBrandModel.setUpdateTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
                                korea_comments_target_brandsList.add(targetBrandModel);
                            } else {
                                log.error("brand_name null");
                            }
                        } else {
                            log.error("brand_name null");
                        }
                    } else {
                        log.error("brand_index null");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        korea_comments_target_brandsDao.saveAll(korea_comments_target_brandsList);
    }

    @Override
    public void get11stCommentWithBrand(Korea_comments_target_brands brands) {
        String brandName = brands.getBrand_name();
        if (StringUtils.isNotEmpty(brandName)) {
            log.info("----- getGmarketCommentWithBrand :" + brands.getBrand_name());
            try {
                Boolean notLast = true;
                int turnPageNum = 1;
                while (notLast) {
                    String url = "http://globalsearch.11st.co.kr/SearchPrdEnAction.tmall?method=getSearchFilterAjax" +
                            "&kwd=" + brandName +
                            "&method=getTotalSearchSeller" +
                            "&pageNum=" + turnPageNum +
                            "&pageSize=40&sortCd=A";
//                    String url = "http://search.11st.co.kr/Search.tmall";
                    Request request = new Request(url, RequestMethod.GET);
//                    request.addUrlParam("kwd", brandName + "#pageNum%%" + 2);
                    Response response = HttpClientUtil.doRequest(request);
                    log.info("----- will visit brandName:" + brandName + " page num:" + turnPageNum + " url:" + request.getUrl());
                    int code = response.getCode();
                    if (code == 200) {
                        String content = response.getResponseText();
//                        log.info(content);
                        // 验证页面
                        if (StringUtils.isNotBlank(content)) {
                            // 验证是否是正常页面
                            if (content.contains("Goods list")) {
                                JSONObject jo = JSONObject.parseObject(content);
                                if (jo.containsKey("template")) {
                                    String html = jo.getString("template");
//                                    log.info("html: " + html);
                                    if (StringUtils.isNotEmpty(html)) {
                                        Document doc = Jsoup.parse(html);
                                        Elements lineseles = doc.select("div.total_listing_wrap > ul.tt_listbox.sell_sec >li");
                                        if (lineseles != null && lineseles.size() > 0) {
                                            log.info("--- this page has item: " + lineseles.size());
                                            for (int i = 0; i < lineseles.size(); i++) {
                                                Element linele = lineseles.get(i);
                                                log.info("===============================第" + turnPageNum + "页，第" + i + "行");
                                                //商品图片
                                                Elements photoeles = linele.select("div.photo_wrap > a > img");
                                                if (photoeles != null && photoeles.size() > 0) {
                                                    String photo_url = photoeles.attr("src").trim();
                                                    log.info("photo_url:" + photo_url);
                                                }
                                                //商品链接product_link
                                                Elements product_linkeles = linele.select("div.photo_wrap > a");
                                                if (product_linkeles != null && product_linkeles.size() > 0) {
                                                    String product_link = product_linkeles.attr("href").trim();
                                                    log.info("product_link:" + product_link);
                                                }
                                                //正面反馈 好评率 product_favorable_rate
                                                Elements product_infoseles = linele.select("div.list_info div.info_btm > span.sfc >b");
                                                if (product_infoseles != null && product_infoseles.size() > 0) {
                                                    String product_favorable_rate = product_infoseles.text().trim();
                                                    log.info("product_favorable_rate: " + product_favorable_rate);
                                                }
                                                //愿望清单  心数量 product_like_count
                                                Elements product_like_countseles = linele.select("div.list_info div.def_likethis strong");
                                                if (product_like_countseles != null && product_like_countseles.size() > 0) {
                                                    String product_like_count = product_like_countseles.text().trim();
                                                    product_like_count = product_like_count.replaceAll(",", "").trim();
                                                    log.info("product_like_count: " + product_like_count);
                                                }

                                                //商品价格
                                                Elements priceles = linele.select("div.list_price");
                                                if (priceles != null && priceles.size() > 0) {
                                                    //原价 origin_price
                                                    Elements origineles = priceles.select("span.ori");
                                                    if (origineles != null && origineles.size() > 0) {
                                                        String origin_price = origineles.text().trim();
                                                        origin_price = origin_price.replaceAll("original price", "").replaceAll("~", "")
                                                                .replaceAll(",", "").trim();
                                                        log.info("origin_price:" + origin_price);
                                                    }
                                                    //折扣 price_discount_rate
                                                    Elements discount_rateeles = priceles.select("span.rm");
                                                    if (discount_rateeles != null && discount_rateeles.size() > 0) {
                                                        String price_discount_rate = discount_rateeles.text().trim();
                                                        price_discount_rate = price_discount_rate.replaceAll("Discount", "").trim();
                                                        log.info("price_discount_rate:" + price_discount_rate);
                                                    }
                                                    //价格 售价 price
                                                    Elements product_priceles = priceles.select("span.prc");
                                                    if (product_priceles != null && product_priceles.size() > 0) {
                                                        String product_price = product_priceles.text().trim();
                                                        product_price = product_price.replaceAll("price", "").replaceAll(",", "")
                                                                .replaceAll("~", "").trim();
                                                        log.info("product_price:" + product_price);
                                                    }
                                                    //价格  美元 product_price_dollar 接口 无此值
//                                                    Elements product_price_dollareles = priceles.select("span.dlr");
//                                                    if (product_price_dollareles != null && product_price_dollareles.size() > 0) {
//                                                        String product_price_dollar = product_price_dollareles.text().trim();
//                                                        log.info("product_price_dollar:" + product_price_dollar);
//                                                    }
                                                }
                                                //卖家seller
                                                Elements seller_eles = linele.select("div.list_benefit");
                                                if (seller_eles != null && seller_eles.size() > 0) {
                                                    Elements seller_titleles = seller_eles.select("p.benefit_tit > a");
                                                    if (seller_titleles != null && seller_titleles.size() > 0) {
                                                        String product_seller_name = seller_titleles.text().trim();
                                                        String product_seller_url = seller_titleles.attr("href");
                                                        log.info("product_seller_name:" + product_seller_name);
                                                        log.info("product_seller_url:" + product_seller_url);
                                                    }
                                                    //卖家水平 product_seller_level
                                                    Elements product_seller_leveleles = seller_eles.select("p.seller_lev > span");
                                                    if (product_seller_leveleles != null && product_seller_leveleles.size() > 0) {
                                                        String product_seller_level = product_seller_leveleles.text().trim();
                                                        log.info("product_seller_level:" + product_seller_level);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    int pageNum = 0;
                                    int totalCount = 0;
                                    if (jo.containsKey("pageNum")) {
                                        String pageNumStr = jo.getString("pageNum");
                                        if (StringUtils.isNotEmpty(pageNumStr)) {
                                            log.info("pageNumStr:" + pageNumStr);
                                            pageNum = Integer.valueOf(pageNumStr);
                                        }
                                    }
                                    if (jo.containsKey("totalCount")) {
                                        String totalCountStr = jo.getString("totalCount");
                                        if (StringUtils.isNotEmpty(totalCountStr)) {
                                            log.info("totalCountStr:" + totalCountStr);
                                            totalCount = Integer.valueOf(totalCountStr);
                                        }
                                    }
                                    if (pageNum * 40 > totalCount) {
                                        notLast = false;
                                        log.info("--- last page: " + pageNum);
                                    }
                                } else {
                                    log.info("--- this page has no template");
                                }
                            } else {
                                log.error("!!! page is not usually");
                            }
                        } else {
                            log.error("!!! page is null:" + url);
                        }
                        //页面访问正常，翻页
                        turnPageNum++;
                        notLast = false;
                    } else {
                        log.error("!!! bad request:" + code + " - " + url);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.error("!!! Brand_name is null");
        }
    }

    @Override
    public void get11stCommentDetail(String url) {
        log.info("get11stCommentDetail");
        try {
            if (StringUtils.isNotEmpty(url)) {
                url = url.replaceAll("global.11st.co.kr", "deal.11st.co.kr");
                log.info("url:" + url);
                Request request = new Request(url, RequestMethod.GET);
                Response response = HttpClientUtil.doRequest(request);
                int code = response.getCode();
                if (code == 200) {
                    String content = response.getResponseText();
                    if (StringUtils.isNotEmpty(content)) {
                        if (content.contains("wrapBody")) {
//                            log.info(content);
                            Document doc = Jsoup.parse(content);
                            //解析国际版
                            extra11stCommentDetailKorea(doc);

                        } else {
                            log.error("!!! is not usually page," + url);
                        }
                    } else {
                        log.error("!!! page is null:" + url);
                    }
                } else {
                    log.error("!!! bad request:" + code + " - " + url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 11st
     * 国际版
     */
    public void extra11stCommentDetailGlobal(Document doc) {
        try {
            //商品名称 product_name
            Elements product_nameles = doc.select("div.inner1 >h2#_prdNm > span");
            if (product_nameles != null && product_nameles.size() > 0) {
                String product_name = product_nameles.text().trim();
                log.info("product_name:" + product_name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 11st
     * 韩国版
     */
    public void extra11stCommentDetailKorea(Document doc) {
        try {
            //商品名称 product_name
            Elements product_nameeles = doc.select("div.heading >h2");
            if (product_nameeles != null && product_nameeles.size() > 0) {
                String product_name = product_nameeles.text().trim();
                log.info("product_name:" + product_name);
            }
            //商品 分类
            String content = doc.toString();
            if (content.contains("CombineSearch.PowerLink.requestData")) {
                String categoryStr = StringUtils.substringBetween(content, "CombineSearch.PowerLink.requestData({", "});");
//                log.info("categoryStr:" + categoryStr);
                if (StringUtils.isNotEmpty(categoryStr)) {
                    if (categoryStr.contains("cat1")) {
                        JSONObject jo = JSONObject.parseObject("{" + categoryStr + "}");
                        if (jo.containsKey("cat1")) {
                            String cate1 = jo.getString("cat1");
                            log.info("cate1:" + cate1);
                        }
                        if (jo.containsKey("cat2")) {
                            String cate2 = jo.getString("cat2");
                            log.info("cate2:" + cate2);
                        }
                        if (jo.containsKey("cat3")) {
                            String cate3 = jo.getString("cat3");
                            log.info("cate3:" + cate3);
                        }
                        if (jo.containsKey("cat4")) {
                            String cate4 = jo.getString("cat4");
                            log.info("cate4:" + cate4);
                        }
                    }
                }
            }
            //商品评论

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        KoreaCommentsCrawlerImp k = new KoreaCommentsCrawlerImp();
        String filepath = "E:\\data\\ProjectDescription\\Korea_Comments\\Korea_Comments_Crawl_20180917_1520.xlsx";
//        k.getTargetBrandsWithFilePath(filepath);
        Korea_comments_target_brands brand = new Korea_comments_target_brands();
        brand.setBrand_name("Innisfree");
//        k.get11stCommentWithBrand(brand);
        //http://deal.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo=934901081
        String url = "http://global.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo=934901081";
//        k.get11stCommentDetail(url);
        String content = Request_demo.getJsContent(url);
        log.info(content);
    }

}
