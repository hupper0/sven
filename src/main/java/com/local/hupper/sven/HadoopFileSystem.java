package com.local.hupper.sven;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.util.ToolRunner;

/**
 * @author lhp@local.com
 * @date 2018/5/11 下午1:16
 */
public class HadoopFileSystem extends FsShell{


    /**
     * 执行入口函数
     * @param argv the command and its arguments
     * @throws Exception upon error
     */
    public static void main(String argv[]) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "hadoopuser");
        FsShell shell = newShellInstance();
        Configuration conf = new Configuration();
        conf.setQuietMode(false);
        shell.setConf(conf);
        int res;
        try {
            res = ToolRunner.run(shell, parseArgs(argv));
        } finally {
            shell.close();
        }
        System.exit(res);
    }


    /**
     * 去除掉第一个参数
     * @param argv
     * @return
     */
    private static String[] parseArgs(String argv[]){
        String[] strings = new String[argv.length-1];
        System.arraycopy(argv, 1, strings, 0, strings.length);
        return strings;
    }


}
