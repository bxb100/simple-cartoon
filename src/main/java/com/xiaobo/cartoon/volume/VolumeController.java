package com.xiaobo.cartoon.volume;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("api/volume")
@RequiredArgsConstructor
public class VolumeController {

	private final VolumeService volumeService;

	@PostMapping("{id}/page/{currentPage}")
	public void markRead(@PathVariable String id, @PathVariable Integer currentPage) {
		volumeService.markRead(id, currentPage);
	}

	@GetMapping("{id}")
	public Volume getVolume(@PathVariable String id) {
		return volumeService.getById(id);
	}

	@GetMapping("stream/{id}")
	public ResponseEntity<StreamingResponseBody> getVolumeStream(@PathVariable String id) throws IOException {
		return volumeService.read(id);
	}

	@DeleteMapping("{id}")
	public void delete(@PathVariable String id) {
		volumeService.deleteById(id);
	}
}
