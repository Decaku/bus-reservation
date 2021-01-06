package com.stylefeng.guns.rest.modular.order.converter;

import com.stylefeng.guns.rest.common.convert.DateMapper;
import com.stylefeng.guns.rest.common.persistence.model.Order;
import com.stylefeng.guns.rest.order.dto.AddOrderRequest;
import com.stylefeng.guns.rest.order.dto.OrderDto;
import com.stylefeng.guns.rest.order.dto.OrderRequest;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-01-06T15:28:43+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_275 (AdoptOpenJDK)"
)
@Component
public class OrderConverterImpl implements OrderConverter {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public List<OrderDto> order2Res(List<Order> orders) {
        if ( orders == null ) {
            return null;
        }

        List<OrderDto> list = new ArrayList<OrderDto>( orders.size() );
        for ( Order order : orders ) {
            list.add( order2Res( order ) );
        }

        return list;
    }

    @Override
    public Order res2Order(AddOrderRequest request) {
        if ( request == null ) {
            return null;
        }

        Order order = new Order();

        order.setCountId( request.getCountId() );
        order.setBusStatus( request.getBusStatus() );
        order.setSeatsIds( request.getSeatsIds() );
        order.setCountPrice( request.getCountPrice() );
        order.setOrderUser( request.getOrderUser() );
        order.setUserId( request.getUserId() );

        return order;
    }

    @Override
    public OrderDto order2Res(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDto orderDto = new OrderDto();

        orderDto.setUuid( order.getUuid() );
        orderDto.setCountId( order.getCountId() );
        orderDto.setBusStatus( order.getBusStatus() );
        orderDto.setSeatsIds( order.getSeatsIds() );
        orderDto.setCountPrice( order.getCountPrice() );
        orderDto.setOrderPrice( order.getOrderPrice() );
        orderDto.setOrderUser( order.getOrderUser() );
        orderDto.setOrderStatus( order.getOrderStatus() );
        orderDto.setOrderTime( dateMapper.asString( order.getOrderTime() ) );

        return orderDto;
    }

    @Override
    public Order res2Order(OrderRequest request) {
        if ( request == null ) {
            return null;
        }

        Order order = new Order();

        order.setUuid( request.getUuid() );
        order.setOrderStatus( request.getOrderStatus() );

        return order;
    }
}
