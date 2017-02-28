package com.boluo.notification.push;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.boluo.dao.EntityDao;
import com.boluo.dao.mapper.EntityRowMapper;
import com.boluo.model.Entity;
import com.boluo.task.TaskScheduler;

import cn.jpush.api.push.PushResult;

/**
 * @author zzw
 * @since Jun 30, 2016
 */
@Service
public class PushTaskTopicNews extends AbstractPushTask {
	
	public PushTaskTopicNews(EntityDao entityDao) {
	    this.entityDao = entityDao;
	  }

  public PushTaskTopicNews() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 23, 30);
  }
  
  @Override
  protected String getPushType(){
	  return "topic";
  }

  @Override
  public int getBusinessType() {
    return 4;
  }
  
  protected List<String> getRegistrationIds(Entity entity){
	List<String> ids = new ArrayList<String>();
	Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("topicId", entity.getLong("objectId"));
    condition.put("status", 1);
    List<Entity> entities = entityDao.find("topic_follow", condition, EntityRowMapper.getInstance());
    
      if (entities != null) {
	  	for (Entity topicFowllow : entities) {
	  		String fromUser = entity.getString("fromUserId");
			if (StringUtils.isEmpty(fromUser) || !topicFowllow.getString("userId").equals(fromUser)) {
				
	  			Map<String, Object> conditionTemp = new HashMap<String, Object>();
	  			conditionTemp.put("userId", topicFowllow.getString("userId"));
	  			conditionTemp.put("status", 1);
	  		    Entity userDevice = entityDao.findOne("user_device", conditionTemp);

	  		    if (userDevice != null) {
	  		      ids.add(userDevice.getString("registrationId"));
	  		    }
	  			
			}
	  	}
	  }
	return ids;  
  }

  @Override
  public String getPushContent(Entity entity) throws Exception {
    
    return entity.getString("content");
  }

  @Override
  public String getTaskTableName() {
    return "notification_user_text";
  }
  
  @Override
  protected PushResult sendPush(List<String> registrationId, String content, int type, long dataId, Boolean apnsProduction) throws Exception {
	  return super.sendPush(registrationId, content, type, dataId, true);
  }
  

}
