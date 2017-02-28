package com.boluo.service;

/**
 * @author mixueqiang
 * @since Aug 12, 2016
 */
public interface AnonymousService {

  int countReplyUp(String ip, long replyId);

  void upReply(String ip, long replyId);

}
