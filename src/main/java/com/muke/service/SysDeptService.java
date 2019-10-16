package com.muke.service;

import com.google.common.base.Preconditions;
import com.muke.common.RequestHolder;
import com.muke.dao.SysDeptMapper;
import com.muke.dao.SysUserMapper;
import com.muke.exception.ParamException;
import com.muke.model.SysDept;
import com.muke.param.DeptParam;
import com.muke.util.BeanValidator;
import com.muke.util.IpUtil;
import com.muke.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysDeptService {
    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    public void save(DeptParam param){
        BeanValidator.check(param);
        if(checkExit(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("当前部门存在相同名称");
        }
        SysDept dept = SysDept.builder().name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        dept.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));//拼接当前父id和父id的层级
        dept.setOperator(RequestHolder.getCurrentUser().getUsername());//*通过ThreadLocal获取用户信息,简化这个信息的获取，不然要引入request才能进行
        dept.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));//todo
        dept.setOperateTime(new Date());//todo
        sysDeptMapper.insertSelective(dept);//这个插入方法是选择性地插入，无值不插入
    }
    public void update(DeptParam param){
        BeanValidator.check(param);
        if(checkExit(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("当前部门存在相同名称");
        }
        SysDept before = sysDeptMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的部门不存在");
        if(checkExit(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("当前部门存在相同名称");
        }
        SysDept after = SysDept.builder().id(param.getId()).name(param.getName()).parentId(param.getId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());//todo
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));//todo
        after.setOperateTime(new Date());//todo
        updateWithChild(before,after);
    }
    @Transactional
    public void updateWithChild(SysDept before,SysDept after){
        String newLevelPrefix = after.getLevel();
        String oldLevelPrefix = before.getLevel();
        if(!after.getLevel().equals(before.getLevel())){
            List<SysDept> deptList = sysDeptMapper.getChildDeptListByLevel(before.getLevel());
            if(CollectionUtils.isNotEmpty(deptList)){
                for(SysDept dept:deptList){
                    String level = dept.getLevel();
                    if(level.indexOf(oldLevelPrefix)==0){
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        dept.setLevel(level);
                    }
                }
                sysDeptMapper.batchUpdateLevel(deptList);
            }
        }
        sysDeptMapper.updateByPrimaryKey(after);
    }

    /**
     *  检查同一父id下是否有同名的子部门
     *  适用与 插入，更新
     * @param parentId
     * @param deptName
     * @param deptId
     * @return
     */
    private boolean checkExit(Integer parentId,String deptName,Integer deptId){
        //todo
        return sysDeptMapper.countByNameParentId(parentId,deptName,deptId)>0;
    }

    private String getLevel(Integer deptId){//通过id获取父级的Level
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
        if(dept==null){
            return null;
        }
        return dept.getLevel();
    }

    public void delete(int deptId){
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
        Preconditions.checkNotNull(dept,"待删除的部门不存在,无法删除");
        if(sysDeptMapper.countByParentId(deptId)>0){
            throw new ParamException("当前部门下面有子部门，无法删除");
        }
        if(sysUserMapper.countByDeptId(deptId)>0){
            throw new ParamException("当前部门下有用户，无法删除");
        }
        sysDeptMapper.deleteByPrimaryKey(deptId);
    }
}
