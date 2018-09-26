package com.edmi.service.service;

import com.edmi.entity.korea_comments.Korea_comments_target_brands;

/**
 * 2018年9月18日
 * KoreaCommentsCrawlService
 * 接口声明
 */
public interface KoreaCommentsCrawlService {
    /**
     * 读xlsx文件，获得需求里的目标品牌
     *
     * @param filePath
     */
    public void getTargetBrandsWithFilePath(String filePath);

    /**
     * get11stCommentWithBrand
     * 按品牌获得评论
     */
    public void get11stCommentWithBrand(Korea_comments_target_brands brand);

    /**
     * 11st
     * 解析评论
     */
    public void get11stCommentDetail(String url);
}
