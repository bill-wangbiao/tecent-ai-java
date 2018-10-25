package org.alking.tecent.ai.impl;

import org.alking.tecent.ai.domain.Resource;
import org.alking.tecent.ai.util.HttpUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

public abstract class BaseClient {

    public static final String SIGN_FIELD_APP_ID = "app_id";

    public static final String SIGN_FIELD_TIME_STAMP = "time_stamp";

    public static final String SIGN_FIELD_NONCE_STR = "nonce_str";

    public static final String SIGN_FIELD_APP_KEY = "app_key";

    public static final String SIGN_FIELD_SIGN = "sign";

    public static final String SIGN_FIELD_IMAGE = "image";

    private final String appId;

    private final String appKey;

    public String getAppId() {
        return appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public BaseClient(String appId, String appKey) {
        this.appId = appId;
        this.appKey = appKey;
    }

    protected abstract String reqURL();

    protected String parseSourceData(final Resource resource) throws IOException {

        if(resource == null){
            throw new IOException("resource is null");
        }

        if(Resource.RES_TYPE_BASE64 == resource.getType()){
            return resource.getUri();
        }

        if(Resource.RES_TYPE_LOCAL == resource.getType()){
            String path = resource.getUri();
            byte[] bytes = FileUtils.readFileToByteArray(new File(path));
            return Base64.getEncoder().encodeToString(bytes);
        }

        if(Resource.RES_TYPE_HTTP == resource.getType()){
            String uri = resource.getUri();
            byte[] bytes = HttpUtil.doGetBytes(uri);
            return Base64.getEncoder().encodeToString(bytes);
        }

        throw new IOException("resource is invalid");
    }

    /**
     * add timestamp and nonce_str automatic
     */
    protected String calcSign(TreeMap<String, String> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        map.put(SIGN_FIELD_APP_ID,this.appId);
        map.put(SIGN_FIELD_APP_KEY, this.appKey);
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        map.put(SIGN_FIELD_TIME_STAMP,timestamp);
        map.put(SIGN_FIELD_NONCE_STR,timestamp);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (SIGN_FIELD_APP_KEY.equals(entry.getKey())) {
                continue;
            }
            if (SIGN_FIELD_IMAGE.equals(entry.getKey())) {
                String base64 = entry.getValue();
                String urlEncode = URLEncoder.encode(base64, "utf-8");
                sb.append(String.format("%s=%s&", entry.getKey(), urlEncode));
                continue;
            }
            sb.append(String.format("%s=%s&", entry.getKey(), entry.getValue()));
        }
        sb.append(String.format("%s=%s", SIGN_FIELD_APP_KEY, this.appKey));

        String sign = DigestUtils.md5Hex(sb.toString()).toUpperCase();
        map.put(SIGN_FIELD_SIGN,sign);
        return sign;
    }
}