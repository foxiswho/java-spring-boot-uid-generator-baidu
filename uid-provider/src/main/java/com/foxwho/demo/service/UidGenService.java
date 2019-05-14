package com.foxwho.demo.service;

import com.baidu.fsg.uid.UidGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class UidGenService {
    @Resource(name = "cachedUidGenerator")
    private UidGenerator uidGenerator;

    public long getUid() {
        return uidGenerator.getUID();
    }
}
