package com.boluo.notification.push;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.boluo.dao.EntityDao;
import com.boluo.dao.mapper.DiscussionRowMapper;
import com.boluo.dao.mapper.EntityRowMapper;
import com.boluo.model.Discussion;
import com.boluo.model.Entity;
import com.boluo.task.TaskScheduler;

/**
 * @author zzw
 * @since Jun 30, 2016
 */
@Service
public class PushTaskReplySave extends AbstractPushTask {
	
	public PushTaskReplySave(EntityDao entityDao) {
	    this.entityDao = entityDao;
	  }

  public PushTaskReplySave() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 23, 30);
  }
  
  @Override
  protected String getPushType(){
	  return "replySave";
  }

  @Override
  public int getBusinessType() {
    return 4;
  }
  
  protected List<String> getRegistrationIds(Entity entity){
	List<String> ids = new ArrayList<String>();
	Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("discussionId", entity.getLong("objectId"));
    condition.put("status", 1);
    List<Entity> entities = entityDao.find("discussion_follow", condition, EntityRowMapper.getInstance());
    
      if (entities != null) {
	  	for (Entity discussion : entities) {
	  		String fromUser = entity.getString("fromUserId");
			if (StringUtils.isEmpty(fromUser) || !discussion.getString("userId").equals(fromUser)) {
				
	  			Map<String, Object> conditionTemp = new HashMap<String, Object>();
	  			conditionTemp.put("userId", discussion.getString("userId"));
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
    
    Discussion discussion = entityDao.findOne("discussion", "id", entity.getLong("objectId"), DiscussionRowMapper.getInstance());
    StringBuilder sb = new StringBuilder();
    sb.append("你关注的讨论： \"");
    sb.append(discussion.getTitle());
    sb.append("\" 有了新观点 ：\"");
    sb.append(entity.getString("content"));
    sb.append("\"");
    return sb.toString();
  }

  @Override
  public String getTaskTableName() {
    return "notification_user_text";
  }

}
