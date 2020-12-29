package com.stylefeng.guns.rest.bus;

import com.stylefeng.guns.rest.bus.dto.*;

public interface IBusService {
    /**
     * 分页获取班车
     * @param request
     * @return
     */
    PageBusResponse getBus(PageBusRequest request);

    /**
     * 按照系统当前分页获取场次
     * @param request
     * @return
     */
    PageCountResponse getCount(PageCountRequest request);

    /**
     * 根据场次id获取场次详情
     * @param request
     * @return
     */
    CountDetailResponse getCountDetailById(CountDetailRequest request);

    /**
     * 入参，座位号和场次号
     * 出参，是否有重复
     * @param seats
     * @param countId
     * @return
     */
    boolean repeatSeats(String seats, Long countId);

    /**
     * 更新座位信息
     * @param seats
     * @param countId
     * @return
     */
     boolean addSeats(String seats, Long countId);

    /**
     * 去掉countId对应的场次中已出现的座位
     */
    boolean filterRepeatSeats(String seats, Long countId);

    /**
     * 每天上午7点到晚上21点，每隔30分钟执行一次
     */
    void schedulChangeBusStatus();


    /**
     * 每天凌晨0点1分执行
     */
    void addCounts();

}
