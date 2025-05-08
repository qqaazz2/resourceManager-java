package com.example.resourcemanager.service.book;

import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.book.SeriesListDTO;
import com.example.resourcemanager.dto.book.SeriesListQueryCondition;
import com.example.resourcemanager.entity.book.Series;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface SeriesService {
    List<Series> createData(List<Series> list);

    void updateNum(Integer filesId,Integer num);

    PageVO<SeriesListDTO> getList(SeriesListQueryCondition seriesListQueryCondition);

    void updateLove(Integer id,Integer love);

    void updateStatus(Integer id,Integer status);

    Series updateData(Series series);

    SeriesListDTO getDetails(Integer id);

    void updateCover(Integer id,Integer coverId);

    Date updateLastReadTime(Integer id);

    SeriesListDTO randomData();
}
