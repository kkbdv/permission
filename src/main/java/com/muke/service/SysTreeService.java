package com.muke.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.muke.dao.SysAclMapper;
import com.muke.dao.SysAclModuleMapper;
import com.muke.dao.SysDeptMapper;
import com.muke.dto.AclDto;
import com.muke.dto.AclModuleLevelDto;
import com.muke.dto.DeptLevelDto;
import com.muke.model.SysAcl;
import com.muke.model.SysAclModule;
import com.muke.model.SysDept;
import com.muke.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 计算层级树
 */
@Service
public class SysTreeService {
    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysAclModuleMapper sysAclModuleMapper;
    @Resource
    private SysCoreService sysCoreService;
    @Resource
    private SysAclMapper sysAclMapper;

    public List<AclModuleLevelDto> roleTree(int roleId){ //拿到角色对应的权限树
        //1.当前用户已分配的权限点
        List<SysAcl> userAclList = sysCoreService.getCurrentUserAclList();
        //2.当前角色分配的权限点
        List<SysAcl> roleAclList = sysCoreService.getRoleAclList(roleId);
        //3.当前系统所有的权限点
        List<SysAcl> allAclList = sysAclMapper.getAll();//获取权限点的全集

        Set<Integer> userAclIdSet =  userAclList.stream().map(sysAcl->sysAcl.getId()).collect(Collectors.toSet());//遍历userAclList中的值，取出每一个的id做成Set
        Set<Integer>  roleAclIdSet = roleAclList.stream().map(sysAcl->sysAcl.getId()).collect(Collectors.toSet());

        Set<SysAcl> aclSet = new HashSet<>(allAclList);
        List<AclDto> aclDtoList = Lists.newArrayList();
        for(SysAcl acl: aclSet){//遍历所有的权限点
            AclDto dto = AclDto.adapt(acl);
            if(userAclIdSet.contains(acl.getId())){//如果用户权限id包含,角色权限id
                dto.setHasAcl(true);
            }
            if(roleAclIdSet.contains(acl.getId())){//如果角色权限，包含角色权限id
                dto.setChecked(true);
            }
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    public List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList){
        if(CollectionUtils.isEmpty(aclDtoList)){
            return Lists.newArrayList();
        }
        List<AclModuleLevelDto> aclModuleLevelDtoList = aclModuleTree();//获取之前的模块树

        Multimap<Integer,AclDto>  moduleIdAclMap = ArrayListMultimap.create(); //Integer-权限模块的id
        for(AclDto acl:aclDtoList){
            if(acl.getStatus() ==1){//判断权限是否有效
                moduleIdAclMap.put(acl.getAclModuleId(),acl);
            }
        }
        bindAclsWithOrder(aclModuleLevelDtoList,moduleIdAclMap);
        return aclModuleLevelDtoList;
    }

    public void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelDtoList,Multimap<Integer,AclDto> moduleIdAclMap){//把权限点绑到权限模块树
            if(CollectionUtils.isEmpty(aclModuleLevelDtoList)){
                return;
            }
            for(AclModuleLevelDto dto:aclModuleLevelDtoList){
                List<AclDto> aclDtoList = (List<AclDto>)moduleIdAclMap.get(dto.getId());//id相同的list挂载到一起
                if(CollectionUtils.isNotEmpty(aclDtoList)){
                    Collections.sort(aclDtoList,aclDtoSeqComparator);
                    dto.setAclList(aclDtoList);
                }
                bindAclsWithOrder(dto.getAclModuleList(),moduleIdAclMap);
            }
    }

    public List<AclModuleLevelDto> aclModuleTree(){
         List<SysAclModule> aclModuleList = sysAclModuleMapper.getAllAclModule();
         List<AclModuleLevelDto> dtoList = Lists.newArrayList();
         for(SysAclModule aclModule:aclModuleList){
             dtoList.add(AclModuleLevelDto.adapt(aclModule));
         }
         return aclModuleListToTree(dtoList);
    }

