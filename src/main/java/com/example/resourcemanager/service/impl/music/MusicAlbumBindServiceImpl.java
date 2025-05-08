package com.example.resourcemanager.service.impl.music;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.entity.music.MusicAlbumBind;
import com.example.resourcemanager.entity.music.MusicAuthorBind;
import com.example.resourcemanager.mapper.music.MusicAlbumBindMapper;
import com.example.resourcemanager.mapper.music.MusicAuthorBindMapper;
import com.example.resourcemanager.service.music.MusicAlbumBindService;
import com.example.resourcemanager.service.music.MusicAuthorBindService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicAlbumBindServiceImpl extends ServiceImpl<MusicAlbumBindMapper, MusicAlbumBind> implements MusicAlbumBindService {
    @Override
    public List<MusicAlbumBind> createData(List<MusicAlbumBind> list) {
        boolean isSuccess = this.saveBatch(list);
        if (!isSuccess) throw new BizException("绑定音乐艺术家失败");
        return list;
    }
}
