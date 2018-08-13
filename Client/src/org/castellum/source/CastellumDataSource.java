package org.castellum.source;

import java.security.PublicKey;

public class CastellumDataSource {

    private String username, password;

    private PublicKey publicKey;

    private String host;

    private int port;


    public String getUsername() {
        return username;
    }

    private CastellumDataSource() {

    }

    public static CastellumDataSource build() {
        return new CastellumDataSource();
    }

    public CastellumDataSource setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public CastellumDataSource setPassword(String password) {
        this.password = password;
        return this;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public CastellumDataSource setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public String getHost() {
        return host;
    }

    public CastellumDataSource setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public CastellumDataSource setPort(int port) {
        this.port = port;
        return this;
    }

}
