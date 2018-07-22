package com.local.hupper.kunkka.hdfs;

import com.local.hupper.kunkka.ClientConf;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lvhongpeng on 2017/9/4.
 */
public class HDFSFile {
    private static final Logger logger = LoggerFactory.getLogger(HDFSFile.class);

    private Configuration configuration = null;
    private FileSystem fileSystem = null;
    private DistributedFileSystem hdfs = null;
    private static String uri = "hdfs://" + ClientConf.HDFS_URI;

    static {
        System.setProperty("HADOOP_USER_NAME", ClientConf.HADOOP_USER_NAME);
    }

    /**
     * @throws Exception
     */
    public HDFSFile() throws Exception {
        logger.info("HDFSFile uri:{}", uri);
        configuration = new Configuration();
        fileSystem = FileSystem.get(URI.create(uri), configuration, ClientConf.HADOOP_USER_NAME);
        hdfs = (DistributedFileSystem) fileSystem;
    }

    /**
     * moveFromHdfsToLocal
     *
     * @param hdfsSrcPath  hdfsSrcPath
     * @param dstLocalPath dstLocalPath
     * @throws IOException IOException
     */
    public void moveFromHdfsToLocal(String hdfsSrcPath, String dstLocalPath, PrintWriter printWriter) throws IOException {
        printWriter.print("move from HDFS to local (delete the HDFS file): {"+hdfsSrcPath+"} -> {"+dstLocalPath+"}");
        Path srcPath = new Path(hdfsSrcPath);
        Path dstPath = new Path(dstLocalPath);
        hdfs.copyToLocalFile(true, srcPath, dstPath);
        File dstFile = new File(dstLocalPath);
        String dstFolder = dstFile.getParent();
        String crcFilePath = dstFolder + "/." + dstFile.getName() + ".crc";
        File crcFile = new File(crcFilePath);
        crcFile.deleteOnExit();
    }

    public void copyFromHdfsToLocal(String hdfsSrcPath, String dstLocalPath, PrintWriter printWriter) throws IOException {
        printWriter.print("copy from HDFS to local (delete the HDFS file): {"+hdfsSrcPath+"} -> {"+dstLocalPath+"}");
        Path srcPath = new Path(hdfsSrcPath);
        Path dstPath = new Path(dstLocalPath);
        hdfs.copyToLocalFile(false, srcPath, dstPath);
    }
    /**
     * deleteFile
     *
     * @param dstLocalPath dstLocalPath
     * @throws IOException IOException
     */
    public void deleteFile(String dstLocalPath) throws IOException {
        logger.info("deleteFile file  : {} ", dstLocalPath);
        Path srcPath = new Path(dstLocalPath);
        hdfs.delete(srcPath, true);
    }

    /**
     * putFromLocalToHdfs
     *
     * @param localSrcPath localSrcPath
     * @param dstHdfsPath  dstHdfsPath
     * @throws IOException IOException
     */
    public void putFromLocalToHdfs(String localSrcPath, String dstHdfsPath) throws IOException {
        logger.info("put local file to HDFS: {} -> {}", localSrcPath, dstHdfsPath);
        Path srcPath = new Path(localSrcPath);
        Path dstPath = new Path(dstHdfsPath);
        hdfs.copyFromLocalFile(false, srcPath, dstPath);
    }


    public void putFromHdfsToHdfs(String localSrcPath, String dstHdfsPath) throws Exception {
        logger.info("put HDFS file to HDFS: {} -> {}", localSrcPath, dstHdfsPath);
        Path srcPath = new Path(localSrcPath);
        Path dstPath = new Path(dstHdfsPath);
        FileUtil.copy(fileSystem, srcPath, fileSystem, dstPath, false, configuration);
    }


    public void rename(String localSrcPath, String dstHdfsPath) throws IOException {
        logger.info("rename hdfs file to HDFS: {} -> {}", localSrcPath, dstHdfsPath);
        Path srcPath = new Path(localSrcPath);
        Path dstPath = new Path(dstHdfsPath);
        hdfs.rename(srcPath, dstPath);
    }

    /**
     * mkdirs
     *
     * @param hdfsPath hdfsPath
     * @throws IOException IOException
     */
    public void mkdirs(String hdfsPath) throws IOException {
        logger.info("HDFS mkdirs: {}", hdfsPath);
        Path path = new Path(hdfsPath);
        hdfs.mkdirs(path);
    }


