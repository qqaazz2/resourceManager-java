package com.example.resourcemanager.service.impl.adult;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.adult.*;
import com.example.resourcemanager.entity.adult.*;
import com.example.resourcemanager.mapper.adult.AdultAuthorBindMapper;
import com.example.resourcemanager.mapper.adult.AdultBindMapper;
import com.example.resourcemanager.mapper.adult.AdultMapper;
import com.example.resourcemanager.service.adult.AdultAuthorBindService;
import com.example.resourcemanager.service.adult.AdultAuthorService;
import com.example.resourcemanager.service.adult.AdultBindService;
import com.example.resourcemanager.service.adult.AdultService;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdultServiceImpl extends ServiceImpl<AdultMapper, Adult> implements AdultService {

    @Value("${file.upload}")
    String filePath;

    @Resource
    AdultMapper adultMapper;

    @Resource
    AdultAuthorBindMapper adultAuthorBindMapper;

    @Resource
    AdultBindMapper adultBindMapper;

    @Resource
    AdultAuthorBindService adultAuthorBindService;

    @Resource
    AdultBindService adultBindService;

    @Override
    public PageVO<AdultListDTO> getList(AdultListQueryCondition condition) {
        LambdaQueryWrapper<Adult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(condition.getDownload() != null, Adult::getDownload, condition.getDownload());
        queryWrapper.like((!condition.getName().isBlank()), Adult::getName, condition.getName());
        queryWrapper.like((!condition.getProduce().isBlank()), Adult::getProduce, condition.getProduce());
        queryWrapper.like((!condition.getNumber().isBlank()), Adult::getNumber, condition.getNumber());

        List<Integer> tagBindId = condition.getTagIds().isEmpty() ? new ArrayList<>() : getTagBindId(condition.getTagIds());
        List<Integer> authorBindId = condition.getAuthorIds().isEmpty() ? new ArrayList<>() : getAuthorBindId(condition.getAuthorIds());
        if (!tagBindId.isEmpty() && !authorBindId.isEmpty()) {
            tagBindId.retainAll(authorBindId);
        } else if (tagBindId.isEmpty()) {
            tagBindId.addAll(authorBindId);
        }

        queryWrapper.in((!tagBindId.isEmpty()), Adult::getId, tagBindId);
        List<Adult> list = this.page(new Page(condition.getPage(), condition.getLimit()), queryWrapper).getRecords();

        long count = this.count(queryWrapper);

        List<AdultListDTO> listDTOS = new ArrayList<>();
        File file = new File(filePath + File.separator + "adult");
        if (!file.exists()) file.mkdirs();

        for (Adult adult : list) {
            AdultListDTO dto = new AdultListDTO();
            BeanUtils.copyProperties(adult, dto);
            File img = new File(file.getPath() + File.separator + adult.getNumber() + File.separator + "cover.jpg");
            if (img.exists()) dto.setImg(img.getPath());
            listDTOS.add(dto);
        }

        return new PageVO<AdultListDTO>(condition.getLimit(), condition.getPage(), (int) count, listDTOS);
    }

    @Override
    public String[] getImages(String number) {
        File file = new File(filePath + File.separator + "adult" + File.separator + number + File.separator + "bg");
        System.out.println(file.getPath());
        if (!file.exists()) return new String[]{};

        File[] files = file.listFiles();
        Arrays.sort(files, Comparator.comparingLong(File::lastModified)); // 使用文件名排序


        String[] filePaths = Arrays.stream(files)
                .map(File::getPath)
                .toArray(String[]::new);

        return filePaths;
    }

    @Override
    public AdultDetailDTO randData() {
        LambdaQueryWrapper<Adult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Adult::getDownload, 1).select(Adult::getId);

        List<Adult> adultList = this.list(queryWrapper);
        List<Integer> ids = adultList.stream().map(value -> value.getId()).toList();

        Random random = new Random();
        int index = random.nextInt(ids.size());
        int id = ids.get(index);

        return this.getOne(id);
    }

    public AdultDetailDTO getOne(Integer id) {
        LambdaQueryWrapper<Adult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Adult::getId, id);
        Adult adult = this.getOne(queryWrapper);
        MPJLambdaWrapper<AdultAuthorBind> wrapper = new MPJLambdaWrapper<AdultAuthorBind>().eq(AdultAuthorBind::getAdultId, id)
                .select(AdultAuthor::getId, AdultAuthor::getName, AdultAuthor::getAvatar)
                .leftJoin(AdultAuthor.class, AdultAuthor::getId, AdultAuthorBind::getAdultAuthorId);
        List<AdultAuthorDTO> authorDTOList = adultAuthorBindMapper.selectJoinList(AdultAuthorDTO.class, wrapper);

        MPJLambdaWrapper<AdultBind> adultBindMPJLambdaWrapper = new MPJLambdaWrapper<AdultBind>().eq(AdultBind::getAdultId, id)
                .select(AdultTags::getId, AdultTags::getName, AdultTags::getSynopsis)
                .leftJoin(AdultTags.class, AdultTags::getId, AdultBind::getAdultTagsId);
        List<AdultTagDTO> tagDTOList = adultBindMapper.selectJoinList(AdultTagDTO.class, adultBindMPJLambdaWrapper);


        AdultDetailDTO detailDTO = new AdultDetailDTO();
        BeanUtils.copyProperties(adult, detailDTO); // 使用 BeanUtils 复制属性

        detailDTO.setAuthorList(authorDTOList);
        detailDTO.setTagList(tagDTOList);

        File file = new File(filePath + File.separator + "adult");
        detailDTO.setCover(file.getPath() + File.separator + adult.getNumber() + File.separator + "cover.jpg");
        detailDTO.setImages(getImages(detailDTO.getNumber()));
        return detailDTO;
    }

    @Override
    public Integer addData(AdultDetailDTO adultDetailDTO) {
        Adult adult = new Adult();
        BeanUtils.copyProperties(adultDetailDTO, adult);
        boolean isSuccess = this.save(adult);
        if (!isSuccess) throw new BizException("4000", "创建影片信息失败");


        if (!adultDetailDTO.getAuthorList().isEmpty())
            adultAuthorBindService.addData(adultDetailDTO.getAuthorList(), adult.getId()); //演员
        if (!adultDetailDTO.getTagList().isEmpty())
            adultBindService.saveData(adultDetailDTO.getTagList(), adult.getId(), false);
        return adult.getId();
    }

    @Override
    public Integer editData(AdultDetailDTO adultDetailDTO) {
        Adult adult = new Adult();
        BeanUtils.copyProperties(adultDetailDTO, adult);
        boolean isSuccess = this.updateById(adult);
        if (!isSuccess) throw new BizException("4000", "修改影片信息失败");


        adultAuthorBindService.editData(adultDetailDTO.getAuthorList(), adult.getId()); //演员
        adultBindService.saveData(adultDetailDTO.getTagList(), adult.getId(), true);
        return adult.getId();
    }

    @Override
    public void delImg(String path) {
        File file = new File(path);
        if (!file.exists()) throw new BizException("4000", "图片不存在");

        file.delete();
    }

    @Override
    public void bindEmbyId(Integer id, Integer embyId) {
        LambdaUpdateWrapper<Adult> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Adult::getId, id).set(Adult::getEmbyId, embyId);
        boolean isSuccess = this.update(lambdaUpdateWrapper);

        if (!isSuccess) throw new BizException("4000", "绑定失败");
    }

    private List<Integer> getAuthorBindId(List<Integer> list){
        LambdaQueryWrapper<AdultAuthorBind> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AdultAuthorBind::getAdultAuthorId,list);
        queryWrapper.select(AdultAuthorBind::getAdultId);
        List<AdultAuthorBind> bindList = adultAuthorBindMapper.selectList(queryWrapper);
        return bindList.stream().map(value -> value.getAdultId()).collect(Collectors.toList());
    }

    private List<Integer> getTagBindId(List<Integer> list){
        LambdaQueryWrapper<AdultBind> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AdultBind::getAdultTagsId,list);
        queryWrapper.select(AdultBind::getAdultId);
        List<AdultBind> bindList = adultBindMapper.selectList(queryWrapper);
        return bindList.stream().map(value -> value.getAdultId()).collect(Collectors.toList());
    }
}
