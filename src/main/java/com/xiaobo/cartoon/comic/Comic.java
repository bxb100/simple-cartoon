package com.xiaobo.cartoon.comic;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Comic {

	@Id
	private String id;

	@NonNull
	@Indexed(unique = true, background = true)
	private String caption;

	@NonNull
	private String path;

	private Integer volNum;

	private boolean scanned;
}
