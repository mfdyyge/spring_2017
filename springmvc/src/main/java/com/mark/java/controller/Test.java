package com.mark.java.controller;

import com.mark.java.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 小觜冰凉 on 2017-2-6 0006.
 * 测试类
 */
@Controller
@RequestMapping("/")
public class Test {
    @Autowired
    private UserService userService;

    @RequestMapping("")
    public String index(){
        return "index";
    }

    @RequestMapping("/getMapJson")
    @ResponseBody
    public Map<String, String> json(){
        Map<String, String> result = new HashMap<String, String>();
        result.put("MarK", "hello");
        result.put("Ken", "Hehe");
        result.put("Fowafolo", "fool");
        return result;
    }


    @RequestMapping("/getListJson")
    @ResponseBody
    public List<String> list_to_json(){
        return userService.getAllUsernames();
    }

    public List<Map> list_map_to_json()
    {
        List<Map>  notify_target_map_list  = new ArrayList<Map>();
 /*       Map map_list=new HashMap();
        //FW_NOTIFY_TARGET 通知目标对象-List
        for (int i = 0;i<=10;i++)
        {
            Map notify_target_map=new HashMap();
            notify_target_map.put("ID",BaseTools.getNextSeq());
            notify_target_map.put("NOTIFY_ID",notify_id);   /设置外键共用*[ID]----------通知目标对象的表字段-FW_NOTIFY_TARGET.NOTIFY_ID
            System.out.println("两表相关ID-notify_id = " + notify_id);
            notify_target_map.put("SWJGDM",swjg_i);          //通知目标对象的表字段-FW_NOTIFY_TARGET.SWJGDM
            notify_target_map_list.add(notify_target_map);
        }*/
        return notify_target_map_list;
    }
}