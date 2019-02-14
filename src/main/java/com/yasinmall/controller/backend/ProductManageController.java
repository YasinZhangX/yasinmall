package com.yasinmall.controller.backend;

import com.google.common.collect.Maps;
import com.yasinmall.common.Const;
import com.yasinmall.common.ResponseCode;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.Product;
import com.yasinmall.pojo.User;
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
import javax.servlet.http.HttpSession;
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
     * @param session 用户session
     * @param product 产品数据
     * @return ServerResponse
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 用户为管理员，添加产品
            return iProductService.manageSaveOrUpdateProduct(product);
        } else {
            return ServerResponse.createByErrorM("无权限操作，需要管理员权限");
        }
    }

    /**
     * 更新产品销售状态
     *
     * @param session   用户session
     * @param productId 产品ID
     * @param status    产品销售状态
     * @return ServerResponse
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 用户为管理员，更新产品销售状态
            return iProductService.manageSetSaleStatus(productId, status);
        } else {
            return ServerResponse.createByErrorM("无权限操作，需要管理员权限");
        }
    }

    /**
     * 获取产品详细信息
     *
     * @param session   用户session
     * @param productId 产品ID
     * @return ServerResponse
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 用户为管理员，填充产品
            return iProductService.manageGetProductDetail(productId);
        } else {
            return ServerResponse.createByErrorM("无权限操作，需要管理员权限");
        }
    }

    /**
     * 获取产品列表
     *
     * @param session  用户session
     * @param pageNum  页码数
     * @param pageSize 页面大小
     * @return ServerResponse
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 用户为管理员，获取产品列表
            return iProductService.manageGetProductList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorM("无权限操作，需要管理员权限");
        }
    }


    /**
     * 搜索产品
     *
     * @param session     用户session
     * @param productName 产品名称
     * @param productId   产品ID
     * @param pageNum     页码数
     * @param pageSize    页面大小
     * @return ServerResponse
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session, String productName, Integer productId,
                                        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 用户为管理员，搜索产品
            return iProductService.manageSearchProduce(productName, productId, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorM("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            Map<String, String> fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);

            return ServerResponse.createBySuccessD(fileMap);
        } else {
            return ServerResponse.createByErrorM("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session,
                                 @RequestParam(value = "upload_file", required = false) MultipartFile file,
                                 HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "请登录管理员账户");
            return resultMap;
        }

        // 校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
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
        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限操作");
            return resultMap;
        }
    }

}
