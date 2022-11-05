package com.xiaobo.cartoon.comic;

import java.util.List;

import com.xiaobo.cartoon.volume.VolumeRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ComicService {

	private final ComicRepository comicRepository;
	private final VolumeRepository volumeRepository;

	public Comic insert(Comic comic) {
		return comicRepository.save(comic);
	}

	public List<Comic> findAll() {
		return comicRepository.findAllByOrderByTitleAsc();
	}

	public void deleteById(String id) {
		// delete all volume under this comic
		volumeRepository.deleteAllByComicId(new ObjectId(id));

		comicRepository.deleteById(id);
	}

	@Transactional
	public Comic updateComic(String id, Comic comic) {
		Comic save = comicRepository.findById(id).orElseThrow();
		save.setTitle(comic.getTitle());
		if (!save.getPath().equals(comic.getPath())) {

			// delete all volume under this comic
			volumeRepository.deleteAllByComicId(new ObjectId(id));
		}
		save.setPath(comic.getPath());

		return comicRepository.save(save);
	}
}
