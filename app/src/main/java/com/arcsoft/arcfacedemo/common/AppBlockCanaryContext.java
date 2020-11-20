package com.arcsoft.arcfacedemo.common;

import android.content.Context;

public class AppBlockCanaryContext {}/*extends BlockCanaryContext {

    // override to provide context like app qualifier, uid, network type, block threshold, log save path
    // this is default block threshold, you can set it by phone's performance
    @Override
    public int getConfigBlockThreshold() {
        return 1000;
    }
    // if set true, notification will be shown, else only write log file
    @Override
    public boolean isNeedDisplay() {
        return BuildConfig.DEBUG;
    }
    // path to save log file
    @Override
    public String getLogPath() {
        return "/mnt/sdcard/";
    }
   *//* // 实现各种上下文，包括应用标示符，用户uid，网络类型，卡慢判断阙值，Log保存位置等

    /**
     * Implement in your project.
     *
     * @return Qualifier which can specify this installation, like version + flavor.
     *//*
    public String provideQualifier() {
        return "unknown";
    }

    *//**
     * Implement in your project.
     *
     * @return user id
     *//*
    public String provideUid() {
        return "uid";
    }

    *//**
     * Network type
     *
     * @return {@link String} like 2G, 3G, 4G, wifi, etc.
     *//*
    public String provideNetworkType() {
        return "unknown";
    }

    *//**
     * Config monitor duration, after this time BlockCanary will stop, use
     * with {@code BlockCanary}'s isMonitorDurationEnd
     *
     * @return monitor last duration (in hour)
     *//**//*
    public int provideMonitorDuration() {
        return -1;
    }

    /**
     * Config block threshold (in millis), dispatch over this duration is regarded as a BLOCK. You may set it
     * from performance of device.
     *
     * @return threshold in mills
     *//*
    public int provideBlockThreshold() {
        return 1000;
    }

    *//**
     * Thread stack dump interval, use when block happens, BlockCanary will dump on main thread
     * stack according to current sample cycle.
     * <p>
     * Because the implementation mechanism of Looper, real dump interval would be longer than
     * the period specified here (especially when cpu is busier).
     * </p>
     *
     * @return dump interval (in millis)
     *//*
    public int provideDumpInterval() {
        return provideBlockThreshold();
    }

    *//**
     * Path to save log, like "/blockcanary/", will save to sdcard if can.
     *
     * @return path of log files
     *//*
    public String providePath() {
        return "/blockcanary/";
    }

    *//**
     * If need notification to notice block.
     *
     * @return true if need, else if not need.
     *//*
    public boolean displayNotification() {
        return true;
    }

    *//**
     * Implement in your project, bundle files into a zip file.
     *
     * @param src  files before compress
     * @param dest files compressed
     * @return true if compression is successful
     *//**//*
    public boolean zip(File[] src, File dest) {
        return false;
    }

    /**
     * Implement in your project, bundled log files.
     *
     * @param zippedFile zipped file
     *//*
    public void upload(File zippedFile) {
        throw new UnsupportedOperationException();
    }


    *//**
     * Packages that developer concern, by default it uses process name,
     * put high priority one in pre-order.
     *
     * @return null if simply concern only package with process name.
     *//*
    public List<String> concernPackages() {
        return null;
    }

    *//**
     * Filter stack without any in concern package, used with @{code concernPackages}.
     *
     * @return true if filter, false it not.
     *//*
    public boolean filterNonConcernStack() {
        return false;
    }

    *//**
     * Provide white list, entry in white list will not be shown in ui list.
     *
     * @return return null if you don't need white-list filter.
     *//*
    public List<String> provideWhiteList() {
        LinkedList<String> whiteList = new LinkedList<>();
        whiteList.add("org.chromium");
        return whiteList;
    }

    *//**
     * Whether to delete files whose stack is in white list, used with white-list.
     *
     * @return true if delete, false it not.
     *//*
    public boolean deleteFilesInWhiteList() {
        return true;
    }

    *//**
     * Block interceptor, developer may provide their own actions.
     *//*
  *//*  public void onBlock(Context context, BlockInfo blockInfo) {

    }*//*
}
*/