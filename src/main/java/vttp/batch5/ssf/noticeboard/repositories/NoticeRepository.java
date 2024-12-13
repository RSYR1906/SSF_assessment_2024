package vttp.batch5.ssf.noticeboard.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NoticeRepository {

	@Autowired
	RedisTemplate<String, Object> template;

	// TODO: Task 4
	// You can change the signature of this method by adding any number of
	// parameters
	// and return any type
	//
	/*
	 * Write the redis-cli command that you use in this method in the comment.
	 * For example if this method deletes a field from a hash, then write the
	 * following
	 * redis-cli command
	 * hdel myhashmap a_key
	 * set uniqueId payload
	 * 
	 */
	public void insertNotices(String uniqueId, String payload) {
		// Save the JSON string into Redis with the unique ID as the key
		template.opsForValue().set(uniqueId, payload);
		System.out.println("Payload saved to Redis with key: " + uniqueId);
	}
}
