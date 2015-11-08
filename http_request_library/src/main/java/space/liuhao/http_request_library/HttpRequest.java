package space.liuhao.http_request_library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import space.liuhao.exception_lib.json.HttpPostKeyAndValueNumberDiscrepancy;
import space.liuhao.exception_lib.json.ParameterNotIsNullOrEmpty;

/**
 * 基于OkHttp的Http请求实现
 *
 * @author 刘浩 2015-11-07 17:46
 * @version 1.0.0
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class HttpRequest implements I_HttpRequest{
    /**
     * 请求一个响应实体
     *
     * @param request 请求体
     * @return 响应实体
     * @throws Exception 当发送请求过程中或Http返回值非200时抛出异常
     */
    private final ResponseBody requestResponseBody(Request request) throws Exception{
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            throw new Exception(e);
        }
        //判断Http请求返回值是否为200
        if (HttpURLConnection.HTTP_OK == response.code()) {
            //为200时返回请求结果结构体
            return response.body();
        } else {
            throw new Exception("Http请求返回码为：" + response.code());
        }
    }

    @Override
    public String putRequest(String requestAddress, String requestParameter) throws Exception{
        try {
            return requestResponseBody(buildPutRequest(requestAddress, requestParameter)).toString();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * 单例模式构造函数
     */
    private HttpRequest(){
        okHttpClient = new OkHttpClient();
        setTimeOut(okHttpClient);
    }

    /**
     * 获得一个HttpConnection实例
     *
     * @return HttpConnection 实例
     */
    public static synchronized HttpRequest getHttpInstance(){
        if (null == httpRequese) {
            httpRequese = new HttpRequest();
        }
        return httpRequese;
    }

    /**
     * 构造一个Put请求
     *
     * @param requestAddress   请求服务器地址
     * @param requestParameter 请求参数构成的Json字符串
     * @return 一个Post请求
     * @throws Exception 当服务器地址、请求参数为Null或空字符串时抛出异常
     */
    private final Request buildPutRequest(String requestAddress, String requestParameter) throws Exception{
        try {
            return new Request.Builder().url(requestAddress).put(buildRequestBody(requestAddress, requestParameter)).build();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * 构建Form请求体。可一次提交多个文本key/Value和多个文本Key/文件Value。其中文件的媒体类型需完全一致。
     *
     * @param pStringParams 文本参数列表
     * @param pStringValue  文本值列表
     * @param pFileParams   文件参数列表
     * @param pFileValue    文件列表
     * @param pMediaType    文件媒体类型
     * @return 请求提
     * @throws ParameterNotIsNullOrEmpty            当参数为null或空字符串时抛出异常
     * @throws HttpPostKeyAndValueNumberDiscrepancy 当参数列表和值列表的数量不一致时抛出异常
     */
    private final RequestBody buildRequestBody(List<String> pStringParams, List<String> pStringValue, List<String> pFileParams, List<File> pFileValue, String pMediaType) throws ParameterNotIsNullOrEmpty, HttpPostKeyAndValueNumberDiscrepancy{
        //检查参数是否为空
        if (null == pStringParams || null == pStringValue || null == pFileParams || null == pFileValue || TextUtils.isEmpty(pMediaType)) {
            throw new ParameterNotIsNullOrEmpty();
        }
        //检查key和values个数是否相同
        if ((pStringParams.size() != pStringValue.size()) || (pFileParams.size() != pFileValue.size())) {
            throw new HttpPostKeyAndValueNumberDiscrepancy();
        }
        //构建请求表单
        MultipartBuilder multipartBuilder = new MultipartBuilder();
        multipartBuilder.type(MultipartBuilder.FORM);
        for(byte i = 0; i < pStringParams.size(); i++){
            multipartBuilder.addFormDataPart(pStringParams.get(i), pStringValue.get(i));
        }
        for(byte i = 0; i < pFileParams.size(); i++){
            multipartBuilder.addFormDataPart(pFileParams.get(i), pFileValue.get(i).getName(), RequestBody.create(MediaType.parse(pMediaType), pFileValue.get(i)));
        }
        return multipartBuilder.build();
    }

    /**
     * 构造一个与具体请求方式无关的请求体
     *
     * @param requestAddress   请求地址
     * @param requestParameter 请求参数
     * @return 一个与具体请求方式无关的请求体
     * @throws Exception 当服务器地址、请求参数为Null或空字符串时抛出异常
     */
    private final RequestBody buildRequestBody(String requestAddress, String requestParameter) throws Exception{
        // 服务器地址是否为空
        if (TextUtils.isEmpty(requestAddress)) {
            throw new Exception("服务器地址不可以为空");
        }
        // 请求参数是否为空
        if (TextUtils.isEmpty(requestParameter)) {
            throw new Exception("请求参数不可以为空");
        }
        // 构造请求并完成请求
        return RequestBody.create(JSON, requestParameter);
    }

    @Override
    public String deleteRequest(String requestAddress, String requestParameter) throws Exception{
        try {
            return requestResponseBody(buildDeleteRequest(requestAddress, requestParameter)).string();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * 构造一个delete请求
     *
     * @param requestAddress   请求服务器地址
     * @param requestParameter 请求参数构成的Json字符串
     * @return 一个Post请求
     * @throws Exception 当服务器地址、请求参数为Null或空字符串时抛出异常
     */
    private final Request buildDeleteRequest(String requestAddress, String requestParameter) throws Exception{
        try {
            return new Request.Builder().url(requestAddress).delete(buildRequestBody(requestAddress, requestParameter)).build();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public String postSubmitStringAndFile(String requestAddress, List<String> pStringParams, List<String> pStringValue, List<String> pFileParams, List<File> pFileValue, String pMediaType) throws HttpPostKeyAndValueNumberDiscrepancy, ParameterNotIsNullOrEmpty, Exception{
        try {
            return requestResponseBody(buildPostRequest(requestAddress, pStringParams, pStringValue, pFileParams, pFileValue, pMediaType)).toString();
        } catch (ParameterNotIsNullOrEmpty parameterNotIsNullOrEmpty) {
            throw new ParameterNotIsNullOrEmpty(parameterNotIsNullOrEmpty);
        } catch (HttpPostKeyAndValueNumberDiscrepancy httpPostKeyAndValueNumberDiscrepancy) {
            throw new HttpPostKeyAndValueNumberDiscrepancy(httpPostKeyAndValueNumberDiscrepancy);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @Override
    public String postRequest(String requestAddress, String requestParameter) throws Exception{
        try {
            return requestResponseBody(buildPostRequest(requestAddress, requestParameter)).toString();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * 构造一个Post请求
     *
     * @param requestAddress   请求服务器地址
     * @param requestParameter 请求参数构成的Json字符串
     * @return 一个Post请求
     * @throws Exception 当服务器地址、请求参数为Null或空字符串时抛出异常
     */
    private final Request buildPostRequest(String requestAddress, String requestParameter) throws Exception{
        try {
            return new Request.Builder().url(requestAddress).post(buildRequestBody(requestAddress, requestParameter)).build();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * @param requestAddress 请求服务器地址
     * @param pStringParams  文本参数列表
     * @param pStringValue   文本值列表
     * @param pFileParams    文件参数列表
     * @param pFileValue     文件列表
     * @param pMediaType     文件媒体类型
     * @return 一个Post请求
     * @throws ParameterNotIsNullOrEmpty            当参数为null或空字符串时抛出异常
     * @throws HttpPostKeyAndValueNumberDiscrepancy 当参数列表和值列表的数量不一致时抛出异常
     */
    private final Request buildPostRequest(String requestAddress, List<String> pStringParams, List<String> pStringValue, List<String> pFileParams, List<File> pFileValue, String pMediaType) throws ParameterNotIsNullOrEmpty, HttpPostKeyAndValueNumberDiscrepancy{
        try {
            return new Request.Builder().url(requestAddress).post(buildRequestBody(pStringParams, pStringValue, pFileParams, pFileValue, pMediaType)).build();
        } catch (ParameterNotIsNullOrEmpty parameterNotIsNullOrEmpty) {
            throw new ParameterNotIsNullOrEmpty(parameterNotIsNullOrEmpty.getMessage());
        } catch (HttpPostKeyAndValueNumberDiscrepancy httpPostKeyAndValueNumberDiscrepancy) {
            throw new HttpPostKeyAndValueNumberDiscrepancy(httpPostKeyAndValueNumberDiscrepancy.getMessage());
        }
    }

    @Override
    public File getImage(String imageLocalDir, String imageName, String imageServerAddress, int width) throws Exception{
        // 服务器地址图片本地存放路径否为空
        if (TextUtils.isEmpty(imageLocalDir)) {
            throw new Exception("图片本地存放路径不可以为空");
        }
        // 图片文件名是否为空
        if (TextUtils.isEmpty(imageName)) {
            throw new Exception("图片名不可以为空");
        }
        // 宽度或高度是否小于等于0
        if (0 >= width) {
            throw new Exception("图片宽度不能小于或等于0");
        }
        // 图片服务器地址是否为空
        if (TextUtils.isEmpty(imageServerAddress)) {
            throw new Exception("图片服务器地址不可以为空");
        }
        // 图片文件是否已经存在
        File imageFile = new File(imageLocalDir + File.separator + imageName);
        if (imageFile.exists()) {
            throw new Exception("图片文件已存在");
        }
        // 拼接请求地址
        StringBuilder requestAddress = new StringBuilder();
        requestAddress.append(imageServerAddress);
        // 因图片服务器不支持识别扩展名在此需要把扩展名截掉
        requestAddress.append(imageName.substring(0, imageName.lastIndexOf('.')));
        // 拼接请求参数
        StringBuilder requestParameter = new StringBuilder();
        requestParameter.append("w=");
        requestParameter.append(width);
        requestImageSaveLoacl(requestAddress, requestParameter, imageFile);
        return imageFile;
    }

    @Override
    public String getRequest(String requestAddress, String requestParameter) throws Exception{
        try {
            return getRequestResponseBody(requestAddress, requestParameter).string();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private final ResponseBody getRequestResponseBody(String requestAddress, String requestParameter) throws Exception{
        // 服务器地址是否为空
        if (TextUtils.isEmpty(requestAddress)) {
            throw new Exception("服务器地址不可以为空");
        }
        // 构造Get请求地址
        StringBuilder url = new StringBuilder();
        url.append(requestAddress);
        //当参数不为空时
        if (null != requestParameter) {
            url.append("?");
            url.append(requestParameter);
        }
        // 构造请求并完成请求
        Request request = new Request.Builder().url(url.toString()).build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            throw new Exception(e);
        }
        //判断Http请求结果是否为200，当200时正常返回请求体，否则抛出异常
        if (HttpURLConnection.HTTP_OK == response.code()) {
            //为200时返回请求返回的ResponseBody
            return response.body();
        } else {
            throw new Exception("Http请求返回码为：" + response.code());
        }
    }

    @Override
    public Bitmap getRequestBitMap(String requestAddress, String requestParameter) throws Exception{
        ResponseBody responseBody = null;

        try {
            responseBody = getRequestResponseBody(requestAddress, requestParameter);

        } catch (Exception e) {
            throw new Exception(e);
        }
        return BitmapFactory.decodeStream(responseBody.byteStream());
    }

    @Override
    public String postWayUploadFile(String tfsAddress, String filePath) throws Exception{
        // 待传送的文件对象
        File file = new File(filePath);
        //        try {
        //            new com.zhubauser.mf.android_public_kernel_realize.file.File().uploadFileCheck(file);
        //        } catch (Exception e) {
        //            throw new Exception(e);
        //        }
        StringBuilder serverAddress = new StringBuilder();
        serverAddress.append(tfsAddress);
        // tfs系统请求参数固有部分，仅需要tfs最基本的文件存储功能，因此不需要appkey
        serverAddress.append("/v1/tfs?");
        // tfs参数-文件扩展名
        serverAddress.append("suffix=.");
        //        try {
        //            serverAddress.append(new com.zhubauser.mf.android_public_kernel_realize.file.File().getFileExpandedName(filePath));
        //        } catch (Exception e) {
        //            throw new Exception(e);
        //        }
        // tfs参数-必须使用扩展名访问上传的文件
        serverAddress.append("&simple_name=1");
        // tfs参数-不存成大文件
        serverAddress.append("&large_file=0");
        RequestBody requestBody = RequestBody.create(UNKNOWN_MEDIA_TYPE, file);
        Request request = new Request.Builder().url(serverAddress.toString()).post(requestBody).build();
        //noinspection UnusedAssignment
        serverAddress = null;
        //noinspection UnusedAssignment
        requestBody = null;
        //执行请求
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            throw new Exception(e);
        }
        //noinspection UnusedAssignment
        file = null;
        //noinspection UnusedAssignment
        request = null;
        //判断Http请求结果是否为200，当200时正常返回请求体，否则抛出异常
        if (HttpURLConnection.HTTP_OK == response.code()) {
            //为200时返回由请求结果
            return response.body().string();
        } else {
            throw new Exception("Http请求返回码为：" + response.code());
        }
    }

    /**
     * 设置超时参数
     *
     * @param okHttpClient OkHttpClient实体
     */
    private final void setTimeOut(OkHttpClient okHttpClient){
        //因设置任何非0超时数后，均会超时提醒。所以改成0。今后有机会在尝试修改为非零0
        okHttpClient.setConnectTimeout(0, TimeUnit.MICROSECONDS);
        okHttpClient.setWriteTimeout(0, TimeUnit.MICROSECONDS);
        okHttpClient.setReadTimeout(0, TimeUnit.MICROSECONDS);
    }

    /**
     * 设置超时参数
     *
     * @param httpURLConnection HttpURLConnection实体
     */
    private final void setTimeOut(HttpURLConnection httpURLConnection){
        httpURLConnection.setConnectTimeout(TIME_OUT);
        httpURLConnection.setReadTimeout(TIME_OUT);
    }

    /**
     * 设置Post请求参数：连接和读取超时时限、开启输入（出）流
     *
     * @param httpURLConnection http连接
     */
    private final void setPostRequestProperty(HttpURLConnection httpURLConnection) throws Exception{
        try {
            httpURLConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            throw new Exception(e);
        }
        httpURLConnection.setDoOutput(true);
    }

    @Override
    public String postWaySubmitImage(String imageServerAddress, String filePath) throws Exception{
        // 待传送的文件对象
        File file = new File(filePath);
        //        try {
        //            new com.zhubauser.mf.android_public_kernel_realize.file.File().uploadFileCheck(file);
        //        } catch (Exception e) {
        //            throw new Exception(e);
        //        }
        // 构造请求
        URL url = null;
        try {
            url = new URL(imageServerAddress);
        } catch (MalformedURLException e) {
            throw new Exception(e);
        }
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new Exception(e);
        }
        setTimeOut(httpURLConnection);
        // 设置请求参数
        setPostRequestProperty(httpURLConnection);
        httpURLConnection.setRequestProperty("Content-Type", filePath.substring(filePath.lastIndexOf('.') + 1));
        // 获取数据输出流
        OutputStream outputStream = null;
        try {
            outputStream = httpURLConnection.getOutputStream();
        } catch (IOException e) {
            throw new Exception(e);
        }
        // 获取文件输入流
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new Exception(e);
        }
        try {
            readInAndWriteOutData(inputStream, outputStream);
        } catch (Exception e) {
            throw new Exception(e);
        }
        return readTextRequestResult(httpURLConnection);
    }

    @Override
    public File getImage(String imageLocalDir, String imageName, String imageServerAddress, int width, int height) throws Exception{
        // 服务器地址图片本地存放路径否为空
        if (TextUtils.isEmpty(imageLocalDir)) {
            throw new Exception("图片本地存放路径不可以为空");
        }
        // 图片文件名是否为空
        if (TextUtils.isEmpty(imageName)) {
            throw new Exception("图片名不可以为空");
        }
        // 宽度或高度是否小于等于0
        if (0 >= width) {
            throw new Exception("图片宽度不能小于或等于0");
        }
        if (0 >= height) {
            throw new Exception("图片高度不能小于或等于0");
        }
        // 图片服务器地址是否为空
        if (TextUtils.isEmpty(imageServerAddress)) {
            throw new Exception("图片服务器地址不可以为空");
        }
        // 图片文件是否已经存在
        File imageFile = new File(imageLocalDir + File.separator + imageName);
        if (imageFile.exists()) {
            throw new Exception("图片文件已存在");
        }
        // 拼接请求地址
        StringBuilder requestAddress = new StringBuilder();
        requestAddress.append(imageServerAddress);
        // 因图片服务器不支持识别扩展名在此需要把扩展名截掉
        requestAddress.append(imageName.substring(0, imageName.lastIndexOf('.')));
        // 拼接请求参数
        StringBuilder requestParameter = new StringBuilder();
        requestParameter.append("w=");
        requestParameter.append(width);
        requestParameter.append("&");
        requestParameter.append("h=");
        requestParameter.append(height);
        requestImageSaveLoacl(requestAddress, requestParameter, imageFile);
        return imageFile;
    }

    @Override
    public File getOriginalImage(String imageLocalDir, String imageName, String imageServerAddress) throws Exception{
        // 服务器地址图片本地存放路径否为空
        if (TextUtils.isEmpty(imageLocalDir)) {
            throw new Exception("图片本地存放路径不可以为空");
        }
        // 图片文件名是否为空
        if (TextUtils.isEmpty(imageName)) {
            throw new Exception("图片名不可以为空");
        }
        // 图片文件是否已经存在
        File imageFile = new File(imageLocalDir + imageName);
        if (imageFile.exists()) {
            throw new Exception("图片文件已存在");
        }
        // 图片服务器地址是否为空
        if (TextUtils.isEmpty(imageServerAddress)) {
            throw new Exception("图片服务器地址不可以为空");
        }
        // 拼接请求地址
        StringBuilder requestAddress = new StringBuilder();
        requestAddress.append(imageServerAddress);
        // 因图片服务器不支持识别扩展名在此需要把扩展名截掉
        requestAddress.append(imageName.substring(0, imageName.lastIndexOf('.')));
        // 拼接请求参数
        StringBuilder requestParameter = new StringBuilder();
        requestParameter.append("q=");
        requestParameter.append(ORIGINAL_IMPAGE_ZOOM_RATIO);
        requestImageSaveLoacl(requestAddress, requestParameter, imageFile);
        return imageFile;
    }

    @Override
    public void requestImageSaveLoacl(StringBuilder requestAddress, StringBuilder requestParameter, File imageFile) throws Exception{
        // 发送请求
        ResponseBody responseBody = null;
        try {
            responseBody = getRequestResponseBody(requestAddress.toString(), requestParameter.toString());
        } catch (Exception e) {
            throw new Exception(e);
        }
        //noinspection UnusedAssignment
        requestAddress = null;
        //noinspection UnusedAssignment
        requestParameter = null;
        // 读取并保存文件
        InputStream inputStream = responseBody.byteStream();
        //noinspection UnusedAssignment
        responseBody = null;
        if (!imageFile.getParentFile().mkdirs()) {
            throw new Exception("创建目录失败");
        }
        try {
            if (!imageFile.createNewFile()) {
                throw new Exception("创建文件失败");
            }
        } catch (IOException e) {
            throw new Exception(e);
        }
        FileOutputStream imageFileOutputStream = null;
        try {
            imageFileOutputStream = new FileOutputStream(imageFile);
        } catch (FileNotFoundException e) {
            throw new Exception(e);
        }
        // 缓冲区
        byte[] dataBuffer = new byte[BUFFER_SIZE];
        // 每次读取字节数
        int readCount = 0;
        try {
            while (-1 != (readCount = inputStream.read(dataBuffer))) {
                imageFileOutputStream.write(dataBuffer, 0, readCount);
            }
        } catch (IOException e) {
            throw new Exception(e);
        }
        // 释放内存
        try {
            imageFileOutputStream.close();
        } catch (IOException e) {
            throw new Exception(e);
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new Exception(e);
        }
        //noinspection UnusedAssignment
        dataBuffer = null;
    }

    @Override
    public void readInAndWriteOutData(InputStream inputStream, OutputStream outputStream) throws Exception{
        if (null == inputStream) {
            throw new Exception("输入流不可以为空");
        }
        if (null == outputStream) {
            throw new Exception("输出流不可以为空");
        }
        // 读取缓冲区
        byte[] buffer = new byte[BUFFER_SIZE];
        // 每次读取量统计
        int count = 0;
        // 读取并写入数据
        // 输入(出)流是否为空
        try {
            while (-1 != (count = inputStream.read(buffer))) {
                outputStream.write(buffer, 0, count);
            }
        } catch (IOException e) {
            throw new Exception(e);
        }
        // 释放文件输入流
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new Exception(e);
        }
        //noinspection UnusedAssignment,UnusedAssignment
        inputStream = null;

        try {
            outputStream.flush();
        } catch (IOException e) {
            throw new Exception(e);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new Exception(e);
        }
        //noinspection UnusedAssignment
        outputStream = null;
        // 释放缓冲区
        //noinspection UnusedAssignment
        buffer = null;
    }

    @Override
    public String readTextRequestResult(HttpURLConnection httpURLConnection) throws Exception{
        if (null == httpURLConnection) {
            throw new Exception("Http连接不可以为空");
        }
        //判断连接状态码，非200时抛出异常
        try {
            if (HttpURLConnection.HTTP_OK != httpURLConnection.getResponseCode()) {
                throw new Exception("Http请求返回码为：" + httpURLConnection.getResponseCode());
            }
        } catch (Exception e) {
            throw new Exception(e);
        }

        StringBuilder responseStringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;
        // 构造读取缓冲区
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        } catch (IOException e) {
            throw new Exception(e);
        }
        // 返回内容缓冲
        String responseBuffer = null;
        // 读取返回内容
        try {
            while ((responseBuffer = bufferedReader.readLine()) != null) {
                responseStringBuffer.append(responseBuffer).append("\n");
            }
        } catch (IOException e) {
            throw new Exception(e);
        }
        // 释放读取缓冲区
        try {
            bufferedReader.close();
        } catch (IOException e) {
            throw new Exception(e);
        } finally {
            //noinspection UnusedAssignment
            bufferedReader = null;
        }

        return responseStringBuffer.toString();
    }

    /**
     * OkHttpClient实体
     */
    private final OkHttpClient okHttpClient;
    /**
     * 单例模式实例
     */
    private static HttpRequest httpRequese;
    /**
     * 原始图片缩放比例
     */
    private static final int ORIGINAL_IMPAGE_ZOOM_RATIO = 100;
    /**
     * 网络连接超时时间：60秒
     */
    private static final int TIME_OUT = 10 * 6 * 1000;
    /**
     * 缓冲区大小50KB
     */
    private static final int BUFFER_SIZE = 1024 * 50;
    /**
     * 媒体类型-JSON
     */
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    /**
     * 媒体类型-未知。因TFS系统上传文件无需指定类型，但okhttp必须指定，因此用此类型进行适配。
     */
    private static final MediaType UNKNOWN_MEDIA_TYPE = MediaType.parse("application/octet-stream");
}
