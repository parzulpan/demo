package cn.parzulpan.web.controller;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 文件上传的控制器
 */

@Controller
@RequestMapping("/file")
public class FileUploadController {

    /**
     * 文件上传回顾
     * @return
     */
    @RequestMapping(path = {"/upload1"})
    public String upload1(HttpServletRequest request) throws Exception {
        System.out.println("called upload1...");

        String path = request.getSession().getServletContext().getRealPath("/uploads"); // 获取到要上传的文件目录
        System.out.println(path);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();    // 创建磁盘文件项工厂
        ServletFileUpload fileUpload = new ServletFileUpload(factory);
        List<FileItem> fileItems = fileUpload.parseRequest(request);    // 解析request对象
        for (FileItem fileItem : fileItems) {
            if (fileItem.isFormField()) {   // 判断文件项是普通字段，还是上传的文件
                System.out.println(fileItem.getName());
            } else {
                String itemName = fileItem.getName();   // 获取到上传文件的名称
                itemName = UUID.randomUUID().toString() + "-" + itemName;   // 把文件名唯一化
                fileItem.write(new File(file, itemName));   // 上传文件
                fileItem.delete();
            }
        }

        return "success";
    }

    /**
     * SpringMVC 传统方式的文件上传
     * @return
     */
    @RequestMapping(path = {"/upload2"})
    public String upload2(HttpServletRequest request, String picName, MultipartFile picFile) throws Exception {
        System.out.println("called upload2...");

        String path = request.getSession().getServletContext().getRealPath("/uploads"); // 获取到要上传的文件目录
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = picName + "-" + picFile.getOriginalFilename();
        fileName = UUID.randomUUID().toString() + "-" + fileName;
        picFile.transferTo(new File(file, fileName));   // 上传文件

        return "success";
    }

    /**
     * SpringMVC 跨服务器方式的文件上传
     * @return
     */
    @RequestMapping(path = {"/upload3"})
    public String upload3( String picName, MultipartFile picFile) throws Exception {
        System.out.println("called upload3...");

        String path = "http://localhost:9090/file-server/uploads/";  // 定义上传文件服务器路径

        String fileName = picName + "-" + picFile.getOriginalFilename();
        fileName = UUID.randomUUID().toString() + "-" + fileName;

        // 1. 创建客户端对象
        Client client = Client.create();

        // 2. 和文件服务器进行连接
        WebResource resource = client.resource(path + fileName);

        // 3. 上传文件，跨服务器的
        resource.put(picFile.getBytes());

        return "success";
    }
}
