package space.liuhao.http_request_library;


import android.graphics.Bitmap;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Http请求二次封装接口定义
 *
 * @author 刘浩 2015-11-07 17:44:31
 * @version 2.0.0
 */
@SuppressWarnings("unused")
public interface I_HttpRequest{

    /**
     * Put请求
     *
     * @param requestAddress   请求服务器地址
     * @param requestParameter 请求参数构成的Json字符串
     * @return 请求结果的字符串，结果格式取决于服务器接口
     * @throws Exception 当服务器地址、请求参数为Null、空字符串、发送请求过程中或Http返回值非200时抛出异常
     */
    String putRequest(String requestAddress, String requestParameter) throws Exception;


    /**
     * Delete请求
     *
     * @param requestAddress   请求服务器地址
     * @param requestParameter 请求参数构成的Json字符串
     * @return 请求结果的字符串，结果格式取决于服务器接口
     * @throws Exception 当服务器地址、请求参数为Null、空字符串、发送请求过程中或Http返回值非200时抛出异常
     */
    String deleteRequest(String requestAddress, String requestParameter) throws Exception;


    /**
     * Post请求
     *
     * @param requestAddress   请求目的地址
     * @param requestParameter 请求参数
     * @return 请求结果的字符串，结果格式取决于服务器接口
     * @throws Exception 当服务器、请求参数为Null、空字符串、请求过程发生异常或请求返回状态码非200时抛出异常
     */
    String postRequest(String requestAddress, String requestParameter) throws Exception;

    /**
     * 从图片服务器下载指定宽度的图片
     *
     * @param imageLocalDir      图片本地存放目录
     * @param imageName          图片名
     * @param imageServerAddress 图片服务器地址
     * @param width              图片宽度
     * @return 图片文件实体
     * @throws Exception 下列情况将抛出异常：1、当图片本地存放路径、图片名、图片服务器地址、
     *                   请求参数为Null、空字符串时。2、请求过程发生异常时。3、要下载的图片在本地文件已存在时。
     *                   4、文件写入过程发生异常时。5、图片宽度小于等于0时。
     */
    File getImage(String imageLocalDir, String imageName, String imageServerAddress, int width) throws Exception;

    /**
     * @param requestAddress   请求目的地址
     * @param requestParameter 请求参数
     * @return 请求结果的字符串，结果格式取决于服务器接口
     * @throws Exception 当服务器、请求参数为Null、空字符串或请求过程发生异常时抛出异常
     * @deprecated 发送一个Get请求
     */
    String getRequest(String requestAddress, String requestParameter) throws Exception;

    /**
     * get方式请求一个位图
     *
     * @param requestAddress   请求目的地址
     * @param requestParameter 请求参数
     * @return 一个位图对象
     * @throws Exception 当服务器为Null、空字符串或请求过程发生异常时抛出异常
     */
    Bitmap getRequestBitMap(String requestAddress, String requestParameter) throws Exception;


    /**
     * Post方式上传文件方法
     *
     * @param tfsAddress tfs系统地址
     * @param filePath   待上传文件路径
     * @return 请求结果的字符串，结果格式取决于服务器接口
     * @throws Exception 下列情况将会抛出异常：1、文件不存在。2、文件是一个目录。3、非标准文件。
     *                   4、当文件路径或文件名中不包含扩展名分隔符“.”时。5、请求过程发生异常时。6、当请求状态为非200时
     */
    String postWayUploadFile(String tfsAddress, String filePath) throws Exception;

    /**
     * 以Post方式提交一个图片文件
     *
     * @param imageServerAddress 图片服务器地址
     * @param filePath           图片文件路径
     * @return 请求结果的字符串，结果格式取决于服务器接口
     * @throws Exception 当要传送文件不存在、不是一个单一文件（即目录）、不是一个标准文件、
     *                   请求过程发生异常或请求返回状态为非200时抛出异常
     */
    String postWaySubmitImage(String imageServerAddress, String filePath) throws Exception;

    /**
     * 从图片服务器下载指定大小的图片
     *
     * @param imageLocalDir      图片本地存放目录
     * @param imageName          图片名
     * @param imageServerAddress 图片服务器地址
     * @param width              图片宽度
     * @param height             图片高度
     * @return 图片文件实体
     * @throws Exception 下列情况将抛出异常：1、当图片本地存放路径、图片名、图片服务器地址、
     *                   请求参数为Null、空字符串时。2、请求过程发生异常时。3、要下载的图片在本地文件已存在时。
     *                   4、文件写入过程发生异常时。5、图片宽度或高度小于等于0时。
     */
    File getImage(String imageLocalDir, String imageName, String imageServerAddress, int width, int height) throws Exception;

    /**
     * 从图片服务器下载原始图片
     *
     * @param imageLocalDir      图片本地存放目录
     * @param imageName          图片名
     * @param imageServerAddress 图片服务器地址
     * @return 图片文件实体
     * @throws Exception 下列情况将抛出异常：1、当图片本地存放路径、图片名、图片服务器地址、请求参数为Null、空字符串时。2、请求过程发生异常时
     *                   。3、要下载的图片在本地文件已存在时。4、文件写入过程发生异常时。
     */
    File getOriginalImage(String imageLocalDir, String imageName, String imageServerAddress) throws Exception;

    /**
     * 请求图片并保存到本地
     *
     * @param requestAddress   请求地址
     * @param requestParameter 请求参数
     * @param imageFile        图片文件实体
     * @throws Exception 当请求发生异常时抛出异常
     */
    void requestImageSaveLoacl(StringBuilder requestAddress, StringBuilder requestParameter, File imageFile) throws Exception;

    /**
     * 读入并写出数据
     *
     * @param inputStream  输入流实体
     * @param outputStream 输出流实体
     * @throws Exception 当输入（出）流为空时抛出异常
     */
    void readInAndWriteOutData(InputStream inputStream, OutputStream outputStream) throws Exception;

    /**
     * 读取文本请求结果
     *
     * @param httpURLConnection http连接实体
     * @return 文本返回结果或Null
     * @throws Exception 当hhtp连接实体为空时抛出异常
     */
    String readTextRequestResult(HttpURLConnection httpURLConnection) throws Exception;
}
