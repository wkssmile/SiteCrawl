package com.edmi;

import com.edmi.dao.korea_comments.Korea_comments_target_brandsDao;
import com.edmi.entity.korea_comments.Korea_comments_target_brands;
import com.edmi.service.service.KoreaCommentsCrawlService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KoreaCommentsCrawlerTest {
    static Logger log = Logger.getLogger(KoreaCommentsCrawlerTest.class);
    @Autowired
    private KoreaCommentsCrawlService koreaCommentsCrawlService;
    @Autowired
    private Korea_comments_target_brandsDao korea_comments_target_brandsDao;

    @Test
    public void getTargetBrandsManager() {
        String filePath = "E:\\data\\ProjectDescription\\Korea_Comments\\Korea_Comments_Crawl_20180917_1520.xlsx";
        koreaCommentsCrawlService.getTargetBrandsWithFilePath(filePath);
    }

    @Test
    public void get11StreetList() {
        log.info("***** get11StreetList start *****");
        List<Korea_comments_target_brands> korea_comments_target_brandsList = korea_comments_target_brandsDao.findAll();
        if (CollectionUtils.isNotEmpty(korea_comments_target_brandsList)) {
            log.info("- get brands num :" + korea_comments_target_brandsList.size());
            for (int i = 0; i < korea_comments_target_brandsList.size(); i++) {
                Korea_comments_target_brands targetBrand = korea_comments_target_brandsList.get(i);
                log.info("---------- will extra target Brand:" + i);
                koreaCommentsCrawlService.get11stCommentWithBrand(targetBrand);
            }
        } else {
            log.info("- has no brands from Korea_comments_target_brands");
        }
        log.info("***** get11StreetList task over *****");
    }

    @Test
    public void get11StreetComment() {
        String url = "http://global.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo=934901081";
        koreaCommentsCrawlService.get11stCommentDetail(url);

    }

}
