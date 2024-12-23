package priv.cgroup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priv.cgroup.repository.FileRepository;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class DeleteFile {
    @Autowired
    private final FileRepository fileRepository;


    public DeleteFile(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    //异常
    public Map<String, Object> deleteFile(Map<String, Object> requestBody) {
        Map<String, Object> response = new HashMap<>();

        try{
            String path = requestBody.get("path").toString();
            String type = requestBody.get("type").toString();
            String name = requestBody.get("name").toString();

            String concatPath = "/home/kncsz/SysMaster/file/user" + path + "/" + name;

            if(type.equals("file")){
                File file = new File(concatPath);
                if(file.exists()){
                    if(file.delete()){
                        String parentDir = file.getParent();
                        fileRepository.deleteByPathAndName(parentDir, name);
                        response.put("status", 200);
                        response.put("message", "success");
                    }
                }else{
                    response.put("status", 404);
                    response.put("message", "file not exist");
                }
            }else if(type.equals("directory")){
                File file = new File(concatPath);
                if(file.exists()){
                    if(file.delete()){
                        // 递归删除
                        deleteDirectoryRecursively(file);
                        // 在数据库中执行两次删除,第一次删除目录条目,第二次删除目录下所有子文件,子目录的条目;
                        String parentDirectoryPath = "/home/kncsz/SysMaster/file/user" + path;
                        fileRepository.deleteByPathAndNameAndType(parentDirectoryPath, name, "directory");
                        fileRepository.deleteByPathPrefix(concatPath);

                        response.put("status", 200);
                        response.put("message", "success");
                    }
                }else{
                    response.put("status", 404);
                    response.put("message", "directory not exist");
                }
            }
        }catch(Exception e){
            response.put("status", 500);
            response.put("message", "Bad Request");
        }
        return response;
    }

    // 递归删除目录及其子文件和子目录
    private boolean deleteDirectoryRecursively(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    // 递归删除子文件和子目录
                    if (!deleteDirectoryRecursively(file)) {
                        return false; // 如果有一个文件或目录未成功删除，则返回 false
                    }
                }
            }
        }
        // 删除空目录或文件
        return dir.delete();
    }
}
