package com.xiaobo.cartoon.volume;

import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Volume {

	public static final Comparator<Volume> ORDER_RULE = Comparator.comparing(Volume::getFileName);

	@Id
	private String id;
	private String fileName;
	private String path;
	private long pageCount;
	private Integer currentPage;
	private String nextId;
	private String previousId;
	private byte[] cover;

	@JsonIgnore
	@Indexed
	private ObjectId comicId;

	private boolean read;

	private List<String> scannerErrors;

	public Volume(Integer currentPage) {
		this.currentPage = currentPage;
	}
}
