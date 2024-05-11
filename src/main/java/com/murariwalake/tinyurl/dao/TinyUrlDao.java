package com.murariwalake.tinyurl.dao;

import com.murariwalake.tinyurl.model.TinyUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TinyUrlDao extends JpaRepository<TinyUrlEntity, Long> {
	TinyUrlEntity findByTinyUrl(String tinyUrl);

	TinyUrlEntity findByLongUrl(String longUrl);
}
