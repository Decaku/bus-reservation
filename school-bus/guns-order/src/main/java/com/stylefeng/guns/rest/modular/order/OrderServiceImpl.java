package com.stylefeng.guns.rest.modular.order;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stylefeng.guns.core.constants.MqTags;
import com.stylefeng.guns.core.constants.RedisConstants;
import com.stylefeng.guns.core.constants.SbCode;
import com.stylefeng.guns.rest.bus.IBusService;
import com.stylefeng.guns.rest.common.persistence.dao.OrderMapper;
import com.stylefeng.guns.rest.common.persistence.model.Order;
import com.stylefeng.guns.rest.modular.order.converter.OrderConverter;
import com.stylefeng.guns.rest.mq.MQDto;
import com.stylefeng.guns.rest.myutils.UUIDUtils;
import com.stylefeng.guns.rest.order.IOrderService;
import com.stylefeng.guns.rest.order.dto.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.apache.dubbo.config.annotation.Service;

import org.apache.dubbo.config.annotation.Reference;
import com.stylefeng.guns.core.util.DateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;






@Slf4j
@Component
@Service
public class OrderServiceImpl implements IOrderService {

    // rocket mq topic 和 tag
    @Value("${mq.order.topic}")
    private String topic;

    private String tag;

    @Autowired
    private OrderMapper orderMapper;

    @Reference(check = false)
    private IBusService busService;

