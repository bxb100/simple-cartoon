package com.xiaobo.cartoon.scanner;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/scanner", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class ScannerController {

	private final ScannerService scannerService;
	private final ReplaceNameService replaceNameService;

	@GetMapping("{comicId}")
	public int startScan(@PathVariable String comicId) {
		return scannerService.scan(comicId);
	}

	@PostMapping("{comicId}/rename")
	public Map<String, String> renameVolume(
			@PathVariable String comicId,
			@RequestBody ReplaceFileNameReq req,
			@RequestParam(value = "show", defaultValue = "true") boolean show
	) {

		return replaceNameService.replaceVolumeName(
				comicId, req, show
		);
	}
}
