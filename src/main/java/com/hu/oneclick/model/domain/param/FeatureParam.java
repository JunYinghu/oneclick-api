package com.hu.oneclick.model.domain.param;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hu.oneclick.model.domain.Feature;

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
@Schema(name = "故事Param")
public class FeatureParam implements Serializable {
    @Schema(name = "名称")
    private String title;

    @Schema(name = "项目ID")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    public LambdaQueryWrapper getQueryCondition() {
        LambdaQueryWrapper<Feature> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(this.title), Feature::getTitle, this.title);
        queryWrapper.eq(null != this.projectId, Feature::getProjectId, this.projectId);
        queryWrapper.orderByDesc(Feature::getCreateTime);
        return queryWrapper;
    }
}
