package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.TestCaseTemplateJson;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

/**
 * TestCaseTemplateJsonDAO继承基类
 */
public interface TestCaseTemplateJsonDAO extends BaseMapper<TestCaseTemplateJson> {


    List<TestCaseTemplateJson> queryByUserId(@Param("masterId") String masterId);

    int insert(TestCaseTemplateJson testCaseTemplateJson);


    int update(TestCaseTemplateJson testCaseTemplateJson);

    int deleteById(String id);

    TestCaseTemplateJson selectById(@Param("id") String id);
}