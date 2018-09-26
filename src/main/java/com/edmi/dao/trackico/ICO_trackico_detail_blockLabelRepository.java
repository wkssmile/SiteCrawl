package com.edmi.dao.trackico;

import org.springframework.data.jpa.repository.JpaRepository;
import com.edmi.entity.trackico.ICO_trackico_detail_blockLabel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author keshi
 * @ClassName: ICO_trackico_detail_blockLabelRepository
 * @Description: 负责 ICO_trackico_detail_blockLabel模型的数据库操作
 * @date 2018年8月3日 下午3:57:55
 */
public interface ICO_trackico_detail_blockLabelRepository extends JpaRepository<ICO_trackico_detail_blockLabel, Long> {
    //删除detail
    @Transactional
    @Modifying
    @Query("delete from ICO_trackico_detail_blockLabel  where fk_id = :fk_id")
    int deleteICO_trackico_detail_blockLabelByPk_id(@Param("fk_id") long fk_id);

    @Query("select d from ICO_trackico_detail_blockLabel d where d.ico_trackico_detail.pk_id = :fk_id")
    List<ICO_trackico_detail_blockLabel> getICO_trackico_detail_blockLabelByFkId(@Param("fk_id") long detailPkId);

}