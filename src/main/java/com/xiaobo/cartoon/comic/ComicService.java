package com.xiaobo.cartoon.comic;

import java.util.List;

import com.xiaobo.cartoon.volume.VolumeRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComicService {

	private final ComicRepository comicRepository;
	private final VolumeRepository volumeRepository;

	public Comic insert(Comic comic) {
		return comicRepository.save(comic);
	}

	public List<Comic> findAll() {
		return comicRepository.findAll();
	}

	public void deleteById(String id) {
		comicRepository.deleteById(id);
	}

	public Comic updateComic(Comic comic) {
		// delete all volume under this comic
		volumeRepository.deleteAllByComicId(new ObjectId(comic.getId()));
		
		return comicRepository.save(comic);
	}
}
