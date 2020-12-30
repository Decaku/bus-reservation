package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.stylefeng.guns.rest.bus.dto.CountDetailDto;
import com.stylefeng.guns.rest.bus.dto.CountSimpleDto;
import com.stylefeng.guns.rest.common.persistence.model.Count;
import org.apache.ibatis.annotations.Param;

public interface CountMapper extends BaseMapper<Count> {
    /**
     * @param page
     * @param wrapper
     * @return
     */
    IPage<CountSimpleDto> selectCounts(IPage<CountSimpleDto> page, @Param(Constants.WRAPPER) Wrapper<CountSimpleDto> wrapper);

    /**
     * @param wrapper
     * @return
     */
    CountDetailDto selectCountDetailById(@Param(Constants.WRAPPER) Wrapper<CountDetailDto> wrapper);
}
