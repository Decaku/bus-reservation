package com.stylefeng.guns.rest.modular.order.converter;

import com.stylefeng.guns.rest.common.convert.DateMapper;
import com.stylefeng.guns.rest.common.persistence.model.Order;
import com.stylefeng.guns.rest.order.dto.AddOrderRequest;
import com.stylefeng.guns.rest.order.dto.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateMapper.class)

public interface OrderConverter {

    @Mappings({})
    List<OrderDto> order2Res(List<Order> orders);

    @Mappings({})
    Order res2Order(AddOrderRequest request);

    @Mappings({})
    OrderDto order2Res(Order order);

}
