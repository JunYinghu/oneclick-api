package com.hu.oneclick.model.domain.param;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hu.oneclick.model.domain.Sprint;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: jhh
 * @Date: 2023/4/24
 */
@Getter
@Setter
@Schema(name = "迭代Param")
public class SprintParam implements Serializable {
    @Schema(name = "名称")
    private String title;

    @Schema(name = "项目ID")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    public LambdaQueryWrapper getQueryCondition() {
        LambdaQueryWrapper<Sprint> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(this.title), Sprint::getTitle, this.title);
        queryWrapper.eq(null != this.projectId, Sprint::getProjectId, this.projectId);
        queryWrapper.orderByDesc(Sprint::getCreateTime);
        return queryWrapper;
    }
}
