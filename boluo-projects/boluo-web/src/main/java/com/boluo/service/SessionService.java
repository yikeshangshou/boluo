package com.boluo.service;

import com.boluo.model.User;

/**
 * @author mixueqiang
 * @since Jul 1, 2016
 */
public interface SessionService {

  void destorySession(String sessionId);

  User signinWithCookie(String sessionId);

  User signinWithDeviceId(String deviceId);

  void storeSession(long userId, String sessionId);

}
