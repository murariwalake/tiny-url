package com.murariwalake.tinyurl.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tiny_url")
public class TinyUrlEntity {
	@Id
	private String tinyUrl;

	private String longUrl;

	private Date createdDate;
}
