package com.xiaobo.cartoon.comic;

import java.util.Dictionary;
import java.util.List;

import com.xiaobo.cartoon.volume.Volume;
import com.xiaobo.cartoon.volume.VolumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/comic")
@RequiredArgsConstructor
public class ComicController {

	private final ComicService comicService;
	private final VolumeService volumeService;

	@GetMapping
	public List<Comic> findAll() {

		return comicService.findAll();
	}

	@PostMapping
	public Comic insert(@RequestBody Comic comic) {
		return comicService.insert(comic);
	}

	@DeleteMapping("{id}")
	public void delete(@PathVariable String id) {
		comicService.deleteById(id);
	}

	@PutMapping
	public Comic update(@RequestBody Comic comic) {

		return comicService.updateComic(comic);
	}

	@GetMapping("{id}/volume")
	public List<Volume> findAllVolumes(@PathVariable String id) {
		return volumeService.findByComicId(id);
	}
}
