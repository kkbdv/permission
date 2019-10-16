package com.muke.dao;

import com.muke.beans.PageQuery;
import com.muke.model.SysAcl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAcl record);

    int insertSelective(SysAcl record);

    SysAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAcl record);

    int updateByPrimaryKey(SysAcl record);

    int countByAclModuleId(@Param("aclModuleId")int aclModuleId);

    List<SysAcl> getPageByAclModuleId(@Param("aclModuleId")int aclModuleId, @Param("page")PageQuery pageQuery);

    int countByNameAncAclModuleId(@Param("aclModuleId")Integer aclModuleId,@Param("name") String name,@Param("id")Integer id);

    List<SysAcl> getAll();

    List<SysAcl> getByIdList(@Param("idList")List<Integer> userAclIdList);

    List<SysAcl> getByUrl(@Param("url")String url);
}