package com.yasinmall.controller.backend;

import com.google.common.collect.Maps;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.Product;
import com.yasinmall.service.IFileService;
import com.yasinmall.service.IProductService;
import com.yasinmall.service.IUserService;
import com.yasinmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author yasin
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     * 新增或更新产品
     *
     * @param httpServletRequest 用户httpServletRequest
     * @param product 产品数据
     * @return ServerResponse
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpServletRequest httpServletRequest, Product product) {
        // 用户为管理员，添加产品
        return iProductService.manageSaveOrUpdateProduct(product);
    }

    /**
     * 更新产品销售状态
     *
     * @param httpServletRequest   用户httpServletRequest
     * @param productId 产品ID
     * @param status    产品销售状态
     * @return ServerResponse
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpServletRequest httpServletRequest, Integer productId, Integer status) {
        // 用户为管理员，更新产品销售状态
        return iProductService.manageSetSaleStatus(productId, status);
    }

    /**
     * 获取产品详细信息
     *
     * @param httpServletRequest   用户httpServletRequest
     * @param productId 产品ID
     * @return ServerResponse
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpServletRequest httpServletRequest, Integer productId) {
        // 用户为管理员，填充产品
        return iProductService.manageGetProductDetail(productId);
    }

    /**
     * 获取产品列表
     *
     * @param httpServletRequest  用户httpServletRequest
     * @param pageNum  页码数
     * @param pageSize 页面大小
     * @return ServerResponse
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpServletRequest httpServletRequest, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        // 用户为管理员，获取产品列表
        return iProductService.manageGetProductList(pageNum, pageSize);
    }


    /**
     * 搜索产品
     *
     * @param httpServletRequest     用户httpServletRequest
     * @param productName 产品名称
     * @param productId   产品ID
     * @param pageNum     页码数
     * @param pageSize    页面大小
     * @return ServerResponse
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpServletRequest httpServletRequest, String productName, Integer productId,
                                        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        // 用户为管理员，搜索产品
        return iProductService.manageSearchProduce(productName, productId, pageNum, pageSize);
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpServletRequest httpServletRequest, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

        Map<String, String> fileMap = Maps.newHashMap();
        fileMap.put("uri", targetFileName);
        fileMap.put("url", url);

        return ServerResponse.createBySuccessD(fileMap);
    }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(@RequestParam(value = "upload_file", required = false) MultipartFile file,
                                 HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = Maps.newHashMap();

        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        if (StringUtils.isBlank(targetFileName)) {
            resultMap.put("success", false);
            resultMap.put("msg", "上传失败");
            return resultMap;
        }
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
        resultMap.put("success", true);
        resultMap.put("msg", "上传成功");
        resultMap.put("file_path", url);

        response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
        return resultMap;
    }
}
