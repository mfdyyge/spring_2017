package com.mark.java.service.imp;

import com.mark.java.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小觜冰凉 on 2017-2-6 0006.
 */
@Service
public class UserServiceImp implements UserService {

    public List<String> getAllUsernames() {
        List<String> users = new ArrayList<String>();
        users.add("MarK");
        users.add("Ken");
        users.add("Fowafolo");
        users.add("周路");
        users.add("重庆市.酉阳县.龚滩镇");
        users.add("新华社区-6组");
        return users;
    }
}
