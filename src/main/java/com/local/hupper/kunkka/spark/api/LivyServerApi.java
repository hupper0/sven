package com.local.hupper.kunkka.spark.api;

import com.local.hupper.kunkka.ClientConf;
import com.local.hupper.kunkka.spark.model.*;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by lvhongpeng on 2017/9/4.
 */
public class LivyServerApi {

    private static Logger logger = LoggerFactory.getLogger(LivyServerApi.class);
    public static String LIVY_URI = "http://" + ClientConf.LIVY_URL + ":" + ClientConf.LIVY_PORT;

    /**
     * createHttp
     *
     * @param uri
     * @param type
     * @return HttpUtil
     */
    private static HttpUtil createHttp(String uri, String type) {
        HttpUtil http;
        switch (type) {
            case "GET":
                http = HttpUtil.get(uri);
                break;
            case "POST":
                http = HttpUtil.post(uri);
                break;
            case "PUT":
                http = HttpUtil.put(uri);
                break;
            case "DELETE":
                http = HttpUtil.delete(uri);
                break;
            default:
                http = HttpUtil.get(uri);
        }
        http.addHeader("Content-type", "application/json; charset=utf-8");
        http.addHeader("Accept", "application/json");
        http.setContentType("application/json", Charset.forName("UTF-8"));
        return http;
    }


    /**
     * Creates a new interactive Scala, Python, or R shell in the cluster.
     *
     * @return Session
     */
    public static Session createInteractiveShell(InteractiveRequest postRequest) {
        String uri = LIVY_URI + "/sessions";
        HttpUtil http = createHttp(uri, "POST");
        JSONObject jsonObject = JSONObject.fromObject(postRequest);
        http.setParameter(jsonObject.toString());
        ResponseWrap response = http.execute();
        JSONObject responseJson = JSONObject.fromObject(response.getString());
        return (Session) JSONObject.toBean(responseJson, Session.class);
    }


    /**
     * Returns the session information.
     *
     * @param sessionId
     * @return
     */
    public static Session getSessionInfo(String sessionId) {
        String uri = LIVY_URI + "/sessions/" + sessionId;
        HttpUtil http = createHttp(uri, "GET");
        ResponseWrap response = http.execute();
        JSONObject responseJson = JSONObject.fromObject(response.getString());
        return (Session) JSONObject.toBean(responseJson, Session.class);
    }


    /**
     * Kills the Session job.
     *
     * @param sessionId
     */
    public static void killSession(String sessionId) {
        String uri = LIVY_URI + "/sessions/" + sessionId;
        HttpUtil http = createHttp(uri, "DELETE");
        ResponseWrap response = http.execute();
        logger.info("killSession sessionId:{}, response:{}", sessionId, response.getString());
    }

    /**
     * Gets the log lines from this session.
     *
     * @param sessionId
     * @return
     */
    public static List<String> getSessionLog(String sessionId) {
        String uri = LIVY_URI + "/sessions/" + sessionId + "/log";
        HttpUtil http = createHttp(uri, "GET");
        ResponseWrap response = http.execute();
        JSONObject responseJson = JSONObject.fromObject(response.getString());
        return (List<String>) responseJson.get("log");
    }

    /**
     * Returns all the statements in a session.
     *
     * @param sessionId
     * @return
     */
    public static List<Statement> getSessionStataements(String sessionId) {
        String uri = LIVY_URI + "/sessions/" + sessionId + "/statements";
        HttpUtil http = createHttp(uri, "GET");
        ResponseWrap response = http.execute();
        JSONObject responseJson = JSONObject.fromObject(response.getString());
        return (List<Statement>) responseJson.get("statements");
    }


    /**
     * Runs a statement in a session.
     *
     * @param sessionId
     * @return
     */
    public static Statement postStatementToSession(String sessionId, String code) {
        String uri = LIVY_URI + "/sessions/" + sessionId + "/statements";
        HttpUtil http = createHttp(uri, "POST");
        http.addParameter("code", code);
        ResponseWrap response = http.execute();
        JSONObject responseJson = JSONObject.fromObject(response.getString());
        return (Statement) JSONObject.toBean(responseJson, Statement.class);
    }


    /**
     * Returns all the active batch sessions.
     *
     * @return
     */
    public static List<Session> getbatchesSession() {
        String uri = LIVY_URI + "/batches";
        HttpUtil http = createHttp(uri, "GET");
        ResponseWrap response = http.execute();
        JSONObject responseJson = JSONObject.fromObject(response.getString());
        return (List<Session>) responseJson.get("sessions");
    }


    /**
     * postSession .
     *
     * @return
     */
    public static Batch postSession(PostBathRequest postRequest) {
        String uri = LIVY_URI + "/batches";
        HttpUtil http = createHttp(uri, "POST");
        JSONObject jsonObject = postRequest.getJSONObject();
        http.setParameter(jsonObject.toString());
        logger.info("postRequest:{} ", jsonObject);
        ResponseWrap response = http.execute();
        JSONObject responseJson = JSONObject.fromObject(response.getString());
        return (Batch) JSONObject.toBean(responseJson, Batch.class);
    }

    /**
     * getBathState .
     *
     * @return
     */
    public static String getBathState(String bathId) {
        String uri = LIVY_URI + "/batches/" + bathId + "/state";
        HttpUtil http = createHttp(uri, "GET");
        ResponseWrap response = http.execute();
        JSONObject responseJson = JSONObject.fromObject(response.getString());
        return String.valueOf(responseJson.get("state"));
    }


    /**
     * Returns the Batch session information.
     *
     * @param batchId
     * @return
     */
    public static Batch getBatchSessionInfo(String batchId) {
        String uri = LIVY_URI + "/batches/" + batchId;
        HttpUtil http = createHttp(uri, "GET");
        ResponseWrap response = http.execute();
        JSONObject responseJson = JSONObject.fromObject(response.getString());
        return (Batch) JSONObject.toBean(responseJson, Batch.class);
    }

    /**
     * Kills the Batch job.
     *
     * @param batchId
     */
    public static void killBashJob(String batchId) {
        String uri = LIVY_URI + "/batches/" + batchId;
        HttpUtil http = createHttp(uri, "DELETE");
        ResponseWrap response = http.execute();
        logger.info("killBashJob sessionId:{}, response:{}", batchId, response.getString());
    }

    /**
     * Gets the log lines from this batch.
     *
     * @param batchId
     */
    public static List<String> getBashJobLog(String batchId, int from, int size) {
        String uri = LIVY_URI + "/batches/" + batchId + "/log";
        HttpUtil http = createHttp(uri, "GET");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", from);
        jsonObject.put("size", size);
        http.setParameter(jsonObject.toString());
        ResponseWrap response = http.execute();
        JSONObject responseJson = JSONObject.fromObject(response.getString());
        return (List<String>) responseJson.get("log");
    }
}
