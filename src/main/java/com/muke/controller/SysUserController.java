package com.muke.controller;

import com.muke.beans.PageQuery;
import com.muke.beans.PageResult;
import com.muke.common.JsonData;
import com.muke.model.SysUser;
import com.muke.param.DeptParam;
import com.muke.param.Userparam;
import com.muke.service.SysUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@RequestMapping("sys/user")
@Controller
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @RequestMapping("/noAuth.page")
    public ModelAndView noAuth(){
         return new ModelAndView("noAuth");
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveUser(Userparam userparam){
        sysUserService.save(userparam);
        return JsonData.success();
    };

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateUser(Userparam userparam){
        sysUserService.update(userparam);
        return JsonData.success();
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public JsonData page(@RequestParam("deptId")Integer deptId, PageQuery pageQuery){
        PageResult<SysUser > result = sysUserService.getPageByDeptId(deptId,pageQuery);
        return JsonData.success(result);
    }
}
