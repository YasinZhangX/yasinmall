package com.yasinmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.dao.ShippingMapper;
import com.yasinmall.pojo.Shipping;
import com.yasinmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author yasin
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * 新增地址
     */
    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount > 0) {
            Map result = Maps.newHashMap();
            result.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccessMD("新建地址成功", result);
        }

        return ServerResponse.createByErrorM("新建地址失败");
    }

    /**
     * 删除地址
     */
    @Override
    public ServerResponse delete(Integer userId, Integer shippingId) {
        int resultCount = shippingMapper.deleteByShippingIdUserId(userId, shippingId);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessM("删除地址成功");
        } else {
            return ServerResponse.createByErrorM("删除地址失败");
        }
    }

    /**
     * 更新地址
     */
    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int resultCount = shippingMapper.updateByShipping(shipping);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessM("更新地址成功");
        } else {
            return ServerResponse.createByErrorM("更新地址失败");
        }
    }

    /**
     * 查询地址
     */
    @Override
    public ServerResponse select(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId, shippingId);
        if (shipping == null) {
            return ServerResponse.createByErrorM("无法查询到改地址");
        } else {
            return ServerResponse.createBySuccessMD("查询地址成功", shipping);
        }
    }

    /**
     * 列出地址
     */
    @SuppressWarnings("unchecked")
    public ServerResponse list(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccessD(pageInfo);
    }
}
