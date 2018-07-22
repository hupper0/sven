package com.local.hupper.kunkka.spark.model;

import com.local.hupper.kunkka.spark.annotation.FieldOption;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lvhongpeng on 2017/9/6.
 */
public class PostBathRequest extends Request {
    /**
     * File containing the application to execute(required)
     */
    @FieldOption(opt = "f", longOpt = "file", description = "File containing the application to execute")
    private String file;
    /**
     * Application Java/Spark main class
     */
    @FieldOption(opt = "c", longOpt = "class", description = "Application Java/Spark main class.")
    private String className;
    /**
     * Command line arguments for the application
     */
//    @FieldOption(opt = "a", longOpt = "args", description = "Command line arguments for the application")
    private List<String> args;


    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        if (this == null) return jsonObject;
        List<Field> fieldList0 = new ArrayList();
        List<Field> fieldList1 = Arrays.asList(this.getClass().getDeclaredFields());
        List<Field> fieldList2 = Arrays.asList(this.getClass().getSuperclass().getDeclaredFields());
        fieldList0.addAll(fieldList1);
        fieldList0.addAll(fieldList2);
        for (int j = 0; j < fieldList0.size(); j++) {
            try {
                Field field = fieldList0.get(j);
                field.setAccessible(true);
                if (field.getType().getName().equals(
                        String.class.getName())) {
                    // String type
                    if (field.get(this) != null && StringUtils.isNotEmpty(field.get(this).toString())) {
                        jsonObject.put(field.getName(), field.get(this));
                    }
                } else if (field.getType().getName().equals(
                        Integer.class.getName())
                        || field.getType().getName().equals("int")) {
                    // Integer type
                    if (field.get(this) != null && Integer.parseInt(field.get(this).toString()) > 0) {
                        jsonObject.put(field.getName(), field.get(this));
                    }
                } else if (field.getType().getName().equals(
                        List.class.getName())) {
                    List list = (List) field.get(this);
                    if (field.get(this) != null && list.size() > 0) {
                        jsonObject.put(field.getName(), field.get(this));
                    }
                } else if (field.getType().getName().equals(
                        java.util.Map.class.getName()) || field.getType().getName().equals(
                        java.util.HashMap.class.getName())) {
                    if (field.get(this) != null) {
                        jsonObject.put(field.getName(), field.get(this));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }


    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }
}
