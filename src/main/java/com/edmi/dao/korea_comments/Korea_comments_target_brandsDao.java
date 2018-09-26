package com.edmi.dao.korea_comments;

import com.edmi.entity.korea_comments.Korea_comments_target_brands;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Korea_comments_target_brands 对象的数据库操作
 */
public interface Korea_comments_target_brandsDao extends JpaRepository<Korea_comments_target_brands, Long> {

    List<Korea_comments_target_brands> findAll();
}
