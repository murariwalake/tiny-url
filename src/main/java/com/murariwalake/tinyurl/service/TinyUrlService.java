package com.murariwalake.tinyurl.service;

import java.util.List;
import java.util.Map;

import com.murariwalake.tinyurl.model.TinyUrlEntity;

public interface TinyUrlService {
	String createTinyUrl(String longUrl);

	String getLongUrl(String tinyUrl);

	List<TinyUrlEntity> getAllTinyUrls();

	void deleteTinyUrl(String tinyUrl);
}
