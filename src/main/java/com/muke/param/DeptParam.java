package com.muke.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DeptParam {

    private  Integer id;
    @NotBlank(message = "部门名称不能为空")
    @Length(max = 15,min = 2,message = "部门名称要在2-15之间")
    private  String name;
    private  Integer parentId = 0;
    @NotNull(message = "展示顺序不可以为空")
    private  Integer seq;
    @Length(max = 150,message = "备注的长度需要在150之内")
    private  String remark;
}
