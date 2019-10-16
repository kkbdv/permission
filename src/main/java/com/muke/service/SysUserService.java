package com.muke.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.muke.beans.Mail;
import com.muke.beans.PageQuery;
import com.muke.beans.PageResult;
import com.muke.common.RequestHolder;
import com.muke.dao.SysUserMapper;
import com.muke.exception.ParamException;
import com.muke.model.SysUser;
import com.muke.param.Userparam;
import com.muke.util.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;

    public void save(Userparam userparam){
        BeanValidator.check(userparam);
        if(checkTelephoneExist(userparam.getTelephone(),userparam.getId())){
            throw new ParamException("电话已被占用");
        }
        if(checkEmailExist(userparam.getMail(),userparam.getId())){
            throw new ParamException("邮箱已被占用");
        }
        String password = PasswordUtil.randomPassword();//给定的password
        String encryptedPassword = MD5Util.encrypt(password);
        SysUser user  =SysUser.builder().username(userparam.getUsername()).telephone(userparam.getTelephone()).mail(userparam.getMail())
                .password(encryptedPassword).deptId(userparam.getDeptId()).status(userparam.getStatus()).remark(userparam.getRemark()).build();
        user.setOperator(RequestHolder.getCurrentUser().getUsername());//todo
        user.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));//todo
        user.setOperatorTime(new Date());//todo
        //TODO:send Email
        Mail mail = new Mail();
        mail.setMessage("密码为:"+password);
        mail.setReceivers(Sets.newHashSet(user.getMail()));
        mail.setSubject("管理员密码");
        MailUtil.send(mail);
        sysUserMapper.insertSelective(user);

    }

    public void update(Userparam userparam){
        BeanValidator.check(userparam);
        if(checkTelephoneExist(userparam.getTelephone(),userparam.getId())){
            throw new ParamException("电话已被占用");
        }
        if(checkEmailExist(userparam.getMail(),userparam.getId())){
            throw new ParamException("邮箱已被占用");
        }
        SysUser before = sysUserMapper.selectByPrimaryKey(userparam.getId());
        Preconditions.checkNotNull(before,"待更新的用户不存在");
        SysUser after = SysUser.builder().id(userparam.getId()).username(userparam.getUsername()).telephone(userparam.getTelephone()).mail(userparam.getMail())
                .password(before.getPassword()).deptId(userparam.getDeptId()).status(userparam.getStatus()).remark(userparam.getRemark()).build();//修改之后的用户
        after.setOperator(RequestHolder.getCurrentUser().getUsername());//todo
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));//todo
        after.setOperatorTime(new Date());//todo

        sysUserMapper.updateByPrimaryKeySelective(after);
    }

    public boolean checkEmailExist(String mail,Integer userId){
        return sysUserMapper.countByMail(mail,userId)>0;
    }

    public boolean checkTelephoneExist(String telephone,Integer userId){
        return sysUserMapper.countByTelephone(telephone,userId)>0;
    }

    public SysUser findByKeyWord(String key){
        return sysUserMapper.findByKeyWord(key);
    }

    public PageResult<SysUser> getPageByDeptId(int deptId, PageQuery page){
        BeanValidator.check(page);
        int count = sysUserMapper.countByDeptId(deptId);
        if(count>0){
            List<SysUser> list = sysUserMapper.getPageByDeptId(deptId,page);
            return PageResult.<SysUser>builder().total(count).data(list).build();//对象强转问题要注意一下
        }
        return PageResult.<SysUser>builder().build();
    }

    public List<SysUser> getAll(){
        return sysUserMapper.getAll();
    }
}
