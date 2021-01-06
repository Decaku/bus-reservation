package com.stylefeng.guns.rest.modular.bus.converter;

import com.stylefeng.guns.rest.bus.dto.BusDto;
import com.stylefeng.guns.rest.common.convert.DateMapper;
import com.stylefeng.guns.rest.common.persistence.model.Bus;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-01-06T10:51:17+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_275 (AdoptOpenJDK)"
)
@Component
public class BusConverterImpl implements BusConverter {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public List<BusDto> bus2List(List<Bus> buses) {
        if ( buses == null ) {
            return null;
        }

        List<BusDto> list = new ArrayList<BusDto>( buses.size() );
        for ( Bus bus : buses ) {
            list.add( busToBusDto( bus ) );
        }

        return list;
    }

    protected BusDto busToBusDto(Bus bus) {
        if ( bus == null ) {
            return null;
        }

        BusDto busDto = new BusDto();

        busDto.setUuid( bus.getUuid() );
        busDto.setLimitNumber( bus.getLimitNumber() );
        busDto.setDriverName( bus.getDriverName() );
        busDto.setSeatsNumber( bus.getSeatsNumber() );
        busDto.setBeginTime( dateMapper.asString( bus.getBeginTime() ) );
        busDto.setUpdateTime( dateMapper.asString( bus.getUpdateTime() ) );

        return busDto;
    }
}
