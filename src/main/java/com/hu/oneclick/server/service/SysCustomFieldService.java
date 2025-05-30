package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.SysCustomFieldVo;

import java.util.List;

public interface SysCustomFieldService {

    Resp<List<SysCustomFieldVo>> querySysCustomFields();

    Resp<String> updateSysCustomFields(SysCustomFieldVo sysCustomFieldVo);

    Resp<SysCustomFieldVo> getSysCustomField(String fieldName);
}
