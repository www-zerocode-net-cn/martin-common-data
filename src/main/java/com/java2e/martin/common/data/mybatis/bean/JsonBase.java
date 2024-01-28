package com.java2e.martin.common.data.mybatis.bean;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.java2e.martin.common.data.AtLeastOneNotNull;
import com.java2e.martin.common.data.mybatis.config.ErdJsonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author: 零代科技
 * @version: 1.0
 * @date: 2023/3/4 16:38
 * @describtion: JsonBase
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "project", autoResultMap = true)
@AtLeastOneNotNull(fieldNames = {"path", "name"}, message = "path，name 至少有一个不为为空")
public class JsonBase {

    @ApiModelProperty(value = "主键")
    protected String id;

    @ApiModelProperty(value = "json path")
    protected String path;

    @ApiModelProperty(value = "json name")
    protected String name;

    @ApiModelProperty(value = "json value")
    @TableField(insertStrategy = FieldStrategy.NEVER,
            updateStrategy = FieldStrategy.NEVER,
            select = false,
            typeHandler = ErdJsonTypeHandler.class)
    protected JSONObject json;

//    @ApiModelProperty(value = "value")
//    protected Object value;

}
