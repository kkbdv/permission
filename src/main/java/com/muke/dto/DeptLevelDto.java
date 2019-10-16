package com.muke.dto;

import com.google.common.collect.Lists;
import com.muke.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Setter
@ToString
public class DeptLevelDto extends SysDept {

    private List<DeptLevelDto> deptList = Lists.newArrayList();

    public static DeptLevelDto adapt(SysDept dept){//把传入的dept数据拷贝到DeptDto
        DeptLevelDto dto = new DeptLevelDto();
        BeanUtils.copyProperties(dept,dto);//spring提供的巧妙方法
        return dto;
    }
}
