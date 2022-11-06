package com.xiaobo.cartoon.scanner;

import java.util.List;
import java.util.Map;

public record ReplaceFileNameReq(String pattern, String replacement, List<String> effectIds,
								 Map<String, String> idMapName) {
}
