package com.stylefeng.guns.rest.modular.pay;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.stylefeng.guns.core.constants.MqTags;
import com.stylefeng.guns.core.constants.RedisConstants;
import com.stylefeng.guns.core.constants.SbCode;
import com.stylefeng.guns.rest.alipay.IPayService;
import com.stylefeng.guns.rest.alipay.dto.PayBackRequest;
import com.stylefeng.guns.rest.alipay.dto.PayRequest;
import com.stylefeng.guns.rest.alipay.dto.PayResponse;
import com.stylefeng.guns.rest.bus.IBusService;
import com.stylefeng.guns.rest.mq.MQDto;
import com.stylefeng.guns.rest.order.IOrderService;
import com.stylefeng.guns.rest.order.dto.OrderRequest;
import com.stylefeng.guns.rest.user.IUserService;
import com.stylefeng.guns.rest.user.dto.UserDto;
import com.stylefeng.guns.rest.user.dto.UserRequest;
import com.stylefeng.guns.rest.user.dto.UserResponse;
import com.stylefeng.guns.rest.user.dto.UserUpdateInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.alibaba.dubbo.config.annotation.Reference;
import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;




@Slf4j
@Component
@Service
public class PayServiceImpl implements IPayService {
    @Value("${mq.pay.topic}")
    private String topic;

    private String tag;


    @Autowired
    private RedisUtils redisUtils;

    @Reference(check = false)
    private IUserService userService;

    @Reference(check = false)
    private IBusService busService;

    @Reference(check = false)
    private IOrderService orderService;


    @Autowired
    private RocketMQTemplate rocketMQTemplate;



    /**
     * 支付业务逻辑
     * @param request
     * @return
     */
    @Override
    public PayResponse pay(PayRequest request) {
        PayResponse payResponse = new PayResponse();
        Long userId = request.getUserId();
        Double userMoney = null;
        // 取用户信息，先去缓存取，取不到就读库
        try {
            tag = MqTags.PAY_CHECK_CANCLE.getTag();
            String key = RedisConstants.USER_INFO_EXPIRE.getKey() + userId;
            UserResponse userResponse = new UserResponse();
            if (redisUtils.hasKey(key)) {
                userResponse = (UserResponse) redisUtils.get(key);
            } else {
                UserRequest userRequest = new UserRequest();
                userRequest.setId(userId);
                // 获取用户信息
                userResponse = userService.getUserById(userRequest);
            }

            // 1. 校验支付密码
            if (!userResponse.getUserDto().getPayPassword().equals(request.getPayPassword())) {
                payResponse.setCode(SbCode.PAY_PASSWORD_ERROR.getCode());
                payResponse.setMsg(SbCode.PAY_PASSWORD_ERROR.getMsg());
                return payResponse;
            }

            // 2. 校验余额
            userMoney = userResponse.getUserDto().getMoney();
            Double subMoney = NumberUtil.sub(userMoney, request.getTotalMoney());
            BigDecimal round = NumberUtil.round(subMoney, 2);
            if (round.doubleValue() < 0) {
                payResponse.setCode(SbCode.MONEY_ERROR.getCode());
                payResponse.setMsg(SbCode.MONEY_ERROR.getMsg());
                return payResponse;
            }

            // 3. 更新用户信息(money)
            UserUpdateInfoRequest userUpdateInfoRequest = new UserUpdateInfoRequest();
            userUpdateInfoRequest.setId(userId);
            userUpdateInfoRequest.setMoney(round.doubleValue());
            tag = MqTags.PAY_MONEY_CANCLE.getTag();
            userService.updateUserInfo(userUpdateInfoRequest); // 暂时先不接受返回信息
            payResponse.setCode(SbCode.SUCCESS.getCode());
            payResponse.setMsg(SbCode.SUCCESS.getMsg());

            // 这里可以更新订单状态...
            return payResponse;
        } catch (Exception e) {
            log.error("支付业务发生异常");
            MQDto mqDto = new MQDto();
            mqDto.setUserId(userId);
            mqDto.setUserMoney(userMoney);
            // 发送消息
            try {
                String key = RedisConstants.PAY_EXCEPTION_CANCLE_EXPIRE.getKey() + Convert.toStr(userId);
                sendCancelPay(topic,tag,key, JSON.toJSONString(mqDto));
                log.warn("支付回退消息已发送");
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error("支付消息都崩的话...");
            }
            payResponse.setCode(SbCode.SYSTEM_ERROR.getCode());
            payResponse.setMsg(SbCode.SYSTEM_ERROR.getMsg());
            return payResponse;
        }
    }


    /**
     * 退款业务逻辑
     * @param request
     * @return
     */
    @Override
    public PayResponse payBack(PayBackRequest request) {
        PayResponse response = new PayResponse();
        try {
            // 1. 退回金额
            // 读用户金额
            UserRequest userRequest = new UserRequest();
            userRequest.setId(request.getUserId());
            UserResponse userResponse = userService.getUserById(userRequest);
            UserDto userDto = userResponse.getUserDto();
            // 计算金额
            BigDecimal add = NumberUtil.add(userDto.getMoney() + request.getTotalMoney());
            BigDecimal round = NumberUtil.round(add, 2);
            // 写回
            UserUpdateInfoRequest userUpdateInfoRequest = new UserUpdateInfoRequest();
            userUpdateInfoRequest.setId(request.getUserId());
            userUpdateInfoRequest.setMoney(round.doubleValue());
            userService.updateUserInfo(userUpdateInfoRequest);
            // 2. 退回座位
            busService.filterRepeatSeats(request.getSeatsIds(), request.getCoundId());
            // 3. 更改订单状态：关闭
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setUuid(request.getOrderId());
            orderRequest.setOrderStatus("3");
            orderService.updateOrderStatus(orderRequest);
            response.setCode(SbCode.SUCCESS.getCode());
            response.setMsg(SbCode.SUCCESS.getMsg());
            return  response;
        } catch (Exception e) {
            log.error("退款业务异常");
            // 这里可以发消息， 此处先省略
            response.setCode(SbCode.SYSTEM_ERROR.getCode());
            response.setMsg(SbCode.SYSTEM_ERROR.getMsg());
            return response;
        }
    }


    /**
     * 发送支付消息
     * @param topic
     * @param tag
     * @param keys
     * @param body
     * @throws Exception
     */
    private void sendCancelPay(String topic, String tag, String keys, String body) throws Exception {
        Message message = new Message(topic,tag,keys,body.getBytes());
        rocketMQTemplate.getProducer().send(message);
    }
}
