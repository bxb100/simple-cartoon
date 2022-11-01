package com.xiaobo.cartoon.comic;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Comic {

	@Id
	private String id;

	@NonNull
	private String title;

	@NonNull
	private String path;
}
