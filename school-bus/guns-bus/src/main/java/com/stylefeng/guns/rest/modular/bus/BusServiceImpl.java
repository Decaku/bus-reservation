package com.stylefeng.guns.rest.modular.bus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stylefeng.guns.core.constants.SbCode;
import com.stylefeng.guns.rest.bus.IBusService;
import com.stylefeng.guns.rest.bus.dto.PageBusRequest;
import com.stylefeng.guns.rest.bus.dto.PageBusResponse;
import com.stylefeng.guns.rest.common.persistence.dao.BusMapper;
import com.stylefeng.guns.rest.common.persistence.model.Bus;
import com.stylefeng.guns.rest.modular.bus.converter.BusConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Slf4j
@Component
@Service
public class BusServiceImpl implements IBusService {

    @Autowired
    private BusMapper busMapper;

    @Autowired
    private BusConverter busConverter;

    @Override
    public PageBusResponse getBus(PageBusRequest request) {
        PageBusResponse response = new PageBusResponse();
        try {
            IPage<Bus> busIPage = new Page<>(request.getCurrentPage(), request.getPageSize());
            busIPage = busMapper.selectPage(busIPage, null);
            response.setCurrentPage(busIPage.getCurrent());
            response.setPageSize(busIPage.getSize());
            response.setPages(busIPage.getPages());
            response.setTotal(busIPage.getTotal());
            response.setBusDtos(busConverter.bus2List(busIPage.getRecords()));
            response.setCode(SbCode.SUCCESS.getCode());
            response.setMsg(SbCode.SUCCESS.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(SbCode.DB_EXCEPTION.getCode());
            response.setMsg(SbCode.DB_EXCEPTION.getMsg());
            log.error("getBus:" , e);
            return response;
        }
        return response;
    }

    @Override
    public PageCountResponse getCount(PageCountRequest request) {
        PageCountResponse response = new PageCountResponse();
        try {
            IPage<CountSimpleDto> countIPage = new Page<>(request.getCurrentPage(), request.getPageSize());
            QueryWrapper<CountSimpleDto> queryWrapper = new QueryWrapper<>();
            // 获取时间
            String currHours = DateUtil.getHours();
            String day = DateUtil.getDay();
            System.out.println("当前时间："+currHours);
            System.out.println("当前日期："+day);
            // 判断条件
            queryWrapper
                    .eq("begin_date", day)
                    .ge("begin_time", currHours)
                    .eq("bus_status", request.getBusStatus())
                    .orderByAsc("begin_time");// 时间

            countIPage = countMapper.selectCounts(countIPage, queryWrapper);
            response.setCurrentPage(countIPage.getCurrent());
            response.setPageSize(countIPage.getSize());
            response.setPages(countIPage.getPages());
            response.setTotal(countIPage.getTotal());
            response.setCountSimpleDtos(countIPage.getRecords());
            response.setCode(SbCode.SUCCESS.getCode());
            response.setMsg(SbCode.SUCCESS.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(SbCode.DB_EXCEPTION.getCode());
            response.setMsg(SbCode.DB_EXCEPTION.getMessage());
            log.error("getCount:", e);
            return response;
        }
        return response;
    }

}
