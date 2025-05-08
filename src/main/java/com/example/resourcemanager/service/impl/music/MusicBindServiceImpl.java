package com.example.resourcemanager.service.impl.music;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.entity.music.MusicAuthorBind;
import com.example.resourcemanager.entity.music.MusicBind;
import com.example.resourcemanager.mapper.music.MusicAuthorBindMapper;
import com.example.resourcemanager.mapper.music.MusicBindMapper;
import com.example.resourcemanager.service.music.MusicAuthorBindService;
import com.example.resourcemanager.service.music.MusicBindService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicBindServiceImpl extends ServiceImpl<MusicBindMapper, MusicBind> implements MusicBindService {
    @Override
    public List<MusicBind> createData(List<MusicBind> list) {
        boolean isSuccess = this.saveBatch(list);
        if (!isSuccess) throw new BizException("绑定专辑失败");
        return list;
    }
}
