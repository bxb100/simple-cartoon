package com.xiaobo.cartoon.scanner;

import java.util.Map;

import lombok.Data;

@Data
public class ReplaceFileNameReq {

	private String pattern;

	private String replacement;

	private Map<String, String> idMapName;
}
