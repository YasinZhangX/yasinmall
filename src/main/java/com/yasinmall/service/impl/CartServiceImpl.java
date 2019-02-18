package com.yasinmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.yasinmall.common.Const;
import com.yasinmall.common.ResponseCode;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.dao.CartMapper;
import com.yasinmall.dao.ProductMapper;
import com.yasinmall.pojo.Cart;
import com.yasinmall.pojo.Product;
import com.yasinmall.service.ICartService;
import com.yasinmall.util.BigDecimalUtil;
import com.yasinmall.util.PropertiesUtil;
import com.yasinmall.vo.CartProductVo;
import com.yasinmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yasin
 */
@Service("iCateService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 查询用户购物车数据
     */
    @Override
    public ServerResponse list(Integer userId) {
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccessD(cartVo);
    }

    /**
     * 向购物车添加产品
     */
    @Override
    public ServerResponse add(Integer userId, Integer productId, Integer count) {
        if (!isNotNull(productId, count)) {
            return illegalArgRsp();
        }

        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart == null) {
            // 该产品不在购物车中,需要新增该产品记录
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);

            cartMapper.insert(cartItem);
        } else {
            // 该产品已存在购物车中,增加数量
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKey(cart);
        }

        return this.list(userId);
    }

    /**
     * 更新购物车内产品数量
     */
    @Override
    public ServerResponse update(Integer userId, Integer productId, Integer count) {
        if (!isNotNull(productId, count)) {
            return illegalArgRsp();
        }

        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);

        return this.list(userId);
    }

    /**
     * 删除购物车内产品
     */
    @Override
    public ServerResponse deleteProduct(Integer userId, String productIds) {
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productIdList)) {
            return illegalArgRsp();
        }
        cartMapper.deleteByUserIdProductIds(userId, productIdList);

        return this.list(userId);
    }

    /**
     * 选择购物车中产品
     */
    @Override
    public ServerResponse selectOrUnselect(Integer userId, Integer productId, Integer checked) {
        cartMapper.checkedOrUncheckedProduct(userId, productId, checked);
        return this.list(userId);
    }

    /**
     * 获取购物差中产品数量
     */
    public ServerResponse getCartProductCount(Integer userId) {
        if (!isNotNull(userId)) {
            return ServerResponse.createBySuccessD(0);
        }

        return ServerResponse.createBySuccessD(cartMapper.selectCartProductCount(userId));
    }

    /**
     * 获取当前用户购物车数据
     */
    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (!CollectionUtils.isEmpty(cartList)) {
            for (Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());


                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());

                    // 判断库存
                    int buyLimitCount;
                    if (product.getStock() >= cartItem.getQuantity()) {
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        // 购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);

                    // 计算当前产品总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity().doubleValue()));
                    // 勾选产品
                    cartProductVo.setIsProductChecked(cartItem.getChecked());
                }

                if (cartItem.getChecked() == Const.Cart.CHECKED) {
                    // 如果已经勾选，增加到整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }

                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;
    }

    /**
     * 查看当前购物车是否全选
     */
    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }

        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }

    /**
     * 检查是否参数都不为null
     *
     * @return 若都不为NULL, 返回true;反之,返回false
     */
    private boolean isNotNull(Object... param) {
        boolean result = true;
        for (Object obj : param) {
            if (obj == null) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * 返回参数错误
     */
    private ServerResponse illegalArgRsp() {
        return ServerResponse.createByErrorCodeM(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
    }
}
