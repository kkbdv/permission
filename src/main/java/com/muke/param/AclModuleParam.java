package com.muke.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class AclModuleParam {

    private Integer id;
    @NotBlank(message ="权限模块名称不能为空")
    @Length(min=2,max=20,message = "权限长度在2-20个字符长度之间")
    private String name;

    private Integer parentId =0; //顶级模块默认为0

    private String level;
    @NotNull(message = "权限模块展示顺序不可以为空")
    private Integer seq;
    @NotNull(message = "权限状态不可以为空")
    @Min(value = 0,message = "权限状态不合法")
    @Max(value = 1,message = "权限状态不合法")
    private Integer status;
    @Length(max = 200,message = "备注要在200字以内")
    private String remark;
}