    public List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> dtoList){
        if(CollectionUtils.isEmpty(dtoList)){
            return Lists.newArrayList();
        }
        Multimap<String,AclModuleLevelDto> levelAclModuleMap = ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList = Lists.newArrayList();

        for(AclModuleLevelDto dto:dtoList){
            levelAclModuleMap.put(dto.getLevel(),dto);//0.2, 按照层级字段level 把相同字段的整合成一个数组
            if(LevelUtil.ROOT.equals(dto.getLevel())){//如果是根部门就存到根rootList中 level == 0
                rootList.add(dto); //把level=0的根节点提取出来成一个数组 [0,0,0,0]
            }
        }
        Collections.sort(rootList,aclModuleSeqComparator);
        transformAclModuleTree(rootList,LevelUtil.ROOT,levelAclModuleMap);
        return rootList;
    }

    public void transformAclModuleTree(List<AclModuleLevelDto> dtoList,String level,Multimap multimap){
        for(int i=0;i<dtoList.size();i++){
            AclModuleLevelDto dto = dtoList.get(i); //遍历当前层
            String nextLevel = LevelUtil.calculateLevel(level,dto.getId());//0.1
            List<AclModuleLevelDto> tempList = (List<AclModuleLevelDto>) multimap.get(nextLevel);
            if(CollectionUtils.isNotEmpty(tempList)){//判断下一层级是否有值
                Collections.sort(tempList,aclModuleSeqComparator);
                dto.setAclModuleList(tempList);
                transformAclModuleTree(tempList,nextLevel,multimap);//递归处理
            }
        }
    }


    public List<DeptLevelDto> deptTree(){ //把dept的List转为dto的List,再调用toTree方法
        List<SysDept> deptList = sysDeptMapper.getAllDept();
        List<DeptLevelDto> dtoList = Lists.newArrayList();
        for(SysDept dept:deptList){
            DeptLevelDto dto = DeptLevelDto.adapt(dept);
            dtoList.add(dto);
        }
        return deptListToTree(dtoList);
    }

    public List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelDtoList){
        if(CollectionUtils.isEmpty(deptLevelDtoList)){
            return Lists.newArrayList();
        }
        //定义一种数据结构,以level为key ,相同level的部门作为value  level->[dept1,dept2,....]
         Multimap<String,DeptLevelDto> levelDeptMap = ArrayListMultimap.create(); //研究一下这个写法
        List<DeptLevelDto> rootList = Lists.newArrayList();

        for(DeptLevelDto dto:deptLevelDtoList){
            levelDeptMap.put(dto.getLevel(),dto);//0.2, 按照层级字段level 把相同字段的整合成一个数组
            if(LevelUtil.ROOT.equals(dto.getLevel())){//如果是根部门就存到根rootList中 level == 0
                rootList.add(dto); //把level=0的根节点提取出来成一个数组 [0,0,0,0]
            }
        }
        //按照seq从小到大排序
        Collections.sort(rootList, new Comparator<DeptLevelDto>() {
            @Override
            public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                return o1.getSeq()-o2.getSeq();
            }
        });
        //递归生成树
        transformDeptTree(rootList,LevelUtil.ROOT,levelDeptMap);
        return rootList;
    }
    public void transformDeptTree(List<DeptLevelDto> deptLevelList,String level,Multimap<String,DeptLevelDto> multimap){
        for(int i = 0;i<deptLevelList.size();i++){
            //遍历该层的每个元素
            DeptLevelDto deptLevelDto = deptLevelList.get(i);
            //处理当前的层级数据
            String nextLevel = LevelUtil.calculateLevel(level,deptLevelDto.getId());// 通过已存在level和自己id 创建新的level
            //处理下一层
            List<DeptLevelDto> tempDeptList = (List<DeptLevelDto>) multimap.get(nextLevel);
            if(CollectionUtils.isNotEmpty(tempDeptList)){
                //排序
                Collections.sort(tempDeptList,deptLevelDtoComparator);
                //设置下一层部门
                deptLevelDto.setDeptList(tempDeptList);
                //进入到下一层处理
                transformDeptTree(tempDeptList,nextLevel,multimap); //此时nextLevel为根节点
            }
        }
    }

    public Comparator<DeptLevelDto> deptLevelDtoComparator = new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };

    public Comparator<AclModuleLevelDto> aclModuleSeqComparator = new Comparator<AclModuleLevelDto>() {
        @Override
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };

    public Comparator<AclDto> aclDtoSeqComparator = new Comparator<AclDto>() {
        @Override
        public int compare(AclDto o1, AclDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };
}
