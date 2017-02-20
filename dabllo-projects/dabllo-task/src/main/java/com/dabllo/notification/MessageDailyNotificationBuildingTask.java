package com.dabllo.notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.dabllo.Constants;
import com.dabllo.dao.BaseDao;
import com.dabllo.dao.EntityDao;
import com.dabllo.dao.mapper.ItemRowMapper;
import com.dabllo.model.Entity;
import com.dabllo.model.Item;
import com.dabllo.task.TaskScheduler;
import com.dabllo.util.Pair;

/**
 * @author zzw
 * @since Aug 17, 2016
 * 找到每天24小时最热和热议，在7pm写入数据库等待通知
 */
@Service
public class MessageDailyNotificationBuildingTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(MessageDailyNotificationBuildingTask.class);

  @Resource
  private EntityDao entityDao;
  
  public static final long MINUTE = 60 * 1000L;
  public static final long HOUR = 60 * MINUTE;
  public static final long DAY = 24 * HOUR;

  public MessageDailyNotificationBuildingTask() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 47, 60);
  }

  public MessageDailyNotificationBuildingTask(EntityDao entityDao) {
    this.entityDao = entityDao;
  }

  @Override
  public void run() {
    try {
    	long time = System.currentTimeMillis();
    	Date date = new Date(time);
    	
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date theDay = null;
        try {
          theDay = dateFormat.parse(dateFormat.format(date));

        } catch (Throwable t) {
          LOG.error("Error occurs on dateFormat message daily notification building task!", t);
          return;
        }

        long begin = theDay.getTime() + 18 * HOUR + 30 * MINUTE;
        long end = begin + 1 * MINUTE;
        
        //TODO 线上版本把return打开
        if (time < begin || time >= end) {
        	if (new java.util.Random().nextInt(100) == 66) {
        		LOG.info("begin " + begin + " " + "end " + end + " " +  "now" + time);
			}
        		//System.out.println("begin " + begin + " " + "end " + end + " " +  "now" + time);
        	return;
		} else {
			LOG.info("send daily push notice : begin " + begin + " " + "end " + end + " " +  "now" + time);
		}

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("status", Constants.STATUS_ENABLED);

        Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
        //并不需要24小时最热 昨天6点之后的新闻如果变成最热也放弃掉，太老了 取今天最热，time - 24*HOUR
        offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), theDay.getTime());
        offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_DESC), time);
        //只取一条
        List<Item> rankedItems = entityDao.findByOffset("item", condition, offsets, "rank", BaseDao.ORDER_OPTION_DESC, 1, 1, ItemRowMapper.getInstance());

        condition.put("hot", 1);
        //拿两条，如果24小时最热也是热议，选择另一条作为热议
        List<Item> hotItems = entityDao.findByOffset("item", condition, offsets, "rank", BaseDao.ORDER_OPTION_DESC, 1, 2, ItemRowMapper.getInstance());
        
        StringBuffer noticeMessageBuffer = new StringBuffer();
        if (rankedItems != null) {
        	noticeMessageBuffer.append("24小时最热：");
        	noticeMessageBuffer.append(rankedItems.get(0).getTitle());
        	noticeMessageBuffer.append("  ");
		}
        
        if (hotItems != null) {
			for (Item item : hotItems) {
				if (rankedItems == null || item.getId() != rankedItems.get(0).getId()) {
					noticeMessageBuffer.append("今日热议：");
					noticeMessageBuffer.append(item.getTitle());
					break;
				}
			}
		}
        String noticeMessage = noticeMessageBuffer.toString();
		if (StringUtils.isEmpty(noticeMessage)) {
			return;
		}
        
        Entity entity = new Entity("notification_text");
        entity.set("type", 10000);
        entity.set("content", noticeMessage);
        entity.set("status", 0).set("createTime", time);
        entityDao.save(entity);
        
        //request.setAttribute("date", date);
        //return Response.ok(new Viewable("index")).build();

    } catch (Throwable t) {
      LOG.error("Error occurs on processing message notification building task!", t);
    }
  }

}
