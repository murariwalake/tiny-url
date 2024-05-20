package com.murariwalake.tinyurl.service;

import com.murariwalake.tinyurl.dao.TinyUrlDao;
import com.murariwalake.tinyurl.model.TinyUrlEntity;
import com.murariwalake.tinyurl.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TinyUrlServiceImpl implements TinyUrlService {

	private final TinyUrlDao tinyUrlDao;
	private final CounterService counterService;

	@Override
	public String createTinyUrl(String longUrl) {
		TinyUrlEntity existingTinyUrlEntity = tinyUrlDao.findByLongUrl(longUrl);
		if (existingTinyUrlEntity != null) {
			log.info("Long URL already exists: {}", longUrl);
			return existingTinyUrlEntity.getTinyUrl();
		}

		TinyUrlEntity newTinyUrlEntity = TinyUrlEntity.builder()
				.longUrl(longUrl)
				.tinyUrl(Util.decimalToBase62(counterService.getNextCount()))
				.createdDate(new Date())
				.build();

		tinyUrlDao.save(newTinyUrlEntity);
		log.info("Tiny URL created: {} for long URL: {}", newTinyUrlEntity.getTinyUrl(), longUrl);
		return newTinyUrlEntity.getTinyUrl();
	}

	@Override
	@Cacheable(cacheNames = "longUrl", key = "#tinyUrl")
	public String getLongUrl(String tinyUrl) {
		TinyUrlEntity tinyUrlEntity = tinyUrlDao.findByTinyUrl(tinyUrl);
		if (tinyUrlEntity != null) {
			log.info("Long URL found for tiny URL: {}", tinyUrl);
			return tinyUrlEntity.getLongUrl();
		}

		log.error("Long URL not found for tiny URL: {}", tinyUrl);
		throw new RuntimeException("Long URL not found for tiny URL: " + tinyUrl);
	}

	@Override
	public List<TinyUrlEntity> getAllTinyUrls() {
		List<TinyUrlEntity> tinyUrls = tinyUrlDao.findAll();
		log.info("Retrieved all tiny URLs. Count: {}", tinyUrls.size());
		return tinyUrls;
	}

	@Override
	@CacheEvict(cacheNames = "longUrl", key = "#tinyUrl", beforeInvocation = true)
	public void deleteTinyUrl(String tinyUrl) {
		TinyUrlEntity tinyUrlEntity = tinyUrlDao.findByTinyUrl(tinyUrl);
		if (tinyUrlEntity != null) {
			tinyUrlDao.delete(tinyUrlEntity);
			log.info("Deleted tiny URL: {}", tinyUrl);
			return;
		}

		log.error("Tiny URL not found for deletion: {}", tinyUrl);
		throw new RuntimeException("Tiny URL not found for deletion: " + tinyUrl);
	}
}
