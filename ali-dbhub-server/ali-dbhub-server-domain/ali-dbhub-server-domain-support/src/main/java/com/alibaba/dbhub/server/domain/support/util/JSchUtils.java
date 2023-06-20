///**
// * alibaba.com Inc.
// * Copyright (c) 2004-2023 All Rights Reserved.
// */
//package com.alibaba.dbhub.server.domain.support.util;
//
//import com.alibaba.dbhub.server.domain.support.model.SSHInfo;
//
//import com.jcraft.jsch.JSch;
//import com.jcraft.jsch.JSchException;
//import com.jcraft.jsch.Session;
//
///**
// * @author jipengfei
// * @version : JSchUtil.java
// */
//public class JSchUtils {
//
//    public static Session getSession(SSHInfo ssh, String host, String port) throws JSchException {
//        JSch jSch = new JSch();
//        Session session = jSch.getSession(ssh.getUserName(), ssh.getHostName(),
//            Integer.parseInt(ssh.getPort()));
//        session.setPassword(ssh.getPassword());
//        session.setConfig("StrictHostKeyChecking", "no");
//        session.connect();
//        int port1 = session.setPortForwardingL(Integer.parseInt(ssh.getLocalPort()), host,
//            Integer.parseInt(port));
//        return session;
//    }
//
//    public static Session getSession(SSHInfo ssh) throws JSchException {
//        JSch jSch = new JSch();
//        Session session = jSch.getSession(ssh.getUserName(), ssh.getHostName(),
//            Integer.parseInt(ssh.getPort()));
//        session.setPassword(ssh.getPassword());
//        session.setConfig("StrictHostKeyChecking", "no");
//        session.connect();
//        return session;
//    }
//}