    @Autowired
    private OrderConverter orderConverter;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    /**
     * 返回未乘坐列表
     * @param request
     * @return
     */
    @Override
    public NoTakeBusResponse getNoTakeOrdersById(NoTakeBusRequest request) {
        NoTakeBusResponse response = new NoTakeBusResponse();
        IPage<NoTakeDto> orderIPage = new Page<>(request.getCurrentPage(), request.getPageSize());
        QueryWrapper<NoTakeDto> queryWrapper = new QueryWrapper<>();

        String day = DateUtil.getDay();
        String hours = DateUtil.getHours();
        System.out.println("当前日期:" + day);
        System.out.println("当前时间:" + hours);
        queryWrapper
                .eq("so.user_id", request.getUserId())
                .eq("so.order_status", "1")// 1：已经支付
                .ge("sc.begin_date", day)
                .ge("sc.begin_time", hours)
                .orderByAsc("sc.begin_time")
                .orderByDesc("so.order_time");
//        orderIPage = orderMapper.selectPage(orderIPage, queryWrapper);
        try {
            orderIPage = orderMapper.selectNoTakeOrders(orderIPage, queryWrapper);
            response.setCurrentPage(orderIPage.getCurrent());
            response.setPages(orderIPage.getPages());
            response.setPageSize(orderIPage.getSize());
            response.setTotal(orderIPage.getTotal());
            response.setNoTakeDtos(orderIPage.getRecords());
            response.setCode(SbCode.SUCCESS.getCode());
            response.setMsg(SbCode.SUCCESS.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getNoTakeOrdersById:",e);
            response.setCode(SbCode.DB_EXCEPTION.getCode());
            response.setMsg(SbCode.DB_EXCEPTION.getMsg());
            return response;
        }
        return response;
    }


    /**
     * 该业务可以和上一个业务合并
     * 返回未评价和评价的列表
     * @param request
     * @return
     */
    @Override
    public EvaluateResponse getEvaluateOrdersById(EvaluateRequest request) {
        EvaluateResponse response = new EvaluateResponse();
        IPage<EvaluateDto> orderIPage = new Page<>(request.getCurrentPage(), request.getPageSize());
        QueryWrapper<EvaluateDto> queryWrapper = new QueryWrapper<>();
        // 获取系统年月日
        String day = DateUtil.getDay();
        String hours = DateUtil.getHours();
        System.out.println("当前日期:" + day);
        System.out.println("当前时间:" + hours);
        queryWrapper
                .eq("so.user_id", request.getUserId())
                .eq("so.order_status", "1")
                .and(o -> o.eq("sc.begin_date", day)
                        .lt("sc.begin_time", hours)
                        .or().lt("sc.begin_date", day))
                .eq("evaluate_status", request.getEvaluateStatus())
                .orderByDesc("sc.begin_time")
                .orderByDesc("so.order_time");
        try {
            orderIPage = orderMapper.selectEvaluateOrders(orderIPage, queryWrapper);
            response.setCurrentPage(orderIPage.getCurrent());
            response.setPages(orderIPage.getPages());
            response.setPageSize(orderIPage.getSize());
            response.setTotal(orderIPage.getTotal());
            response.setEvaluateDtos(orderIPage.getRecords());
            response.setCode(SbCode.SUCCESS.getCode());
            response.setMsg(SbCode.SUCCESS.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getEvaluateOrdersById:",e);
            response.setCode(SbCode.DB_EXCEPTION.getCode());
            response.setMsg(SbCode.DB_EXCEPTION.getMsg());
            return response;
        }
        return response;
    }


    /**
     * 返回未支付列表
     * @param request
     * @return
     */
    @Override
    public NoPayResponse getNoPayOrdersById(NoPayRequest request) {
        NoPayResponse response = new NoPayResponse();
        IPage<NoPayDto> noPayDtoIPage = new Page<>(request.getCurrentPage(), request.getPageSize());
        QueryWrapper<NoPayDto> queryWrapper = new QueryWrapper<>();
        // 获取系统年月日
        String day = DateUtil.getDay();
        String hours = DateUtil.getHours();
        System.out.println("当前日期:" + day);
        System.out.println("当前时间:" + hours);
        queryWrapper
                .eq("so.user_id", request.getUserId())
                .eq("so.order_status", "0")
                .ge("sc.begin_date", day) // 比如，
                .ge("sc.begin_time", hours)
                .orderByDesc("sc.begin_time")
                .orderByDesc("so.order_time"); // 未支付
        try {
            noPayDtoIPage = orderMapper.selectNoPayOrders(noPayDtoIPage, queryWrapper);
            response.setCurrentPage(noPayDtoIPage.getCurrent());
            response.setPages(noPayDtoIPage.getPages());
            response.setPageSize(noPayDtoIPage.getSize());
            response.setTotal(noPayDtoIPage.getTotal());
            response.setNoPayDtos(noPayDtoIPage.getRecords());
            response.setCode(SbCode.SUCCESS.getCode());
            response.setMsg(SbCode.SUCCESS.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getNoPayOrdersById:",e);
            response.setCode(SbCode.DB_EXCEPTION.getCode());
            response.setMsg(SbCode.DB_EXCEPTION.getMsg());
            return response;
        }
        return response;
    }


    /**
     * 添加订单 （核心函数）
     * @param request
     * @return
     */
    @Override
    public AddOrderResponse addOrder(AddOrderRequest request) {
        AddOrderResponse response = new AddOrderResponse();

        Long orderId = UUIDUtils.flakesUUID();  // 全局唯一的id

        try {
            // 1. 座位判重
            tag = MqTags.ORDER_SEATS_CANCEL.getTag();
            boolean repeatSeats = busService.repeatSeats(request.getSeatsIds(), request.getCountId());
            if(repeatSeats) { // 订单里的座位号有被使用乐的
                response.setCode(SbCode.SELECTED_SEATS.getCode());
                response.setMsg(SbCode.SELECTED_SEATS.getMsg());
                return response;
            }

            // 2. 更新座位
            tag = MqTags.ORDER_ADD_SEATS_CANCLE.getTag();
            boolean addSeats = busService.addSeats(request.getSeatsIds(), request.getCountId());
            if (!addSeats) {
                response.setCode(SbCode.DB_EXCEPTION.getCode());
                response.setMsg(SbCode.DB_EXCEPTION.getMsg());
                return response;
            }

            // 3. 计算总金额
            tag = MqTags.ORDER_CALC_MONEY_CANCLE.getTag();
            String seatIds = request.getSeatsIds();
            Integer seatNumber = seatIds.split(",").length;
            Double countPrice = request.getCountPrice();
            Double totalPrice = getTotalPrice(seatNumber, countPrice);

            // 4. 添加订单
            tag = MqTags.ORDER_ADD_CANCLE.getTag();
            Order order = orderConverter.res2Order(request);
            order.setOrderPrice(totalPrice);
            order.setEvaluateStatus("0"); // 未评价
            order.setOrderStatus("0"); // 未支付
            order.setUuid(orderId); // 唯一id
            orderMapper.insert(order); // 插入 不判断了
            response.setCode(SbCode.SUCCESS.getCode());
            response.setMsg(SbCode.SUCCESS.getMsg());
            response.setOrderId(orderId);

            // redis设置订单过期时间，缓存过期时间由前端传入
            redisUtils.set(RedisConstants.ORDER_CANCLE_EXPIRE.getKey() + Convert.toStr(orderId), orderId, request.getExpireTime());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("addOrder failed, err = " + e.toString());

            // 如果订单发生异常，需要用MQ回退消息
            MQDto mqDto = new MQDto();
            mqDto.setOrderId(orderId);
            mqDto.setCountId(request.getCountId());
            mqDto.setSeatsIds(request.getSeatsIds());
            try {
                String key = RedisConstants.ORDER_EXCEPTION_CANCLE_EXPIRE.getKey() + Convert.toStr(orderId);
                sendCancelOrder(topic,tag, key, JSON.toJSONString(mqDto));
                log.warn("订单回退消息发送成功..." + mqDto);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            response.setCode(SbCode.SYSTEM_ERROR.getCode());
            response.setMsg(SbCode.SYSTEM_ERROR.getMsg());
            return response;
        }
    }


    /**
     * 根据订单id返回订单信息
     * @param request
     * @return
     */
    @Override
    public OrderResponse selectOrderById(OrderRequest request) {
        OrderResponse response = new OrderResponse();
        try {
            QueryWrapper<OrderDto> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("so.uuid", request.getUuid());
            OrderDto orderDto = orderMapper.selectOrderById(queryWrapper);
            response.setOrderDto(orderDto);
            response.setCode(SbCode.SUCCESS.getCode());
            response.setMsg(SbCode.SUCCESS.getMsg());
        } catch(Exception e) {
            e.printStackTrace();
            log.error("selectOrderById", e);
            response.setCode(SbCode.DB_EXCEPTION.getCode());
            response.setMsg(SbCode.DB_EXCEPTION.getMsg());
        }
        return response;
    }


    /**
     * 更新订单状态 0-待支付,1-已支付,2-已关闭/
     * @param request
     * @return
     */
    @Override
    public OrderResponse updateOrderStatus(OrderRequest request) {
        OrderResponse response = new OrderResponse();
        try {
            // 获取OrderDto
            QueryWrapper<OrderDto> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("so.uuid", request.getUuid());
            OrderDto orderDto = orderMapper.selectOrderById(queryWrapper);

            // 1. 检查状态是否为2
            if(request.getOrderStatus().equals("2")) {
                // 关闭订单，回退座位。 清除缓存
                busService.filterRepeatSeats(orderDto.getSeatsIds(), orderDto.getCountId());
                redisUtils.del(RedisConstants.COUNT_DETAIL_EXPIRE.getKey()
                        + orderDto.getCountId());
            } else if(request.getOrderStatus().equals("1")) {
                // 已支付，删除缓存
                redisUtils.del(RedisConstants.ORDER_CANCLE_EXPIRE.getKey() + request.getUuid());
            }

            Order order = orderConverter.res2Order(request);

            orderMapper.updateById(order);
            response.setCode(SbCode.SUCCESS.getCode());
            response.setMsg(SbCode.SUCCESS.getMsg());
            redisUtils.del(RedisConstants.NO_PAY_ORDERS_EXPIRE.getKey()+order.getUserId());
            redisUtils.del(RedisConstants.SELECT_ORDER_EXPIRE.getKey() + request.getUuid());
        } catch (Exception e) {
            log.error("updateOrderStatus", e);
            response.setCode(SbCode.DB_EXCEPTION.getCode());
            response.setMsg(SbCode.DB_EXCEPTION.getMsg());
            return response;
        }
        return response;
    }


    /**
     * 根据订单id删除订单, 这里的设计有点问题，从业务角度来考虑 最好不要对数据库里的数据硬删除
     * @param OrderId
     * @return
     */
    @Override
    public boolean deleteOrderById(Long OrderId) {
        try {
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uuid", OrderId);
            orderMapper.delete(queryWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("deleteOrderById:", e);
            return false;
        }
        return true;
    }


    /**
     * 计算总价格
     * @param seatNumbers：座位数量
     * @param countPrice：场次价格
     * @return
     */
    private double getTotalPrice(Integer seatNumbers, double countPrice) {
        BigDecimal seatNumbersDeci = new BigDecimal(seatNumbers);
        BigDecimal countPriceDeci = new BigDecimal(countPrice);
        BigDecimal result = seatNumbersDeci.multiply(countPriceDeci);
        // 四舍五入，取小数点后一位
        BigDecimal bigDecimal = result.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    /**
     * 发送订单回退消息
     * @param topic
     * @param tag
     * @param keys
     * @param body
     * @throws Exception
     */
    private void sendCancelOrder(String topic, String tag, String keys, String body) throws Exception{
        Message message = new Message(topic,tag,keys,body.getBytes());
        rocketMQTemplate.getProducer().send(message);
    }
}
