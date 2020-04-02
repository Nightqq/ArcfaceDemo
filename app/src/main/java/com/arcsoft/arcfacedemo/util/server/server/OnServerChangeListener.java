package com.arcsoft.arcfacedemo.util.server.server;

public interface OnServerChangeListener {

    void onServerStarted(String ipAddress);

    void onServerStopped();

    void onServerError(String errorMessage);

}
