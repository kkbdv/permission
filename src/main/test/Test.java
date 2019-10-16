import com.google.common.collect.*;
import com.muke.beans.Mail;
import com.muke.dto.DeptLevelDto;
import com.muke.model.SysDept;
import com.muke.util.JsonMapper;
import com.muke.util.MailUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Array;
import java.util.*;

public class Test {

    ArrayList<DeptLevelDto> deptDtoList = Lists.newArrayList();

    //递归读取方法
    public void readRoot(List<DeptLevelDto> dtoList,int level){
        int currLevel = level;
        if(CollectionUtils.isEmpty(dtoList)){
            return;
        }

        for(DeptLevelDto dto:dtoList){//遍历进来的list 获取输出
            for(int i =0;i<currLevel;i++){
                System.out.print("  ");
            }
            System.out.println("id="+dto.getId()+"  name="+dto.getName());

            readRoot(dto.getDeptList(),++level);
        }

    }
    //将parentId==id 的值整合到一个新的list 输入:list 输入：整合后的List
    public ArrayList makeTree(ArrayList<DeptLevelDto> dtoList){
        if (CollectionUtils.isEmpty(dtoList)){
            return null;
        }
        Multimap mulMap = ArrayListMultimap.create();
        ArrayList<DeptLevelDto> retList = Lists.newArrayList();
        for(DeptLevelDto dto:dtoList){
            if(dto.getParentId()==0) {
                retList.add(dto);
            }
            mulMap.put(dto.getParentId(),dto);//父id相同的放到一个数组里去
        }
        //遍历结束,得到了头和头有关的数组
        for(int i=0;i<retList.size();i++){
            DeptLevelDto lDto = retList.get(i);
            lDto.setDeptList(((ArrayListMultimap) mulMap).get(lDto.getId()));
        }

        return retList;
    }
    //无限树生成 输入: 原始数据，根节点 ,一层一层地进行，返回时把数组插入上一层的List
    public ArrayList infiTree(ArrayList<DeptLevelDto> dtoList ,int parentId){
        if(CollectionUtils.isEmpty(dtoList)){
            return null;
        }
        ArrayList<DeptLevelDto> headList = Lists.newArrayList();//头list ,有下层
        ArrayList<DeptLevelDto> otherList = Lists.newArrayList();//其他list ,无下层
        for(DeptLevelDto dto:dtoList){
            if(dto.getParentId()==parentId){
                headList.add(dto);
            }else {
                otherList.add(dto);
            }
        }
        if(CollectionUtils.isNotEmpty(otherList)){
            for(DeptLevelDto head:headList){ //剩余的list 指定parentId后返回
                head.setDeptList(infiTree(otherList,head.getId()));
            }
        }
        return headList;
    }

    @org.junit.Test
    public void testMulitMap(){
        ArrayList<SysDept> sysDeptList = Lists.newArrayList();
        sysDeptList.add(SysDept.builder().id(1).name("北京市").parentId(0).build());
        sysDeptList.add(SysDept.builder().id(2).name("广东省").parentId(0).build());
        sysDeptList.add(SysDept.builder().id(3).name("朝阳区").parentId(1).build());
        sysDeptList.add(SysDept.builder().id(4).name("东城区").parentId(1).build());
        sysDeptList.add(SysDept.builder().id(5).name("我的宝贝儿").parentId(3).build());
        sysDeptList.add(SysDept.builder().id(6).name("干就好了").parentId(5).build());
        sysDeptList.add(SysDept.builder().id(7).name("有内味了").parentId(6).build());


        for(SysDept dept:sysDeptList){
            DeptLevelDto dto = new DeptLevelDto();
            BeanUtils.copyProperties(dept,dto);
            deptDtoList.add(dto);
        }//转换成可用数据
        //将parentId==id 的值整合到一个新的list
        ArrayList<DeptLevelDto> rootList = infiTree(deptDtoList,0);
        readRoot(rootList,0);//输入拍好序的数组，输入初始层级

    }

    @org.junit.Test
    public void mailTest(){
        double wight,wight2,height=1111;
        wight = 0.01+0.09;
        wight2 = (height-80)*0.6;
        System.out.println(wight);
        System.out.println(wight2);
    }
}
