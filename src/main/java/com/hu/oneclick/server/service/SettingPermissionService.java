package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.model.domain.SysOperationAuthority;
import com.hu.oneclick.model.domain.dto.SubUserPermissionDto;

import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
public interface SettingPermissionService {

    Resp<SubUserPermissionDto> getPermissions(String subUserId,String projectId);

    Resp<String> updatePermissions(SubUserPermissionDto entity);

    Resp<List<Project>> getProjects(String subUserId);
}
