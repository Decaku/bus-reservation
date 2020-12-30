package com.stylefeng.guns.rest.modular.bus;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stylefeng.guns.core.constants.RedisConstants;
import com.stylefeng.guns.core.constants.SbCode;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.bus.IBusService;
import com.stylefeng.guns.rest.bus.dto.*;
import com.stylefeng.guns.rest.common.RedisUtils;
import com.stylefeng.guns.rest.common.persistence.dao.BusMapper;
import com.stylefeng.guns.rest.common.persistence.dao.CountMapper;
import com.stylefeng.guns.rest.common.persistence.model.Bus;
import com.stylefeng.guns.rest.common.persistence.model.Count;
import com.stylefeng.guns.rest.modular.bus.converter.BusConverter;
import com.stylefeng.guns.rest.myutils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


@Slf4j
@Component
@Service
public class BusServiceImpl implements IBusService {

    @Autowired
    private BusMapper busMapper;

    @Autowired
    private BusConverter busConverter;

    @Autowired
    private CountMapper countMapper;

    @Autowired
    private RedisUtils redisUtils;

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
            response.setMsg(SbCode.SUCCESS.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(SbCode.DB_EXCEPTION.getCode());
            response.setMsg(SbCode.DB_EXCEPTION.getMsg());
            log.error("getCount:", e);
            return response;
        }
        return response;
    }

    @Override
    public CountDetailResponse getCountDetailById(CountDetailRequest request) {
        CountDetailResponse response = new CountDetailResponse();
        try {
            QueryWrapper<CountDetailDto> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("sc.uuid", request.getCountId());
            CountDetailDto countDetailDto = countMapper.selectCountDetailById(queryWrapper);
            response.setCountDetailDto(countDetailDto);
            response.setCode(SbCode.SUCCESS.getCode());
            response.setMsg(SbCode.SUCCESS.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getCountDetailById failed, err = " + e.toString());
            response.setCode(SbCode.DB_EXCEPTION.getCode());
            response.setMsg(SbCode.DB_EXCEPTION.getMsg());
            return response;
        }
        return response;
    }

    @Override
    public boolean repeatSeats(String seats, Long countId) {
        try {
            Count count = countMapper.selectById(countId);
            if(seats.equals("")) return true;
            if(count.getSelectedSeats().equals("")) return false;
            String ss[] = seats.split(",");
            String cs[] = count.getSelectedSeats().split(",");
            HashSet<String> hashSet = new HashSet<>(Arrays.asList(cs));
            for(String s : ss) {
                if(hashSet.contains(s)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("repeatSeats failed, err = " + e.toString());
            return true;
        }
        return false;
    }

    @Override
    public boolean addSeats(String seats, Long countId) {
        try {
            Count count = countMapper.selectById(countId);
            String selectedSeats = count.getSelectedSeats();
            String newSelectedSeats = seats;
            if(!selectedSeats.equals("")) {
                newSelectedSeats = selectedSeats + newSelectedSeats;
            }
            count.setSelectedSeats(newSelectedSeats);
            countMapper.updateById(count);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("addSeats failed, err = " + e.toString());
            return false;
        }
        return true;
    }

    @Override
    public boolean filterRepeatSeats(String seats, Long countId) {
        try {
            Count count = countMapper.selectById(countId);
            String[] ss = seats.split(",");
            String[] cs = count.getSelectedSeats().split(",");
            HashSet<String>hashSet = new HashSet<>(Arrays.asList(cs));
            for(String s : ss) {
                if(hashSet.contains(s)) {
                    hashSet.remove(s);
                }
            }
            if(hashSet.isEmpty()) {
                count.setSelectedSeats("");
            }
            StringBuffer sb = new StringBuffer();
            for(String s : hashSet) {
                sb.append(s);
                sb.append(",");
            }
            count.setSelectedSeats(sb.toString());
            countMapper.updateById(count);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("filterRepeatSeats failed, err = " + e.toString());
            return false;
        }
        return true;
    }

    @Override
    public void schedulChangeBusStatus() {
        // 获取时间
        String currTime = DateUtil.getHours();

        // 获取日期
        String day = DateUtil.getDay();

        log.info("schedulChangeBusStatus 当前时间" + currTime);
        log.info("schedulChangeBusStatus 当前日期" + day);

        QueryWrapper<Count>queryWrapper = new QueryWrapper<>();

        // 先取出beingtime和now相等的表或者end_time和now相等到表
        queryWrapper.eq("begin_date", day)
                    .and(o -> o.eq("begin_time", currTime).or().eq("end_time", currTime));
        List<Count> countList = countMapper.selectList(queryWrapper);
        log.info("schedulChangeBusStatus countList = " + countList);

        for(Count count : countList) {
            String busStatus = count.getBusStatus();
            String beginTime = count.getBeginTime();
            String endTime = count.getEndTime();
            if (currTime.equals(beginTime)) {
                if (busStatus.equals("0")) { // 沙河空闲
                    count.setBusStatus("2"); // 沙河->清水河
                }
                if (busStatus.equals("1")) { // 清水河空闲
                    count.setBusStatus("3"); // 清水河->沙河
                }
                count.setSelectedSeats(""); // 清空座位
            }
            if (currTime.equals(endTime)) {
                if (busStatus.equals("2")) { // 沙河->清水河
                    count.setBusStatus("1"); // 清水河空闲
                }
                if (busStatus.equals("3")) { // 清水河->沙河
                    count.setBusStatus("0"); // 沙河空闲
                }
            }
            System.out.println("修改的：" + count);
            log.info("schedulChangeBusStatus->修改的：" + count);
            // 写入数据库
            countMapper.updateById(count);
        }

        // 1. 获取key
        String countZeroKey = RedisConstants.COUNTS_PAGES_EXPIRE.getKey() + "0";
        String countOneKey = RedisConstants.COUNTS_PAGES_EXPIRE.getKey() + "1";

        // 2. 获取value
        Long countZeroPages = (Long) redisUtils.get(countZeroKey);
        Long countOnePages = (Long) redisUtils.get(countOneKey);

        // 3. 遍历删除
        for(int i = 1; i < countZeroPages; ++i) {
            String key = RedisConstants.COUNTS_EXPIRE + "0" + Integer.toString(i);
            redisUtils.del(key);
        }
        for(int i = 1; i < countOnePages; ++i) {
            String key = RedisConstants.COUNTS_EXPIRE + "1" + Integer.toString(i);
            redisUtils.del(key);
        }

        // 4. 更新场次列表的页数，但是， 这里不更新， 因为在接口那边会判断
    }

    /**
     * 每天添加一些场次
     */
    @Override
    public void addCounts() {
        // 获取日期
        String day = DateUtil.getDay();

        // 获取前17个场次
        QueryWrapper<Count>queryWrapper = new QueryWrapper<>();
        queryWrapper.last("limit 17");
        List<Count> countList = countMapper.selectList(queryWrapper);

        // 开始修改
        for(Count count : countList) {
            // 更改日期
            count.setBeginDate(day);

            // 更改uuid
            count.setUuid(UUIDUtils.flakesUUID());

            // 清空座位
            count.setSelectedSeats("");

            // 座位状态清0
            count.setSeatStatus("");

            // 插入
            countMapper.insert(count);
        }

        // 1. 添加完之后，调用场次列表查看总页数
        PageCountRequest request = new PageCountRequest();
        request.setBusStatus("0"); // 沙河
        request.setCurrentPage(Convert.toLong(1));
        request.setPageSize(Convert.toLong(5));
        PageCountResponse countResponse = this.getCount(request);
        Long countPagesZero = countResponse.getPages();
        log.info("沙河页数：" + countResponse.getPages());
        request.setBusStatus("1"); // 清水河
        PageCountResponse countResponse1 = this.getCount(request);
        Long countPagesOne = countResponse1.getPageSize();

        // 2. 构建key
        // 2、构建key
        String countPagesZeroKey = RedisConstants.COUNTS_PAGES_EXPIRE.getKey() + "0";
        String countPagesOneKey = RedisConstants.COUNTS_PAGES_EXPIRE.getKey() + "1";
        // 3、不用判断， 直接覆盖

        redisUtils.set(countPagesZeroKey, countPagesZero);
        redisUtils.set(countPagesOneKey, countPagesOne);
    }

    @Override
    public void scheduledTest() {
        log.info("Test 定时器");
    }
}
