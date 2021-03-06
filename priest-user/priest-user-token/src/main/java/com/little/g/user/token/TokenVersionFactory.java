package com.little.g.user.token;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.little.g.common.enums.TokenVersion;
import jodd.props.Props;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentMap;

/**
 * token版本控制工厂类
 * User: erin
 * Date: 14-10-30
 * Time: 下午5:34
 */
public class TokenVersionFactory {

    private static String TOKEN_VERSION = "tokenVersion";
    private static String RESOURCE_NAME = "token_version.properties";

    private static Props properties = null;

    protected static ConcurrentMap<String, TokenVersionConfig> versionMap = Maps.newConcurrentMap();

    public static TokenVersionConfig getTokenConfig(int version){

        String versionString = TokenVersion.getStringVersion(version);
        TokenVersionConfig oAuthConsumer = versionMap.get(buildVersionKey(versionString));
        if (oAuthConsumer == null) {
            oAuthConsumer = newResource(RESOURCE_NAME, versionString);
            versionMap.putIfAbsent(buildVersionKey(versionString), oAuthConsumer);
        }
        return oAuthConsumer;
    }

    private static synchronized TokenVersionConfig newResource(String resourceName, String versionStr){
        properties = new Props();
        InputStream input = TokenVersionConfig.class.getClassLoader().getResourceAsStream(resourceName);
        try {
            properties.load(input);
        } catch (IOException e) {
            throw new Error(e);
        }
        TokenVersionConfig tokenVersionConfig = new TokenVersionConfig();
        tokenVersionConfig.setVersion(Integer.valueOf(versionStr));
        tokenVersionConfig.setCheck_token_key(getValue("check_token_key", versionStr));
        tokenVersionConfig.setCreate_token_key(getValue("create_token_key", versionStr));
        tokenVersionConfig.setCheck(getValue("check", versionStr));
        return tokenVersionConfig;
    }

    private static String getValue(String name, String provider) {
        name = TOKEN_VERSION + "." + name;
        String url = properties.getValue(name, provider);
        if (Strings.isNullOrEmpty(url)) {
            return null;
        }
        return url;
    }

    private static String buildVersionKey(String versionStr) {
        return versionStr + "_" + RESOURCE_NAME;
    }

}
