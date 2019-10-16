package com.muke.service;

import com.google.common.base.Preconditions;
import com.muke.common.RequestHolder;
import com.muke.dao.SysAclMapper;
import com.muke.dao.SysAclModuleMapper;
import com.muke.exception.ParamException;
import com.muke.model.SysAcl;
import com.muke.model.SysAclModule;
import com.muke.model.SysDept;
import com.muke.param.AclModuleParam;
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
public class SysAclModuleService {
    @Resource
    private SysAclModuleMapper sysAclModuleMapper;
    @Resource
    private SysAclMapper sysAclMapper;

    public void save(AclModuleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级存在相同名称");
        }
        SysAclModule aclModule = SysAclModule.builder().name(param.getName()).parentId(param.getParentId()).level(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()))
                .seq(param.getSeq()).status(param.getStatus()).remark(param.getRemark()).build();
        aclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
        aclModule.setOperateTime(new Date());
        aclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));

        sysAclModuleMapper.insertSelective(aclModule);
    }

    public void update(AclModuleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("当前部门存在相同名称");
        }
        SysAclModule before = sysAclModuleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的权限模块不存在");
        SysAclModule after = SysAclModule.builder().name(param.getName()).parentId(param.getParentId()).level(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()))
                .seq(param.getSeq()).status(param.getStatus()).id(param.getId()).remark(param.getRemark()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        updateWithChild(before,after);
    }

    @Transactional
    protected void updateWithChild(SysAclModule before, SysAclModule after){//层级更新
        String newLevelPrefix = after.getLevel();
        String oldLevelPrefix = before.getLevel();
        if(!after.getLevel().equals(before.getLevel())){
            List<SysAclModule> aclModules = sysAclModuleMapper.getChildAclModuleListByLevel(oldLevelPrefix);
            if(CollectionUtils.isNotEmpty(aclModules)){
                for(SysAclModule aclModule:aclModules){
                    String level = aclModule.getLevel();
                    if(level.indexOf(oldLevelPrefix)==0){ //判断每个子节点的level和修改前是否相等
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        aclModule.setLevel(level);
                    }
                }
                sysAclModuleMapper.batchUpdateLevel(aclModules);
            }
        }
        sysAclModuleMapper.updateByPrimaryKey(after);
    }

    private boolean checkExist(Integer parentId,String aclModuleName,Integer aclModuleId){//校验同一模块中不能出现相同的名称
        return sysAclModuleMapper.countByNameParentId(parentId,aclModuleName,aclModuleId)>0;
    }

    private String getLevel(Integer aclModuleId){
         SysAclModule aclModule = sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
         if(aclModule == null){
             return null;
         }
         return aclModule.getLevel();
    }

    public void delete(int aclModuleId){
        SysAclModule aclModule = sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        Preconditions.checkNotNull(aclModule,"待删除的权限模块不存在，无法删除");
        if(sysAclModuleMapper.countByParentId(aclModuleId)>0){
            throw new ParamException("当前模块下面有子模块，无法删除");
        }
        if(sysAclMapper.countByAclModuleId(aclModuleId)>0){
            throw new ParamException(("当前模块有权限点，无法删除"));
        }
        sysAclModuleMapper.deleteByPrimaryKey(aclModuleId);
    }
}
