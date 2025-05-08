package com.example.resourcemanager.controller.book;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.dto.book.SeriesListDTO;
import com.example.resourcemanager.dto.book.SeriesListQueryCondition;
import com.example.resourcemanager.dto.book.group.SpecificCheck;
import com.example.resourcemanager.entity.book.Series;
import com.example.resourcemanager.service.book.SeriesService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Update;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/series")
public class SeriesController {
    @Resource
    SeriesService seriesService;

    @GetMapping("/getList")
    public ResultResponse getList(SeriesListQueryCondition seriesListQueryCondition) {
        return ResultResponse.success(seriesService.getList(seriesListQueryCondition));
    }

    @GetMapping("/updateLove")
    public ResultResponse updateLove(@Validated({SpecificCheck.class}) SeriesListQueryCondition seriesListQueryCondition) {
        seriesService.updateLove(seriesListQueryCondition.getId(), seriesListQueryCondition.getLove());
        return ResultResponse.success();
    }

    @PostMapping("/updateData")
    public ResultResponse updateData(@Validated({Update.class}) @RequestBody Series series) {
        seriesService.updateData(series);
        return ResultResponse.success();
    }

    @GetMapping("/getDetails")
    public ResultResponse getDetails(@RequestParam(required = true) Integer id) {
        SeriesListDTO seriesListDTO = seriesService.getDetails(id);
        return ResultResponse.success(seriesListDTO);
    }

    @GetMapping("/updateCover")
    public ResultResponse updateCover(@RequestParam Integer id, Integer coverId) {
        seriesService.updateCover(id, coverId);
        return ResultResponse.success();
    }

    @GetMapping("/updateLastReadTime")
    public ResultResponse updateLastReadTime(@RequestParam Integer id) {
        return ResultResponse.success(seriesService.updateLastReadTime(id));
    }

    @GetMapping("/randomData")
    public ResultResponse randomData() {
        return ResultResponse.success(seriesService.randomData());
    }
}
