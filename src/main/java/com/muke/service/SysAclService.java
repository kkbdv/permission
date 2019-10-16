package com.muke.service;

import com.google.common.base.Preconditions;
import com.muke.beans.PageQuery;
import com.muke.beans.PageResult;
import com.muke.common.RequestHolder;
import com.muke.dao.SysAclMapper;
import com.muke.exception.ParamException;
import com.muke.model.SysAcl;
import com.muke.param.AclParam;
import com.muke.util.BeanValidator;
import com.muke.util.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SysAclService {

    @Resource
    private SysAclMapper sysAclMapper;

    public void save(AclParam param){
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("存在相同的acl");
        }
        SysAcl acl = SysAcl.builder().name(param.getName()).aclModuleId(param.getAclModuleId()).url(param.getUrl())
                .seq(param.getSeq()).remark(param.getRemark()).status(param.getStatus()).type(param.getType()).build();
        acl.setCode(generateCode());//每个权限点这个值都是唯一的
        acl.setOperator(RequestHolder.getCurrentUser().getUsername());
        acl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        acl.setOperateTime(new Date());
        sysAclMapper.insertSelective(acl);
    }

    public void update(AclParam param){
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("存在相同的acl");
        }
        SysAcl before = sysAclMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的点不存在");

        SysAcl after = SysAcl.builder().id(param.getId()).name(param.getName()).aclModuleId(param.getAclModuleId()).url(param.getUrl())
                .seq(param.getSeq()).remark(param.getRemark()).status(param.getStatus()).type(param.getType()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysAclMapper.updateByPrimaryKeySelective(after);
    }

    public boolean checkExist(Integer aclModuleId,String name,Integer id){
        return sysAclMapper.countByNameAncAclModuleId(aclModuleId,name,id)>0;
    }

    public String generateCode(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date())+"_"+(int)(Math.random()*100);
    }

    public PageResult<SysAcl> getPageByAclModuleId(int aclModuleId, PageQuery pageQuery){
        BeanValidator.check(pageQuery);
        int count = sysAclMapper.countByAclModuleId(aclModuleId);
        if(count>0){
            List<SysAcl> aclList = sysAclMapper.getPageByAclModuleId(aclModuleId,pageQuery);
            return PageResult.<SysAcl>builder().data(aclList).total(count).build();
        }
        return PageResult.<SysAcl>builder().build();//返回一个默认构造的PageResult
    }
}
