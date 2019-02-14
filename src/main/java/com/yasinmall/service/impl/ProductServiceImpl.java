package com.yasinmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yasinmall.common.Const;
import com.yasinmall.common.ResponseCode;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.dao.CategoryMapper;
import com.yasinmall.dao.ProductMapper;
import com.yasinmall.pojo.Category;
import com.yasinmall.pojo.Product;
import com.yasinmall.service.ICategoryService;
import com.yasinmall.service.IProductService;
import com.yasinmall.util.DateTimeUtil;
import com.yasinmall.util.PropertiesUtil;
import com.yasinmall.vo.ProductDetailVo;
import com.yasinmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yasin
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 新增或更新产品
     */
    @Override
    public ServerResponse manageSaveOrUpdateProduct(Product product) {
        if (product != null) {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getMainImage().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }

            if (product.getId() != null) {
                int rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccessM("更新产品成功");
                } else {
                    return ServerResponse.createByErrorM("更新产品失败");
                }
            } else {
                int rowCount = productMapper.insert(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccessM("新增产品成功");
                } else {
                    return ServerResponse.createByErrorM("新增产品失败");
                }
            }
        }

        return ServerResponse.createByErrorM("新增或更新产品参数不正确");
    }

    /**
     * 更新产品销售状态
     */
    @Override
    public ServerResponse<String> manageSetSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessM("修改产品销售状态成功");
        }

        return ServerResponse.createByErrorM("修改产品销售状态失败");
    }

    /**
     * （后端）获取产品数据
     */
    @Override
    public ServerResponse manageGetProductDetail(Integer productId) {
        // 检验产品是否存在
        ServerResponse resultRsp = isProductExist(productId);
        if (!resultRsp.isSuccess()) {
            return resultRsp;
        }

        Product product = (Product) resultRsp.getData();

        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccessD(productDetailVo);
    }

    /**
     * 获取产品列表
     */
    @Override
    public ServerResponse<PageInfo> manageGetProductList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();

        PageInfo pageInfo = BuildPageInfoFromProductList(productList);

        return ServerResponse.createBySuccessD(pageInfo);
    }

    /**
     * 搜索产品
     */
    @Override
    public ServerResponse<PageInfo> manageSearchProduce(String productName, Integer productId, int pageNum, int pageSize) {
        if (StringUtils.isBlank(productName) && productId == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)) {
            productName = "%" + productName + "%";
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);

        PageInfo pageInfo = BuildPageInfoFromProductList(productList);

        return ServerResponse.createBySuccessD(pageInfo);
    }

    /**
     * （前端）获取产品数据
     */
    @Override
    public ServerResponse getProductDetail(Integer productId) {
        // 检验产品是否存在
        ServerResponse resultRsp = isProductExist(productId);
        if (!resultRsp.isSuccess()) {
            return resultRsp;
        }

        Product product = (Product) resultRsp.getData();
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorM("产品已下架");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccessD(productDetailVo);
    }

    /**
     * (前端）搜索产品，并返回产品列表页面
     */
    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId,
                                                                int pageNum, int pageSize, String orderBy) {
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        List<Integer> categoryIdList = new ArrayList<>();
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null) {
                if (StringUtils.isBlank(keyword)) {
                    // 没有该分类，并且没有关键字,此时返回空结果,不报错
                    PageHelper.startPage(pageNum, pageSize);
                    List<ProductListVo> productListVoList = Lists.newArrayList();
                    PageInfo pageInfo = new PageInfo(productListVoList);
                    return ServerResponse.createBySuccessD(pageInfo);
                }
            } else {
                categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
            }
        }

        if (StringUtils.isNotBlank(keyword)) {
            keyword = "%" + keyword + "%";
        }

        PageHelper.startPage(pageNum, pageSize);
        // 排序处理
        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null : keyword,
                categoryIdList.size() == 0 ? null : categoryIdList);

        PageInfo pageInfo = BuildPageInfoFromProductList(productList);

        return ServerResponse.createBySuccessD(pageInfo);
    }

    /**
     * 将产品的数据库数据转换为对象数据(vo)
     */
    private ProductDetailVo assembleProductDetailVo(@NotNull Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        // imageHost
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.yasinmall.com/"));

        // parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category != null) {
            productDetailVo.setParentCategoryId(category.getParentId());
        } else {
            // 默认根节点？？
            productDetailVo.setParentCategoryId(0);
        }

        // createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        // updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    /**
     * 将产品列表转换为页面数据
     */
    @SuppressWarnings("unchecked")
    private PageInfo BuildPageInfoFromProductList(List<Product> productList) {
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return pageInfo;
    }

    /**
     * 将产品列表的数据转换为对象数据(vo)
     */
    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.yasinmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    /**
     * 确定产品是否存在
     */
    private ServerResponse isProductExist(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorM("产品已删除");
        }

        return ServerResponse.createBySuccessD(product);
    }
}