    /**
     * ls
     *
     * @param folder hdfsPath
     * @throws IOException IOException
     */
    public void ls(String folder, PrintWriter printWriter) throws IOException {
        Path path = new Path(folder);
        FileStatus[] list = hdfs.listStatus(path);
        printWriter.println("ls: " + folder);
        printWriter
                .println("==========================================================");
        for (FileStatus f : list) {
            printWriter.printf("name: %s, folder: %s, size: %d\n", f.getPath(),
                    (!f.isFile()), f.getLen());
        }
        printWriter
                .println("==========================================================");
        printWriter.flush();
    }


    /**
     * run list
     *
     * @param hdfsPath hdfsPath
     * @return list files
     * @throws IOException IOException
     */
    public List<String> list(String hdfsPath) throws IOException {
        logger.info("HDFS list: {}", hdfsPath);
        List<String> pathList = new ArrayList();
        Path path = new Path(hdfsPath);
        RemoteIterator<LocatedFileStatus> fileList = hdfs.listFiles(path, false);
        while (fileList.hasNext()) {
            LocatedFileStatus file = fileList.next();
            if (file.isFile()) {
                pathList.add(hdfsPath + "/" + file.getPath().getName());
            }
        }
        return pathList;
    }

    /**
     *检测hdfs spark package dir exist
     */
    public void checkPackagePath(){
        try {
            if(StringUtils.isNotEmpty(ClientConf.APPLICATION_HDFS_DIR)){
                Path path = new Path(ClientConf.APPLICATION_HDFS_DIR);
                if(!hdfs.isDirectory(path)){
                    System.out.println("create hdfs path:" +  ClientConf.APPLICATION_HDFS_DIR);
                    hdfs.mkdirs(path);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    /**
     * read
     *
     * @param hdfsPath hdfsPath
     * @throws IOException IOException
     */
    public void read(String hdfsPath) throws IOException {
        FSDataInputStream fsDataInputStream = hdfs.open(new Path(hdfsPath));
        IOUtils.copyBytes(fsDataInputStream, System.out, 4096, false);
        fsDataInputStream.close();
    }

    /**
     * get
     *
     * @param hdfsPath  hdfsPath
     * @param localFile localFile
     * @throws IOException IOException
     */
    public void get(String hdfsPath, String localFile) throws IOException {
        FSDataInputStream fsDataInputStream = hdfs.open(new Path(hdfsPath));
        IOUtils.copyBytes(fsDataInputStream, new FileOutputStream(localFile), 4096, false);
        fsDataInputStream.close();
    }

    /**
     * putMerge
     *
     * @param localSrcPath localSrcPath
     * @param hdfsPath     dstHdfsPath
     */
//    public void putMerge(String localSrcPath, String hdfsPath) throws IOException {
//        Path inputDir = new Path(localSrcPath);
//        Path hdfsFile = new Path(hdfsPath);
//        FileStatus[] inputFiles = hdfs.listStatus(inputDir);
//        FSDataOutputStream out = hdfs.create(hdfsFile);
//
//        for (FileStatus fileStatus : inputFiles) {
//            System.out.println(fileStatus.getPath().getName());
//            FSDataInputStream in = hdfs.open(fileStatus.getPath());
//            byte buffer[] = new byte[256];
//            int bytesRead;
//            while ((bytesRead = in.read(buffer)) > 0) {
//                out.write(buffer, 0, bytesRead);
//            }
//            in.close();
//        }
//        out.close();
//    }

    /**
     * getMerge
     *
     * @param inputDir  inputDir
     * @param localFile localFile
     * @throws IOException IOException
     */
    public void getMerge(String inputDir, String localFile) throws IOException {
        Path inputDirPath = new Path(inputDir);
        FileStatus[] inputFiles = hdfs.listStatus(inputDirPath);
        for (FileStatus fileStatus : inputFiles) {
            if (!hdfs.isFile(fileStatus.getPath())) {
                getMerge(fileStatus.getPath().toString(), localFile);
                continue;
            }

            System.out.println(fileStatus.getPath().getName());
            FSDataInputStream in = hdfs.open(fileStatus.getPath());
            FileOutputStream fileOutputStream = new FileOutputStream(localFile, true);
            byte buffer[] = new byte[256];
            int bytesRead = 0;
            while ((bytesRead = in.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            in.close();
        }
    }


}
